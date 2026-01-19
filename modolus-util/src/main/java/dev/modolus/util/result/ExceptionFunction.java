package dev.modolus.util.result;

@FunctionalInterface
public interface ExceptionFunction<E, T, X extends Exception> {

    T apply(E value) throws X;

}
