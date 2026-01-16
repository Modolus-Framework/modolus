package com.modolus.core.database;

import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.SingletonFor;
import com.modolus.annotations.singleton.SingletonNeeded;
import com.modolus.core.log.Logger;

@SingletonFor(Database.class)
@SingletonNeeded(Logger.class)
@CreateOnRuntime
public class DatabaseImpl extends AbstractDatabaseImpl implements Database {

    @Override
    public void onInitialization() {
        logger.get()
                .onSuccess(Logger::test);
    }
}
