package com.modolus.util.singleton;

import org.jetbrains.annotations.NotNull;

public class SingletonException extends Exception {
    public SingletonException(@NotNull SingletonError error) {
        super(error.name());
    }
}
