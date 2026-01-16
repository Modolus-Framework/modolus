package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public final class Lazy<T> {

    private final Class<T> clazz;
    private String singletonIdentifier = null;

    public Result<T, SingletonError> get() {
        return singletonIdentifier == null ? Singletons.getSingleton(clazz) : Singletons.getSingleton(clazz, singletonIdentifier);
    }

    public T getOrThrow() {
        return this.get().orElseThrow();
    }

}
