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

package tech.pegasys.teku.networking.eth2.gossip.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static tech.pegasys.teku.networking.p2p.gossip.config.GossipConfig.DEFAULT_D;

import org.junit.jupiter.api.Test;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.TestSpecFactory;

public class ScoringConfigTest {

  private final Spec spec = TestSpecFactory.createMainnetPhase0();
  private final ScoringConfig scoringConfig = ScoringConfig.create(spec, DEFAULT_D);

  @Test
  public void maxPositiveScore() {
    assertThat(scoringConfig.getMaxPositiveScore()).isCloseTo(107.5, within(0.00005));
  }
}
