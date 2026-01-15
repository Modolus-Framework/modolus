package com.modolus.annotations.command;

import java.lang.annotation.*;

@Repeatable(Args.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Arg {

    String name();

    String description();

    DefaultArg defaultArg() default @DefaultArg(ignore = true, type = Void.class, argTypeName = "", defaultValue = "", defaultValueDescription = "");

    OptionalArg optionalArg() default @OptionalArg(ignore = true, type = Void.class, argTypeName = "");

    RequiredArg requiredArg() default @RequiredArg(ignore = true, type = Void.class, argTypeName = "");

    FlagArg flagArg() default @FlagArg(ignore = true);

}
