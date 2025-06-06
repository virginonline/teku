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

package tech.pegasys.teku.infrastructure.metrics;

import org.hyperledger.besu.plugin.services.metrics.MetricCategory;

abstract class StubMetric {
  private final MetricCategory category;
  private final String name;
  private final String help;

  protected StubMetric(final MetricCategory category, final String name, final String help) {
    this.category = category;
    this.name = name;
    this.help = help;
  }

  public MetricCategory getCategory() {
    return category;
  }

  public String getName() {
    return name;
  }

  public String getHelp() {
    return help;
  }
}
