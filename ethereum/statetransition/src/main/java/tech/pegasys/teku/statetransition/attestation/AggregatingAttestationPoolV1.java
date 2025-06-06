/*
 * Copyright Consensys Software Inc., 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.statetransition.attestation;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.plugin.services.MetricsSystem;
import tech.pegasys.teku.infrastructure.metrics.SettableGauge;
import tech.pegasys.teku.infrastructure.metrics.TekuMetricCategory;
import tech.pegasys.teku.infrastructure.ssz.SszList;
import tech.pegasys.teku.infrastructure.ssz.schema.SszListSchema;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.datastructures.attestation.ValidatableAttestation;
import tech.pegasys.teku.spec.datastructures.operations.Attestation;
import tech.pegasys.teku.spec.datastructures.operations.AttestationData;
import tech.pegasys.teku.spec.datastructures.operations.AttestationSchema;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.BeaconState;
import tech.pegasys.teku.spec.schemas.SchemaDefinitions;
import tech.pegasys.teku.statetransition.attestation.utils.AggregatingAttestationPoolProfiler;
import tech.pegasys.teku.storage.client.RecentChainData;

/**
 * Maintains a pool of attestations. Attestations can be retrieved either for inclusion in a block
 * or as an aggregate to publish as part of the naive attestation aggregation algorithm. In both
 * cases the returned attestations are aggregated to maximise the number of validators that can be
 * included.
 */
public class AggregatingAttestationPoolV1 extends AggregatingAttestationPool {
  private static final Logger LOG = LogManager.getLogger();

  static final Comparator<PooledAttestationWithData> ATTESTATION_INCLUSION_COMPARATOR =
      Comparator.<PooledAttestationWithData>comparingInt(
              attestation -> attestation.pooledAttestation().bits().getBitCount())
          .reversed();

  private final Map<Bytes, MatchingDataAttestationGroup> attestationGroupByDataHash =
      new HashMap<>();
  private final NavigableMap<UInt64, Set<Bytes>> dataHashBySlot = new TreeMap<>();

  private final SettableGauge sizeGauge;
  private final int maximumAttestationCount;

  private final AggregatingAttestationPoolProfiler aggregatingAttestationPoolProfiler;

  private final AtomicInteger size = new AtomicInteger(0);

  public AggregatingAttestationPoolV1(
      final Spec spec,
      final RecentChainData recentChainData,
      final MetricsSystem metricsSystem,
      final AggregatingAttestationPoolProfiler aggregatingAttestationPoolProfiler,
      final int maximumAttestationCount) {
    super(spec, recentChainData);
    this.sizeGauge =
        SettableGauge.create(
            metricsSystem,
            TekuMetricCategory.BEACON,
            "attestation_pool_size",
            "The number of attestations available to be included in proposed blocks");
    this.maximumAttestationCount = maximumAttestationCount;
    this.aggregatingAttestationPoolProfiler = aggregatingAttestationPoolProfiler;
  }

  @Override
  public synchronized void add(final ValidatableAttestation attestation) {
    if (!ensureCommitteesSizeInAttestation(attestation)) {
      LOG.debug(
          "Attestation at slot {}, block root {} and target root {} has no committee size. Will NOT add this attestation to the pool.",
          attestation.getData().getSlot(),
          attestation.getData().getBeaconBlockRoot(),
          attestation.getData().getTarget().getRoot());
      return;
    }

    getOrCreateAttestationGroup(attestation.getData(), attestation.getCommitteesSize())
        .ifPresent(
            attestationGroup -> {
              final boolean added =
                  attestationGroup.add(
                      PooledAttestation.fromValidatableAttestation(attestation),
                      attestation.getCommitteeShufflingSeed());
              if (added) {
                updateSize(1);
              }
            });
    // Always keep the latest slot attestations, so we don't discard everything
    int currentSize = getSize();
    while (dataHashBySlot.size() > 1 && currentSize > maximumAttestationCount) {
      LOG.trace("Attestation cache at {} exceeds {}, ", currentSize, maximumAttestationCount);
      final UInt64 firstSlotToKeep = dataHashBySlot.firstKey().plus(1);
      removeAttestationsPriorToSlot(firstSlotToKeep);
      currentSize = getSize();
    }
  }

  /**
   * @param committeesSize Required for aggregating attestations as per <a
   *     href="https://eips.ethereum.org/EIPS/eip-7549">EIP-7549</a>
   */
  private Optional<MatchingDataAttestationGroup> getOrCreateAttestationGroup(
      final AttestationData attestationData, final Optional<Int2IntMap> committeesSize) {
    dataHashBySlot
        .computeIfAbsent(attestationData.getSlot(), slot -> new HashSet<>())
        .add(attestationData.hashTreeRoot());
    final MatchingDataAttestationGroup attestationGroup =
        attestationGroupByDataHash.computeIfAbsent(
            attestationData.hashTreeRoot(),
            key -> new MatchingDataAttestationGroup(spec, attestationData, committeesSize));
    return Optional.of(attestationGroup);
  }

  @Override
  public synchronized void onSlot(final UInt64 slot) {
    if (slot.compareTo(ATTESTATION_RETENTION_SLOTS) <= 0) {
      return;
    }
    final UInt64 firstValidAttestationSlot = slot.minus(ATTESTATION_RETENTION_SLOTS);
    removeAttestationsPriorToSlot(firstValidAttestationSlot);

    aggregatingAttestationPoolProfiler.execute(spec, slot, recentChainData, this);
  }

  private void removeAttestationsPriorToSlot(final UInt64 firstValidAttestationSlot) {
    final Collection<Set<Bytes>> dataHashesToRemove =
        dataHashBySlot.headMap(firstValidAttestationSlot, false).values();
    dataHashesToRemove.stream()
        .flatMap(Set::stream)
        .forEach(
            key -> {
              final int removed = attestationGroupByDataHash.get(key).size();
              attestationGroupByDataHash.remove(key);
              updateSize(-removed);
            });
    if (!dataHashesToRemove.isEmpty()) {
      LOG.trace(
          "firstValidAttestationSlot: {}, removing: {}",
          () -> firstValidAttestationSlot,
          dataHashesToRemove::size);
    }
    dataHashesToRemove.clear();
  }

  @Override
  public synchronized void onAttestationsIncludedInBlock(
      final UInt64 slot, final Iterable<Attestation> attestations) {
    attestations.forEach(attestation -> onAttestationIncludedInBlock(slot, attestation));
  }

  private void onAttestationIncludedInBlock(final UInt64 slot, final Attestation attestation) {
    final ValidatableAttestation validatableAttestation =
        ValidatableAttestation.from(spec, attestation);
    if (!ensureCommitteesSizeInAttestation(validatableAttestation)) {
      LOG.debug(
          "Attestation at slot {}, block root {} and target root {} has no committee size. Unable to call onAttestationIncludedInBlock.",
          attestation.getData().getSlot(),
          attestation.getData().getBeaconBlockRoot(),
          attestation.getData().getTarget().getRoot());
      return;
    }
    getOrCreateAttestationGroup(attestation.getData(), validatableAttestation.getCommitteesSize())
        .ifPresent(
            attestationGroup -> {
              final int numRemoved =
                  attestationGroup.onAttestationIncludedInBlock(slot, attestation);
              updateSize(-numRemoved);
            });
  }

  private void updateSize(final int delta) {
    final int currentSize = size.addAndGet(delta);
    sizeGauge.set(currentSize);
  }

  @Override
  public synchronized int getSize() {
    return size.get();
  }

  @Override
  public synchronized SszList<Attestation> getAttestationsForBlock(
      final BeaconState stateAtBlockSlot, final AttestationForkChecker forkChecker) {
    final UInt64 currentEpoch = spec.getCurrentEpoch(stateAtBlockSlot);
    final int previousEpochLimit = spec.getPreviousEpochAttestationCapacity(stateAtBlockSlot);

    final SchemaDefinitions schemaDefinitions =
        spec.atSlot(stateAtBlockSlot.getSlot()).getSchemaDefinitions();
    final AttestationSchema<Attestation> attestationSchema =
        schemaDefinitions.getAttestationSchema();
    final SszListSchema<Attestation, ?> attestationsSchema =
        schemaDefinitions.getBeaconBlockBodySchema().getAttestationsSchema();

    final boolean blockRequiresAttestationsWithCommitteeBits =
        attestationSchema.requiresCommitteeBits();

    final AtomicInteger prevEpochCount = new AtomicInteger(0);

    return dataHashBySlot
        // We can immediately skip any attestations from the block slot or later
        .headMap(stateAtBlockSlot.getSlot(), false)
        .descendingMap()
        .values()
        .stream()
        .flatMap(
            dataHashSetForSlot ->
                streamAggregatesForDataHashesBySlot(
                    dataHashSetForSlot,
                    stateAtBlockSlot,
                    forkChecker,
                    blockRequiresAttestationsWithCommitteeBits))
        .limit(attestationsSchema.getMaxLength())
        .filter(
            attestation -> {
              if (spec.computeEpochAtSlot(attestation.data().getSlot()).isLessThan(currentEpoch)) {
                final int currentCount = prevEpochCount.getAndIncrement();
                return currentCount < previousEpochLimit;
              }
              return true;
            })
        .map(pooledAttestation -> pooledAttestation.toAttestation(attestationSchema))
        .collect(attestationsSchema.collector());
  }

  private Stream<PooledAttestationWithData> streamAggregatesForDataHashesBySlot(
      final Set<Bytes> dataHashSetForSlot,
      final BeaconState stateAtBlockSlot,
      final AttestationForkChecker forkChecker,
      final boolean blockRequiresAttestationsWithCommitteeBits) {

    return dataHashSetForSlot.stream()
        .map(attestationGroupByDataHash::get)
        .filter(Objects::nonNull)
        .filter(group -> isValid(stateAtBlockSlot, group.getAttestationData()))
        .filter(forkChecker::areAttestationsFromCorrectFork)
        .flatMap(MatchingDataAttestationGroup::stream)
        .filter(
            attestation ->
                attestation.pooledAttestation().bits().requiresCommitteeBits()
                    == blockRequiresAttestationsWithCommitteeBits)
        .sorted(ATTESTATION_INCLUSION_COMPARATOR);
  }

  @Override
  public synchronized List<Attestation> getAttestations(
      final Optional<UInt64> maybeSlot, final Optional<UInt64> maybeCommitteeIndex) {

    final Predicate<Map.Entry<UInt64, Set<Bytes>>> filterForSlot =
        (entry) -> maybeSlot.map(slot -> entry.getKey().equals(slot)).orElse(true);

    final UInt64 slot = maybeSlot.orElse(recentChainData.getCurrentSlot().orElse(UInt64.ZERO));
    final SchemaDefinitions schemaDefinitions = spec.atSlot(slot).getSchemaDefinitions();
    final AttestationSchema<Attestation> attestationSchema =
        schemaDefinitions.getAttestationSchema();

    final boolean requiresCommitteeBits =
        schemaDefinitions.getAttestationSchema().requiresCommitteeBits();

    return dataHashBySlot.descendingMap().entrySet().stream()
        .filter(filterForSlot)
        .map(Map.Entry::getValue)
        .flatMap(Collection::stream)
        .map(attestationGroupByDataHash::get)
        .filter(Objects::nonNull)
        .flatMap(
            matchingDataAttestationGroup ->
                matchingDataAttestationGroup.stream(maybeCommitteeIndex, requiresCommitteeBits))
        .map(pooledAttestation -> pooledAttestation.toAttestation(attestationSchema))
        .toList();
  }

  private boolean isValid(
      final BeaconState stateAtBlockSlot, final AttestationData attestationData) {
    return spec.validateAttestation(stateAtBlockSlot, attestationData).isEmpty();
  }

  @Override
  public synchronized Optional<Attestation> createAggregateFor(
      final Bytes32 attestationHashTreeRoot, final Optional<UInt64> committeeIndex) {
    final MatchingDataAttestationGroup group =
        attestationGroupByDataHash.get(attestationHashTreeRoot);
    if (group == null) {
      return Optional.empty();
    }

    final AttestationSchema<Attestation> attestationSchema =
        spec.atSlot(group.getAttestationData().getSlot())
            .getSchemaDefinitions()
            .getAttestationSchema();

    return group.stream(committeeIndex)
        .findFirst()
        .map(pooledAttestation -> pooledAttestation.toAttestation(attestationSchema));
  }

  @Override
  public synchronized void onReorg(final UInt64 commonAncestorSlot) {
    attestationGroupByDataHash.values().forEach(group -> group.onReorg(commonAncestorSlot));
  }
}
