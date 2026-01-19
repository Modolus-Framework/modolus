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

package dev.modolus.core.config;

import dev.modolus.annotations.config.Config;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.core.logger.Logger;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.SingletonScope;
import java.util.logging.Level;
import lombok.Getter;

@Getter
@Config(name = "modolus-config")
@CreateOnRuntime
public class ModolusConfiguration extends AbstractConfiguration<ModolusConfiguration> {

  private static final Lazy<Logger> LOGGER = Logger.getRootLogger();

  public ModolusConfiguration() {
    super(ModolusConfiguration.class, SingletonScope.ROOT);
  }

  private String logLevel = "INFO";

  @Override
  protected void onConfigurationLoaded() {
    LOGGER.get().onSuccess(logger -> logger.setLevel(Level.parse(getLogLevel())));
  }
}
