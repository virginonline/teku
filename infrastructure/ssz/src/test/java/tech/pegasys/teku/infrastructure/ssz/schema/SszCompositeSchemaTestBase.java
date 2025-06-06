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

package tech.pegasys.teku.infrastructure.ssz.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class SszCompositeSchemaTestBase extends SszSchemaTestBase {

  @MethodSource("testSchemaArguments")
  @ParameterizedTest
  void isPrimitive_shouldReturnFalse(final SszCompositeSchema<?> schema) {
    assertThat(schema.isPrimitive()).isFalse();
  }

  @MethodSource("testSchemaArguments")
  @ParameterizedTest
  void getChildSchema_shouldThrowIndexOutOfBounds(final SszCompositeSchema<?> schema) {
    Assumptions.assumeThat(schema.getMaxLength()).isLessThan(Integer.MAX_VALUE);
    assertThatThrownBy(() -> schema.getChildSchema((int) schema.getMaxLength()))
        .isInstanceOf(IndexOutOfBoundsException.class);
  }
}
