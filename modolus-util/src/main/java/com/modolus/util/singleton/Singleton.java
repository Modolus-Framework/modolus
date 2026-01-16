package com.modolus.util.singleton;

public interface Singleton {

    default void onInitialization() {
    }

    default void onDestruction() {
    }

    default void destruct() {
        Singletons.destructSingleton(this.getClass());
    }

}
