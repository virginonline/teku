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

package tech.pegasys.teku.ethereum.json.types.beacon;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.bytes.Bytes4;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public class GetGenesisApiData {
  private final UInt64 genesisTime;
  private final Bytes32 genesisValidatorsRoot;
  private final Bytes4 genesisForkVersion;

  public GetGenesisApiData(
      final UInt64 genesisTime,
      final Bytes32 genesisValidatorsRoot,
      final Bytes4 genesisForkVersion) {
    this.genesisTime = genesisTime;
    this.genesisValidatorsRoot = genesisValidatorsRoot;
    this.genesisForkVersion = genesisForkVersion;
  }

  public UInt64 getGenesisTime() {
    return genesisTime;
  }

  public Bytes32 getGenesisValidatorsRoot() {
    return genesisValidatorsRoot;
  }

  public Bytes4 getGenesisForkVersion() {
    return genesisForkVersion;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final GetGenesisApiData that = (GetGenesisApiData) o;
    return Objects.equals(genesisTime, that.genesisTime)
        && Objects.equals(genesisValidatorsRoot, that.genesisValidatorsRoot)
        && Objects.equals(genesisForkVersion, that.genesisForkVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(genesisTime, genesisValidatorsRoot, genesisForkVersion);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("genesisTime", genesisTime)
        .add("genesisValidatorsRoot", genesisValidatorsRoot)
        .add("genesisForkVersion", genesisForkVersion)
        .toString();
  }
}
