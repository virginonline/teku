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

package tech.pegasys.teku.spec.propertytest.suppliers.state;

import tech.pegasys.teku.spec.SpecMilestone;
import tech.pegasys.teku.spec.datastructures.state.versions.electra.PendingBalanceDeposit;
import tech.pegasys.teku.spec.propertytest.suppliers.DataStructureUtilSupplier;
import tech.pegasys.teku.spec.util.DataStructureUtil;

public class PendingBalanceDepositSupplier
    extends DataStructureUtilSupplier<PendingBalanceDeposit> {
  public PendingBalanceDepositSupplier() {
    super(DataStructureUtil::randomPendingBalanceDeposit, SpecMilestone.ELECTRA);
  }
}