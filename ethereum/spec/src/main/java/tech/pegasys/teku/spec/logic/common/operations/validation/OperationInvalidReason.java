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

package tech.pegasys.teku.spec.logic.common.operations.validation;

import com.google.errorprone.annotations.CheckReturnValue;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface OperationInvalidReason {
  String describe();

  @SafeVarargs
  static Optional<OperationInvalidReason> firstOf(
      final Supplier<Optional<OperationInvalidReason>>... checks) {
    return Stream.of(checks)
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  @CheckReturnValue
  static Optional<OperationInvalidReason> check(
      final boolean isValid, final OperationInvalidReason check) {
    return !isValid ? Optional.of(check) : Optional.empty();
  }
}
