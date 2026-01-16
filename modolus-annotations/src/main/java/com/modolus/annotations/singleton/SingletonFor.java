package com.modolus.annotations.singleton;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(SingletonForCollection.class)
@Retention(RetentionPolicy.SOURCE)
public @interface SingletonFor {

    Class<?> value();

}
