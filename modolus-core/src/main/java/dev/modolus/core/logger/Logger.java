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

package dev.modolus.core.logger;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.Singleton;
import dev.modolus.util.singleton.SingletonScope;
import dev.modolus.util.singleton.Singletons;
import io.sentry.IScopes;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Logger implements Singleton {

  public static Lazy<Logger> getPluginLogger() {
    return Lazy.ofPlugin(Logger.class);
  }

  public static Lazy<Logger> getRootLogger() {
    return Lazy.ofRoot(Logger.class);
  }

  private final HytaleLogger hytaleLogger;

  private Logger(HytaleLogger hytaleLogger, String singletonIdentifier, SingletonScope scope) {
    this(hytaleLogger);
    if (singletonIdentifier == null) Singletons.provideSingleton(this, scope).orElseThrow();
    else Singletons.provideSingleton(this, singletonIdentifier, scope).orElseThrow();
  }

  public static void providePluginLogger(@NotNull HytaleLogger logger) {
    providePluginLogger(logger, null);
  }

  public static void providePluginLogger(@NotNull HytaleLogger logger, String loggerIdentifier) {
    new Logger(logger, loggerIdentifier, SingletonScope.PLUGIN);
  }

  public static void provideRootLogger(@NotNull HytaleLogger logger) {
    provideRootLogger(logger, null);
  }

  public static void provideRootLogger(@NotNull HytaleLogger logger, String loggerIdentifier) {
    new Logger(logger, loggerIdentifier, SingletonScope.ROOT);
  }

  public HytaleLogger.Api at(Level level) {
    return hytaleLogger.at(level);
  }

  public HytaleLogger.Api atSevere() {
    return hytaleLogger.atSevere();
  }

  public HytaleLogger.Api atWarning() {
    return hytaleLogger.atWarning();
  }

  public HytaleLogger.Api atInfo() {
    return hytaleLogger.atInfo();
  }

  public HytaleLogger.Api atConfig() {
    return hytaleLogger.atConfig();
  }

  public HytaleLogger.Api atFine() {
    return hytaleLogger.atFine();
  }

  public HytaleLogger.Api atFiner() {
    return hytaleLogger.atFiner();
  }

  public HytaleLogger.Api atFinest() {
    return hytaleLogger.atFinest();
  }

  public @NotNull Level getLevel() {
    return hytaleLogger.getLevel();
  }

  public void setLevel(Level level) {
    hytaleLogger.setLevel(level);
  }

  public String getName() {
    return hytaleLogger.getName();
  }

  public @NotNull HytaleLogger getSubLogger(String name) {
    return hytaleLogger.getSubLogger(name);
  }

  public void setPropagatesSentryToParent(boolean propagate) {
    hytaleLogger.setPropagatesSentryToParent(propagate);
  }

  public void setSentryClient(IScopes scopes) {
    hytaleLogger.setSentryClient(scopes);
  }
}
