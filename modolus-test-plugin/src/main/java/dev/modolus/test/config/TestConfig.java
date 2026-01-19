/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.test.config;

import dev.modolus.annotations.config.Config;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.core.config.AbstractConfiguration;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.singleton.Lazy;
import lombok.Getter;

@Getter
@Config(name = "test")
@CreateOnRuntime
public class TestConfig extends AbstractConfiguration<TestConfig> {

  private static final Lazy<Logger> LOGGER = Logger.getPluginLogger();

  public TestConfig() {
    super(TestConfig.class);
  }

  private String test = "test";

  @Override
  protected void onConfigurationLoaded() {
    LoggerUtils.printInfo(LOGGER, "Test config loaded");
    LoggerUtils.printInfo(LOGGER, String.format("Test value: %s", test));
  }
}
