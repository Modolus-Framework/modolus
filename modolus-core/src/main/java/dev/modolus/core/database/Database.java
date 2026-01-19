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

package dev.modolus.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.result.ExceptionConsumer;
import dev.modolus.util.result.ExceptionFunction;
import dev.modolus.util.result.Result;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Database implements Singleton {

  private final Lazy<Logger> logger = Lazy.ofPlugin(Logger.class);

  private Result<HikariDataSource, DatabaseError> dataSource =
      Result.failure(DatabaseErrorType.DATABASE_NOT_INITIALIZED.toError());

  public final <T> @NotNull Result<T, DatabaseError> doOnConnection(
      @NotNull ExceptionFunction<@NotNull Connection, @NotNull T, SQLException> function) {
    return dataSource
        .mapException(
            HikariDataSource::getConnection,
            ex -> DatabaseErrorType.FAILED_TO_RECEIVE_CONNECTION.toError(ex.getMessage()),
            SQLException.class)
        .mapException(
            conn -> {
              var result = function.apply(conn);
              conn.close();
              return result;
            },
            ex -> DatabaseErrorType.GENERIC_SQL_EXCEPTION.toError(ex.getMessage()),
            SQLException.class);
  }

  public final <T> @NotNull Result<T, DatabaseError> doOnTransaction(
      @NotNull ExceptionFunction<@NotNull Connection, @NotNull T, SQLException> function) {
    return doOnConnection(
        conn -> {
          conn.setAutoCommit(false);
          var result = function.apply(conn);
          conn.commit();
          return result;
        });
  }

  public final <T> @NotNull Result<Stream<T>, DatabaseError> doOnPreparedStatementQuery(
      @Language("sql") String statementQuery,
      @NotNull ExceptionConsumer<@NotNull PreparedStatement, SQLException> preparedStatementHandler,
      @NotNull ExceptionFunction<@NotNull ResultSet, @NotNull T, SQLException> resultSetMapper) {
    return doOnConnection(
        conn -> {
          var preparedStatement = conn.prepareStatement(statementQuery);
          preparedStatementHandler.apply(preparedStatement);
          var resultSet = preparedStatement.executeQuery();

          List<T> resultList = new ArrayList<>();

          while (resultSet.next()) {
            resultList.add(resultSetMapper.apply(resultSet));
          }

          resultSet.close();
          preparedStatement.close();
          return resultList.stream();
        });
  }

  public final @NotNull Result<Integer, DatabaseError> doOnPreparedStatementUpdate(
      @Language("sql") String statementQuery,
      @NotNull ExceptionConsumer<@NotNull PreparedStatement, SQLException> preparedStatementHandler) {
    return doOnConnection(
        conn -> {
          var preparedStatement = conn.prepareStatement(statementQuery);
          preparedStatementHandler.apply(preparedStatement);

          var affected = preparedStatement.executeUpdate();

          preparedStatement.close();
          return affected;
        });
  }

  public final @NotNull Result<Void, DatabaseError> close() {
    return dataSource.mapVoid(HikariDataSource::close);
  }

  public void updateConfiguration(@NotNull DatabaseConfiguration configuration) {
    this.close()
        .onFailure(
            _ ->
                LoggerUtils.printInfo(
                    logger,
                    "No active database connection to close before updating configuration"));

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(configuration.getJdbcUrl());
    config.setUsername(configuration.getUsername());
    config.setPassword(configuration.getUsername());

    configuration.getDataSourceProperties().forEach(config::addDataSourceProperty);

    config.setConnectionTimeout(configuration.getConnectionTimeout());
    config.setIdleTimeout(configuration.getIdleTimeout());
    config.setKeepaliveTime(configuration.getKeepAliveTime());
    config.setMaxLifetime(configuration.getMaxLifetime());

    config.setMaximumPoolSize(configuration.getMaximumPoolSize());
    config.setMinimumIdle(configuration.getMinimumIdle());

    this.dataSource = Result.success(new HikariDataSource(config));

    if (configuration.getMigrationPath() != null) {
      dataSource
          .map(Flyway.configure()::dataSource)
          .map(cfg -> cfg.locations("classpath:" + configuration.getMigrationPath()))
          .map(FluentConfiguration::load)
          .mapVoid(Flyway::migrate)
          .onFailure(
              databaseError ->
                  LoggerUtils.printError(
                      Logger.getPluginLogger(),
                      String.format(
                          "Failed to migrate database with error: %s",
                          databaseError.getMessage())));
    }
  }
}
