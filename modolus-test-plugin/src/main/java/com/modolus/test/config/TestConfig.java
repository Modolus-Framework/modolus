package com.modolus.test.config;

import com.modolus.annotations.config.Config;
import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.core.config.AbstractConfiguration;
import com.modolus.core.logger.Logger;
import com.modolus.core.logger.LoggerUtils;
import com.modolus.util.singleton.Lazy;
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
