package dev.modolus.util.result;

@FunctionalInterface
public interface ExceptionRunnable<E extends Exception> {

    void run() throws E;

}
