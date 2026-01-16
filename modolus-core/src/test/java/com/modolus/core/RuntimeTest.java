package com.modolus.core;

import com.modolus.core.runtime.Runtime;
import com.modolus.util.singleton.Lazy;
import com.modolus.util.singleton.SingletonScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuntimeTest {

    @Test
    void testRuntime() {
        assertDoesNotThrow(Runtime::initializeRuntime);

        Lazy<Plugin> plugin = new Lazy<>(Plugin.class, SingletonScope.ROOT);

        assertDoesNotThrow(plugin::getOrThrow);
        assertNotNull(plugin.getOrThrow());
    }

}
