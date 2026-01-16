package com.modolus.util.singleton;

import com.modolus.util.result.Result;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Lazy<T> {

    private final Class<T> clazz;

    public Result<T, SingletonError> get() {
        return Singletons.getSingleton(clazz);
    }

}
