/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
/*

Copyright 2008-2014 TOPdesk, the Netherlands

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package org.kordamp.jipsy.processor.testutils;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestDiagnosticListener implements DiagnosticListener<JavaFileObject> {

    private final List<Diagnostic<JavaFileObject>> diagnostics = new ArrayList<Diagnostic<JavaFileObject>>();

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        @SuppressWarnings("unchecked")
        Diagnostic<JavaFileObject> d = (Diagnostic<JavaFileObject>) diagnostic;
        diagnostics.add(d);
    }

    public List<Diagnostic<JavaFileObject>> diagnostics() {
        return diagnostics;
    }

    public static List<Diagnostic<JavaFileObject>> compile(Processor processor, String prefix, FileType type, String... fileNames) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        TestDiagnosticListener listener = new TestDiagnosticListener();
        CompilationTask task = compiler.getTask(null, null, listener, OutputDir.getOptions(), null, TestJavaFileObject.read(prefix, type, fileNames));
        task.setProcessors(Collections.singleton(processor));
        task.call();
        return listener.diagnostics();
    }
}
