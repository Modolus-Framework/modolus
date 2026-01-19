package dev.modolus.util.singleton;

public interface Singleton {

    default void onInitialization() {
    }

    default void onDestruction() {
    }

}
