package com.modolus.util.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<T, E> {

    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }

    @SafeVarargs
    public static <T> Result<T, Exception> ofException(Supplier<T> supplier, Class<? extends Exception>... exceptionClass) {
        try {
            return success(supplier.get());
        } catch (Exception e) {
            for (var clazz : exceptionClass) {
                if (clazz.isInstance(e)) return failure(e);
            }
            throw e;
        }
    }

    public static <T> Result<T, GenericError> ofNullable(T value) {
        return value == null ? failure(GenericError.NULL_VALUE) : success(value);
    }

    public static <T> Result<T, IllegalStateException> ofOptional(Optional<T> optional) {
        return optional.<Result<T, IllegalStateException>>map(Result::success).orElseGet(() -> failure(new IllegalStateException("Optional was empty.")));
    }

    public static <T extends Collection<E>, E> Result<T, GenericError> ofPossibleEmpty(T collection) {
        return collection.isEmpty() ? Result.failure(GenericError.EMPTY_COLLECTION) : Result.success(collection);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public @Nullable T get() {
        return value;
    }

    public @Nullable E getError() {
        return error;
    }

    public @NotNull T orElse(T other) {
        return isSuccess() ? value : other;
    }

    public T orElseThrow() {
        if (isSuccess()) {
            return value;
        }
        throw new IllegalStateException(error.toString());
    }

    public <X extends Throwable> T orElseThrow(@NotNull X exception) throws X {
        if (isSuccess()) {
            return value;
        }
        throw exception;
    }

    public <X> @NotNull Result<X, E> map(Function<T, X> mapper) {
        if (isFailure()) return failure(error);
        return success(mapper.apply(value));
    }

    public <X> @NotNull Result<T, X> mapError(Function<E, X> mapper) {
        if (isSuccess()) return success(value);
        return failure(mapper.apply(error));
    }

    public <X> @NotNull ResultErrorSwitch<T, E, X> switchMapError() {
        return new ResultErrorSwitch<>(this, isSuccess());
    }

    public @NotNull Result<T, E> recover(Function<E, T> mapper) {
        if (isSuccess()) return this;
        return success(mapper.apply(error));
    }

    public <X> @NotNull Result<X, E> flatMap(Function<T, Result<X, E>> mapper) {
        if (isFailure()) return failure(error);
        return mapper.apply(value);
    }

    public Result<T, E> tap(Consumer<T> consumer) {
        onSuccess(consumer);
        return this;
    }

    public void onSuccess(Consumer<T> consumer) {
        if (isSuccess()) consumer.accept(value);
    }

    public void onFailure(Consumer<E> consumer) {
        if (isFailure()) consumer.accept(error);
    }

    @Override
    public @NotNull String toString() {
        return isSuccess() ? String.format("Success(%s)", value.toString()) : String.format("Failure(%s)", error.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Result<?, ?> result = (Result<?, ?>) o;
        return Objects.equals(value, result.value) && Objects.equals(getError(), result.getError());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, getError());
    }
}
