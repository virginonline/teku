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

package tech.pegasys.teku.infrastructure.ssz.containers;

import java.util.List;
import java.util.function.BiFunction;
import tech.pegasys.teku.infrastructure.ssz.SszContainer;
import tech.pegasys.teku.infrastructure.ssz.SszData;
import tech.pegasys.teku.infrastructure.ssz.schema.SszSchema;
import tech.pegasys.teku.infrastructure.ssz.schema.impl.AbstractSszContainerSchema;
import tech.pegasys.teku.infrastructure.ssz.tree.TreeNode;

/** Autogenerated by tech.pegasys.teku.ssz.backing.ContainersGenerator */
public abstract class ContainerSchema20<
        C extends SszContainer,
        V0 extends SszData,
        V1 extends SszData,
        V2 extends SszData,
        V3 extends SszData,
        V4 extends SszData,
        V5 extends SszData,
        V6 extends SszData,
        V7 extends SszData,
        V8 extends SszData,
        V9 extends SszData,
        V10 extends SszData,
        V11 extends SszData,
        V12 extends SszData,
        V13 extends SszData,
        V14 extends SszData,
        V15 extends SszData,
        V16 extends SszData,
        V17 extends SszData,
        V18 extends SszData,
        V19 extends SszData>
    extends AbstractSszContainerSchema<C> {

  public static <
          C extends SszContainer,
          V0 extends SszData,
          V1 extends SszData,
          V2 extends SszData,
          V3 extends SszData,
          V4 extends SszData,
          V5 extends SszData,
          V6 extends SszData,
          V7 extends SszData,
          V8 extends SszData,
          V9 extends SszData,
          V10 extends SszData,
          V11 extends SszData,
          V12 extends SszData,
          V13 extends SszData,
          V14 extends SszData,
          V15 extends SszData,
          V16 extends SszData,
          V17 extends SszData,
          V18 extends SszData,
          V19 extends SszData>
      ContainerSchema20<
              C,
              V0,
              V1,
              V2,
              V3,
              V4,
              V5,
              V6,
              V7,
              V8,
              V9,
              V10,
              V11,
              V12,
              V13,
              V14,
              V15,
              V16,
              V17,
              V18,
              V19>
          create(
              final SszSchema<V0> fieldSchema0,
              final SszSchema<V1> fieldSchema1,
              final SszSchema<V2> fieldSchema2,
              final SszSchema<V3> fieldSchema3,
              final SszSchema<V4> fieldSchema4,
              final SszSchema<V5> fieldSchema5,
              final SszSchema<V6> fieldSchema6,
              final SszSchema<V7> fieldSchema7,
              final SszSchema<V8> fieldSchema8,
              final SszSchema<V9> fieldSchema9,
              final SszSchema<V10> fieldSchema10,
              final SszSchema<V11> fieldSchema11,
              final SszSchema<V12> fieldSchema12,
              final SszSchema<V13> fieldSchema13,
              final SszSchema<V14> fieldSchema14,
              final SszSchema<V15> fieldSchema15,
              final SszSchema<V16> fieldSchema16,
              final SszSchema<V17> fieldSchema17,
              final SszSchema<V18> fieldSchema18,
              final SszSchema<V19> fieldSchema19,
              final BiFunction<
                      ContainerSchema20<
                          C,
                          V0,
                          V1,
                          V2,
                          V3,
                          V4,
                          V5,
                          V6,
                          V7,
                          V8,
                          V9,
                          V10,
                          V11,
                          V12,
                          V13,
                          V14,
                          V15,
                          V16,
                          V17,
                          V18,
                          V19>,
                      TreeNode,
                      C>
                  instanceCtor) {
    return new ContainerSchema20<>(
        fieldSchema0,
        fieldSchema1,
        fieldSchema2,
        fieldSchema3,
        fieldSchema4,
        fieldSchema5,
        fieldSchema6,
        fieldSchema7,
        fieldSchema8,
        fieldSchema9,
        fieldSchema10,
        fieldSchema11,
        fieldSchema12,
        fieldSchema13,
        fieldSchema14,
        fieldSchema15,
        fieldSchema16,
        fieldSchema17,
        fieldSchema18,
        fieldSchema19) {
      @Override
      public C createFromBackingNode(final TreeNode node) {
        return instanceCtor.apply(this, node);
      }
    };
  }

  protected ContainerSchema20(
      final SszSchema<V0> fieldSchema0,
      final SszSchema<V1> fieldSchema1,
      final SszSchema<V2> fieldSchema2,
      final SszSchema<V3> fieldSchema3,
      final SszSchema<V4> fieldSchema4,
      final SszSchema<V5> fieldSchema5,
      final SszSchema<V6> fieldSchema6,
      final SszSchema<V7> fieldSchema7,
      final SszSchema<V8> fieldSchema8,
      final SszSchema<V9> fieldSchema9,
      final SszSchema<V10> fieldSchema10,
      final SszSchema<V11> fieldSchema11,
      final SszSchema<V12> fieldSchema12,
      final SszSchema<V13> fieldSchema13,
      final SszSchema<V14> fieldSchema14,
      final SszSchema<V15> fieldSchema15,
      final SszSchema<V16> fieldSchema16,
      final SszSchema<V17> fieldSchema17,
      final SszSchema<V18> fieldSchema18,
      final SszSchema<V19> fieldSchema19) {

    super(
        List.of(
            fieldSchema0,
            fieldSchema1,
            fieldSchema2,
            fieldSchema3,
            fieldSchema4,
            fieldSchema5,
            fieldSchema6,
            fieldSchema7,
            fieldSchema8,
            fieldSchema9,
            fieldSchema10,
            fieldSchema11,
            fieldSchema12,
            fieldSchema13,
            fieldSchema14,
            fieldSchema15,
            fieldSchema16,
            fieldSchema17,
            fieldSchema18,
            fieldSchema19));
  }

  protected ContainerSchema20(
      final String containerName,
      final NamedSchema<V0> fieldNamedSchema0,
      final NamedSchema<V1> fieldNamedSchema1,
      final NamedSchema<V2> fieldNamedSchema2,
      final NamedSchema<V3> fieldNamedSchema3,
      final NamedSchema<V4> fieldNamedSchema4,
      final NamedSchema<V5> fieldNamedSchema5,
      final NamedSchema<V6> fieldNamedSchema6,
      final NamedSchema<V7> fieldNamedSchema7,
      final NamedSchema<V8> fieldNamedSchema8,
      final NamedSchema<V9> fieldNamedSchema9,
      final NamedSchema<V10> fieldNamedSchema10,
      final NamedSchema<V11> fieldNamedSchema11,
      final NamedSchema<V12> fieldNamedSchema12,
      final NamedSchema<V13> fieldNamedSchema13,
      final NamedSchema<V14> fieldNamedSchema14,
      final NamedSchema<V15> fieldNamedSchema15,
      final NamedSchema<V16> fieldNamedSchema16,
      final NamedSchema<V17> fieldNamedSchema17,
      final NamedSchema<V18> fieldNamedSchema18,
      final NamedSchema<V19> fieldNamedSchema19) {

    super(
        containerName,
        List.of(
            fieldNamedSchema0,
            fieldNamedSchema1,
            fieldNamedSchema2,
            fieldNamedSchema3,
            fieldNamedSchema4,
            fieldNamedSchema5,
            fieldNamedSchema6,
            fieldNamedSchema7,
            fieldNamedSchema8,
            fieldNamedSchema9,
            fieldNamedSchema10,
            fieldNamedSchema11,
            fieldNamedSchema12,
            fieldNamedSchema13,
            fieldNamedSchema14,
            fieldNamedSchema15,
            fieldNamedSchema16,
            fieldNamedSchema17,
            fieldNamedSchema18,
            fieldNamedSchema19));
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V0> getFieldSchema0() {
    return (SszSchema<V0>) getChildSchema(0);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V1> getFieldSchema1() {
    return (SszSchema<V1>) getChildSchema(1);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V2> getFieldSchema2() {
    return (SszSchema<V2>) getChildSchema(2);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V3> getFieldSchema3() {
    return (SszSchema<V3>) getChildSchema(3);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V4> getFieldSchema4() {
    return (SszSchema<V4>) getChildSchema(4);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V5> getFieldSchema5() {
    return (SszSchema<V5>) getChildSchema(5);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V6> getFieldSchema6() {
    return (SszSchema<V6>) getChildSchema(6);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V7> getFieldSchema7() {
    return (SszSchema<V7>) getChildSchema(7);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V8> getFieldSchema8() {
    return (SszSchema<V8>) getChildSchema(8);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V9> getFieldSchema9() {
    return (SszSchema<V9>) getChildSchema(9);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V10> getFieldSchema10() {
    return (SszSchema<V10>) getChildSchema(10);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V11> getFieldSchema11() {
    return (SszSchema<V11>) getChildSchema(11);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V12> getFieldSchema12() {
    return (SszSchema<V12>) getChildSchema(12);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V13> getFieldSchema13() {
    return (SszSchema<V13>) getChildSchema(13);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V14> getFieldSchema14() {
    return (SszSchema<V14>) getChildSchema(14);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V15> getFieldSchema15() {
    return (SszSchema<V15>) getChildSchema(15);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V16> getFieldSchema16() {
    return (SszSchema<V16>) getChildSchema(16);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V17> getFieldSchema17() {
    return (SszSchema<V17>) getChildSchema(17);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V18> getFieldSchema18() {
    return (SszSchema<V18>) getChildSchema(18);
  }

  @SuppressWarnings("unchecked")
  public SszSchema<V19> getFieldSchema19() {
    return (SszSchema<V19>) getChildSchema(19);
  }
}
