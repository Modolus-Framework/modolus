package com.modolus.core.database;

import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.InjectSingleton;
import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.core.logger.Logger;

@CreateOnRuntime
@InjectSingleton(value = Logger.class)
@ProvideSingleton(value = Database.class)
public class DatabaseImpl extends AbstractDatabaseImpl implements Database {

}
