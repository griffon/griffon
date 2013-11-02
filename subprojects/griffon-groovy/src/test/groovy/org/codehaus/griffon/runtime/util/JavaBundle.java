package org.codehaus.griffon.runtime.util;

import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import java.util.Map;

public class JavaBundle extends AbstractMapResourceBundle{
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        entries.put("java.key", "java");
    }
}
