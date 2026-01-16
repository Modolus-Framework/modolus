package com.example.testing;

import com.modolus.core.Plugin;
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

        Lazy<com.modolus.core.Plugin> plugin = new Lazy<>(Plugin.class, SingletonScope.PLUGIN);

        assertDoesNotThrow(plugin::getOrThrow);
        assertNotNull(plugin.getOrThrow());
    }

}
