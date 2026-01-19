package dev.modolus.annotations.singleton;

import dev.modolus.util.singleton.SingletonScope;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(ProvideSingletons.class)
@Retention(RetentionPolicy.SOURCE)
public @interface ProvideSingleton {

    Class<?> value();

    String singletonIdentifier() default "";

    SingletonScope scope() default SingletonScope.PLUGIN;

}
