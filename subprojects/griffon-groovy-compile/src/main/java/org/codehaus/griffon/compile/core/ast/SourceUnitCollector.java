/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.core.ast;

import griffon.annotations.core.Nonnull;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Keeps track of all SourceUnits that belongs to a CompilationUnit.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SourceUnitCollector extends CompilationUnit.SourceUnitOperation {
    private static final Map<String, SourceUnit> SOURCES = new LinkedHashMap<>();
    private static final String ERROR_SOURCE_NULL = "Argument 'source' must not be null";
    private static final String ERROR_CLASS_NODE_NULL = "Argument 'classNode' must not be null";

    private static final SourceUnitCollector instance;

    static {
        instance = new SourceUnitCollector();
    }

    @Nonnull
    public static SourceUnitCollector getInstance() {
        return instance;
    }

    private SourceUnitCollector() {
    }

    public void call(@Nonnull SourceUnit source) throws CompilationFailedException {
        requireNonNull(source, ERROR_SOURCE_NULL);
        SOURCES.put(source.getName(), source);
    }

    public void clear() {
        SOURCES.clear();
    }

    public boolean containsSource(String name) {
        requireNonBlank(name, "Argument 'name' must not be blank");
        return SOURCES.containsKey(name);
    }

    public boolean containsSource(SourceUnit source) {
        requireNonNull(source, ERROR_SOURCE_NULL);
        return SOURCES.containsValue(source);
    }

    public boolean containsSource(ClassNode classNode) {
        requireNonNull(classNode, ERROR_CLASS_NODE_NULL);
        return getSourceUnit(classNode) != null;
    }

    public SourceUnit getSourceUnit(ClassNode classNode) {
        requireNonNull(classNode, ERROR_CLASS_NODE_NULL);
        for (Map.Entry<String, SourceUnit> entry : SOURCES.entrySet()) {
            SourceUnit source = entry.getValue();
            if (source.getAST().getClasses().contains(classNode)) {
                return source;
            }
        }
        return null;
    }
}
