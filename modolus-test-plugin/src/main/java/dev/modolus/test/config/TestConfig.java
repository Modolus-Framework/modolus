package dev.modolus.test.config;

import dev.modolus.annotations.config.Config;
import dev.modolus.annotations.singleton.CreateOnRuntime;
import dev.modolus.core.config.AbstractConfiguration;
import dev.modolus.core.logger.Logger;
import dev.modolus.core.logger.LoggerUtils;
import dev.modolus.util.singleton.Lazy;
import lombok.Getter;

@Getter
@Config(name = "test")
@CreateOnRuntime
public class TestConfig extends AbstractConfiguration<TestConfig> {

    private static final Lazy<Logger> LOGGER = Logger.getPluginLogger();

    public TestConfig() {
        super(TestConfig.class);
    }

    private String test = "test";

    @Override
    protected void onConfigurationLoaded() {
        LoggerUtils.printInfo(LOGGER, "Test config loaded");
        LoggerUtils.printInfo(LOGGER, String.format("Test value: %s", test));
    }
}
