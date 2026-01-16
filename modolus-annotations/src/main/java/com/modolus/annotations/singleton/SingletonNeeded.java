package com.modolus.annotations.singleton;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(SingletonNeededCollection.class)
@Retention(RetentionPolicy.SOURCE)
public @interface SingletonNeeded {

    Class<?> value();

    String name() default "";

}
