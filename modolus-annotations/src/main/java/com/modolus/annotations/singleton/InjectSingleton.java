package com.modolus.annotations.singleton;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(InjectSingletons.class)
@Retention(RetentionPolicy.SOURCE)
public @interface InjectSingleton {

    Class<?> value();

    String fieldName() default "";

    String singletonIdentifier() default "";

}
