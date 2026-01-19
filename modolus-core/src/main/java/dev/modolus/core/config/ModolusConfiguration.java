package dev.modolus.core.config;

import dev.modolus.annotations.config.Config;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.core.logger.Logger;
import dev.modolus.util.singleton.Lazy;
import dev.modolus.util.singleton.SingletonScope;
import lombok.Getter;

import java.util.logging.Level;

@Getter
@Config(name = "modolus-config")
@CreateOnRuntime
public class ModolusConfiguration extends AbstractConfiguration<ModolusConfiguration> {

    private static final Lazy<Logger> LOGGER = Logger.getRootLogger();

    public ModolusConfiguration() {
        super(ModolusConfiguration.class, SingletonScope.ROOT);
    }

    private String logLevel = "INFO";

    @Override
    protected void onConfigurationLoaded() {
        LOGGER.get()
                .onSuccess(logger -> logger.setLevel(Level.parse(getLogLevel())));
    }
}
