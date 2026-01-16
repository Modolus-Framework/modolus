package com.modolus.processor;

import java.util.Map;
import java.util.Set;

/**
 * Map an incoming source file class name to multiple outgoing source file class names with separate source file writers
 */
public record SharedContext(
        Map<String, Map<String, SourceFileWriter>> sourceFileWriters,
        Set<String> createOnRuntimeClasses,
        Set<String> scopePackages
) {
}
