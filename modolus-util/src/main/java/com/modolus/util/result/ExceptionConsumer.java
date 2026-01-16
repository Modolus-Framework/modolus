package com.modolus.util.result;

@FunctionalInterface
public interface ExceptionConsumer<T, E extends Exception> {

    void apply(T value) throws E;

}