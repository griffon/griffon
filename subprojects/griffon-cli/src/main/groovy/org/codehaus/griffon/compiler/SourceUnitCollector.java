/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.compiler;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of all SourceUnits that belongs to a CompilationUnit.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class SourceUnitCollector extends CompilationUnit.SourceUnitOperation {
    private static final Map<String, SourceUnit> SOURCES = new HashMap<String, SourceUnit>();

    private static SourceUnitCollector instance;

    static {
        instance = new SourceUnitCollector();
    }

    public static SourceUnitCollector getInstance() {
        return instance;
    }

    private SourceUnitCollector() {
    }

    public void call(SourceUnit source) throws CompilationFailedException {
        SOURCES.put(source.getName(), source);
    }

    public void clear() {
        SOURCES.clear();
    }

    public boolean containsSource(String name) {
        return SOURCES.containsKey(name);
    }

    public boolean containsSource(SourceUnit source) {
        return SOURCES.containsValue(source);
    }

    public boolean containsSource(ClassNode classNode) {
        return getSourceUnit(classNode) != null;
    }

    public SourceUnit getSourceUnit(ClassNode classNode) {
        for (Map.Entry<String, SourceUnit> entry : SOURCES.entrySet()) {
            SourceUnit source = entry.getValue();
            if (source.getAST().getClasses().contains(classNode)) {
                return source;
            }
        }
        return null;
    }
}
