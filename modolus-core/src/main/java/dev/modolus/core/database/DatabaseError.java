package dev.modolus.core.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DatabaseError(DatabaseErrorType type, List<Object> args) {

    @Contract(pure = true)
    public @NotNull String getMessage() {
        return String.format(type.getMessage(), args.toArray(Object[]::new));
    }

}
