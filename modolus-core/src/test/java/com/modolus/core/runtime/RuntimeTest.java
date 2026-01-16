package com.modolus.core.runtime;

import org.junit.jupiter.api.Test;

class RuntimeTest {

    @Test
    void runTestBed() {
        Runtime.initializeRuntime().orElseThrow();
    }


}