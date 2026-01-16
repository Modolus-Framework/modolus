package com.modolus.processor;

import lombok.experimental.UtilityClass;

@UtilityClass
final class Annotations {

    static final String COMMAND_ANNOTATION = "com.modolus.annotations.command.Command";
    static final String SINGLETON_FOR_COLLECTION_ANNOTATION = "com.modolus.annotations.singleton.SingletonForCollection";
    static final String SINGLETON_FOR_ANNOTATION = "com.modolus.annotations.singleton.SingletonFor";
    static final String SINGLETON_NEEDED_COLLECTION_ANNOTATION = "com.modolus.annotations.singleton.SingletonNeededCollection";
    static final String SINGLETON_NEEDED_ANNOTATION = "com.modolus.annotations.singleton.SingletonNeeded";
    static final String CREATE_ON_RUNTIME_ANNOTATION = "com.modolus.annotations.singleton.CreateOnRuntime";

}
