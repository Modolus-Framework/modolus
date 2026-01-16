package com.example.testing;

import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.InjectSingleton;
import com.modolus.annotations.singleton.ProvideSingleton;
import com.modolus.annotations.singleton.Scope;
import com.modolus.core.logger.Logger;
import com.modolus.util.singleton.SingletonScope;

@Scope
@CreateOnRuntime
@ProvideSingleton(value = Plugin.class)
@InjectSingleton(value = Logger.class, scope = SingletonScope.ROOT)
public class Plugin extends AbstractPlugin {
}
