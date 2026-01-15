package com.modolus.annotations.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Command {

    String name();

    String description();

    boolean async() default false;

    CommandTarget target() default CommandTarget.ALL;

    enum CommandTarget {
        PLAYER,
        WORLD,
        ALL
    }

}
