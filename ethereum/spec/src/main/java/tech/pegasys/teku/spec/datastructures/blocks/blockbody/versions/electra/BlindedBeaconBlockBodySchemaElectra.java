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

package tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.electra;

import static com.google.common.base.Preconditions.checkArgument;

import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBodySchema;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.deneb.BlindedBeaconBlockBodySchemaDeneb;
import tech.pegasys.teku.spec.datastructures.execution.versions.electra.ExecutionRequestsSchema;

public interface BlindedBeaconBlockBodySchemaElectra<T extends BlindedBeaconBlockBodyElectra>
    extends BlindedBeaconBlockBodySchemaDeneb<T> {

  ExecutionRequestsSchema getExecutionRequestsSchema();

  static BlindedBeaconBlockBodySchemaElectra<?> required(final BeaconBlockBodySchema<?> schema) {
    checkArgument(
        schema instanceof BlindedBeaconBlockBodySchemaElectra,
        "Expected a BlindedBeaconBlockBodySchemaElectra but was %s",
        schema.getClass());
    return (BlindedBeaconBlockBodySchemaElectra<?>) schema;
  }
}
