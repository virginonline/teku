/*
 * Copyright Consensys Software Inc., 2024
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

package tech.pegasys.teku.statetransition.datacolumns;

import java.util.function.Supplier;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.kzg.KZG;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.datastructures.blocks.SignedBeaconBlock;
import tech.pegasys.teku.spec.logic.common.statetransition.availability.AvailabilityChecker;
import tech.pegasys.teku.spec.logic.common.statetransition.availability.AvailabilityCheckerFactory;
import tech.pegasys.teku.statetransition.forkchoice.DataColumnSidecarAvailabilityChecker;

public class DasSamplerManager implements AvailabilityCheckerFactory<UInt64> {
  public static final AvailabilityCheckerFactory<UInt64> NOOP =
      block -> AvailabilityChecker.NOOP_DATACOLUMN_SIDECAR;
  private final Supplier<DataAvailabilitySampler> dataAvailabilitySamplerSupplier;
  final KZG kzg;
  final Spec spec;

  public DasSamplerManager(
      final Supplier<DataAvailabilitySampler> dataAvailabilitySamplerSupplier,
      final KZG kzg,
      final Spec spec) {
    this.dataAvailabilitySamplerSupplier = dataAvailabilitySamplerSupplier;
    this.kzg = kzg;
    this.spec = spec;
  }

  @Override
  public DataColumnSidecarAvailabilityChecker createAvailabilityChecker(
      final SignedBeaconBlock block) {
    return new DataColumnSidecarAvailabilityChecker(
        dataAvailabilitySamplerSupplier.get(), kzg, spec, block);
  }
}
