package com.modolus.core.log;

import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.SingletonFor;

@SingletonFor(Logger.class)
@CreateOnRuntime
public class Logger extends AbstractLogger {

    public void test() {
        System.out.println("test");
    }

}
