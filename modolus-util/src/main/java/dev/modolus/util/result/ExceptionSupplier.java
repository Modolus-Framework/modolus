package dev.modolus.util.result;

@FunctionalInterface
public interface ExceptionSupplier<T, E extends Exception> {

    T get() throws E;

}
