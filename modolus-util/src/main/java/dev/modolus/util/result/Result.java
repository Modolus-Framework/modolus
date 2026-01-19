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

package dev.modolus.util.result;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Result<T, E> {

  private final T value;
  private final E error;

  private Result(T value, E error) {
    this.value = value;
    this.error = error;
  }

  @Contract(value = "_ -> new", pure = true)
  public static <T, E> @NotNull Result<T, E> success(@NotNull T value) {
    return new Result<>(value, null);
  }

  public static <E> @NotNull Result<Void, E> success() {
    return new Result<>(null, null);
  }

  @Contract(value = "_ -> new", pure = true)
  public static <T, E> @NotNull Result<T, E> failure(@NotNull E error) {
    return new Result<>(null, error);
  }

  public static <T, E extends Exception> Result<T, E> ofException(
      ExceptionSupplier<T, E> supplier, Class<E> exceptionClass) {
    try {
      return success(supplier.get());
    } catch (Exception e) {
      if (exceptionClass.isInstance(e)) return Result.failure(exceptionClass.cast(e));
      throw new UnexpectedErrorException(e);
    }
  }

  public static <T, E extends Exception> Result<T, GenericError> ofNullableWithException(
      ExceptionSupplier<@Nullable T, E> supplier, Class<E> exceptionClass) {
    try {
      return ofNullable(supplier.get());
    } catch (Exception e) {
      if (exceptionClass.isInstance(e)) return Result.failure(GenericError.EXCEPTION_THROWN);
      throw new UnexpectedErrorException(e);
    }
  }

  public static <E extends Exception> Result<Void, E> ofExceptionVoid(
      ExceptionRunnable<E> supplier, Class<E> exceptionClass) {
    try {
      supplier.run();
      return success();
    } catch (Exception e) {
      if (exceptionClass.isInstance(e)) return Result.failure(exceptionClass.cast(e));
      throw new UnexpectedErrorException(e);
    }
  }

  public static <T> Result<T, GenericError> ofNullable(T value) {
    return value == null ? failure(GenericError.NULL_VALUE) : success(value);
  }

  public static <T> Result<T, IllegalStateException> ofOptional(@NotNull Optional<T> optional) {
    return optional
        .<Result<T, IllegalStateException>>map(Result::success)
        .orElseGet(
            () ->
                failure(
                    new IllegalStateException(
                        "Expected Optional to contain a value, but it was empty.")));
  }

  public static <T extends Collection<E>, E> Result<T, GenericError> ofPossibleEmpty(
      @NotNull T collection) {
    return collection.isEmpty()
        ? Result.failure(GenericError.EMPTY_COLLECTION)
        : Result.success(collection);
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
    var message = String.valueOf(error);
    throw new IllegalStateException(message);
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

  public @NotNull Result<Void, E> mapVoid(Consumer<T> mapper) {
    if (isFailure()) return failure(error);
    mapper.accept(value);
    return success();
  }

  public <O, X extends Exception> @NotNull Result<O, E> mapException(
      ExceptionFunction<T, @NotNull O, X> mapper,
      Function<X, @NotNull E> failureMapper,
      Class<X> exceptionClass) {
    if (isFailure()) return failure(error);
    return ofException(() -> mapper.apply(value), exceptionClass).mapError(failureMapper);
  }

  public <X extends Exception> @NotNull Result<Void, E> mapExceptionVoid(
      ExceptionConsumer<T, X> supplier,
      Function<X, @NotNull E> failureMapper,
      Class<X> exceptionClass) {
    if (isFailure()) return failure(error);
    return ofExceptionVoid(() -> supplier.apply(value), exceptionClass).mapError(failureMapper);
  }

  public <X> @NotNull Result<T, X> mapError(Function<E, X> mapper) {
    if (isSuccess()) return success(value);
    return failure(mapper.apply(error));
  }

  public <X> @NotNull ResultErrorSwitch<T, E, X> switchMapError() {
    return new ResultErrorSwitch<>(this);
  }

  public @NotNull Result<T, E> recover(Function<E, @NotNull T> mapper) {
    if (isSuccess()) return this;
    return success(mapper.apply(error));
  }

  public @NotNull Result<T, E> recoverFlat(Function<E, @NotNull Result<T, E>> mapper) {
    if (isSuccess()) return this;
    return mapper.apply(error);
  }

  public @NotNull Result<T, GenericError> recoverNullable(Function<E, @Nullable T> mapper) {
    if (isSuccess()) return success(value);
    return ofNullable(mapper.apply(error));
  }

  public <X extends Exception> @NotNull Result<T, X> tryRecover(
      ExceptionFunction<E, @NotNull T, X> mapper, Class<X> exceptionClass) {
    if (isSuccess()) return success(value);
    return Result.ofException(() -> mapper.apply(error), exceptionClass);
  }

  public <X extends Exception> @NotNull Result<T, GenericError> tryRecoverNullable(
      ExceptionFunction<E, @Nullable T, X> mapper, Class<X> exceptionClass) {
    if (isSuccess()) return success(value);
    return Result.ofNullableWithException(() -> mapper.apply(error), exceptionClass);
  }

  public <X> @NotNull Result<X, E> flatMap(Function<T, Result<X, E>> mapper) {
    if (isFailure()) return failure(error);
    return mapper.apply(value);
  }

  public <O, X> @NotNull Result<O, X> flatMap(
      Function<T, Result<O, X>> mapper, Function<E, X> errorMapper) {
    if (isSuccess()) return mapper.apply(value);
    return failure(errorMapper.apply(error));
  }

  public @NotNull Result<T, E> tap(Consumer<T> consumer) {
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
    return isSuccess() ? String.format("Success(%s)", value) : String.format("Failure(%s)", error);
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
