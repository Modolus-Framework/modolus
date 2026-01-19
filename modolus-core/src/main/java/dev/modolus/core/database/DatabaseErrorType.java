package dev.modolus.core.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum DatabaseErrorType {

    DATABASE_NOT_INITIALIZED("Database is not initialized"),
    GENERIC_SQL_EXCEPTION("SQL Exception: %s"),
    FAILED_TO_CONNECT_TO_DATABASE("Failed to connect to database with error: %s"),
    FAILED_TO_RECEIVE_CONNECTION("Failed to receive connection with error: %s");

    private final String message;

    @Contract("_ -> new")
    public @NotNull DatabaseError toError(Object... args) {
        return new DatabaseError(this, List.of(args));
    }

}
