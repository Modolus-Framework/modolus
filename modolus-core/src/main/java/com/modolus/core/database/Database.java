package com.modolus.core.database;

import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.util.result.ExceptionFunction;
import com.modolus.util.result.Result;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.Singleton;
import com.modolus.util.singleton.SingletonScope;
import com.modolus.util.singleton.Singletons;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Database implements Singleton {

    private final Lazy<Logger> logger = Lazy.ofPlugin(Logger.class);

    private Result<HikariDataSource, DatabaseError> dataSource = Result.failure(DatabaseErrorType.DATABASE_NOT_INITIALIZED.toError());

    public final <T> @NotNull Result<T, DatabaseError> doOnConnection(
            @NotNull ExceptionFunction<@NotNull Connection, @NotNull T, SQLException> function) {
        return dataSource
                .mapException(
                        HikariDataSource::getConnection,
                        ex -> DatabaseErrorType.FAILED_TO_RECEIVE_CONNECTION.toError(ex.getMessage()),
                        SQLException.class
                )
                .mapException(
                        conn -> {
                            var result = function.apply(conn);
                            conn.close();
                            return result;
                        },
                        ex -> DatabaseErrorType.GENERIC_SQL_EXCEPTION.toError(ex.getMessage()),
                        SQLException.class
                );
    }

    public final <T> @NotNull Result<T, DatabaseError> doOnTransaction(
            @NotNull ExceptionFunction<@NotNull Connection, @NotNull T, SQLException> function) {
        return doOnConnection(conn -> {
            conn.setAutoCommit(false);
            var result = function.apply(conn);
            conn.commit();
            return result;
        });
    }

    public final <T> @NotNull Result<Stream<T>, DatabaseError> doOnPreparedStatement(
            @NotNull Function<@NotNull Connection, @NotNull PreparedStatement> preparedStatementBuilder,
            @NotNull Function<@NotNull ResultSet, @NotNull T> resultSetMapper) {
        return doOnConnection(conn -> {
            var preparedStatement = preparedStatementBuilder.apply(conn);
            var resultSet = preparedStatement.getResultSet();

            List<T> resultList = new ArrayList<>();

            while (resultSet.next()) {
                resultList.add(resultSetMapper.apply(resultSet));
            }

            resultSet.close();
            preparedStatement.close();
            return resultList.stream();
        });
    }

    public final @NotNull Result<Void, DatabaseError> close() {
        return dataSource.mapVoid(HikariDataSource::close);
    }

    public void updateConfiguration(@NotNull DatabaseConfiguration configuration) {
        this.close()
                .onFailure(_ -> LoggerUtils.printInfo(logger, "No active database connection to close before updating configuration"));

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
    }

    public static void provideDatabaseConfiguration() {
        provideDatabaseConfiguration(SingletonScope.PLUGIN);
    }

    public static void provideDatabaseConfiguration(SingletonScope scope) {
        Singletons.provideSingleton(new Database(), scope).onSuccess(Singleton::onInitialization);
    }

    public static void provideDatabaseConfiguration(String singletonIdentifier, SingletonScope scope) {
        Singletons.provideSingleton(new Database(), singletonIdentifier, scope).onSuccess(Singleton::onInitialization);
    }

}
