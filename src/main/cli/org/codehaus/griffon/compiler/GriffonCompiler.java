/*
 * Copyright 2010-2011 the original author or authors.
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

import org.codehaus.groovy.ant.Groovyc;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Phases;

import java.io.File;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public class GriffonCompiler extends Groovyc {
    public void setVerbose(boolean verbose) {
        GriffonCompilerContext.verbose = verbose;
    }

    public void setBasedir(String basedir) {
        if(basedir.endsWith(File.separator)) basedir = basedir.substring(0, basedir.length() - 2);
        GriffonCompilerContext.basedir = basedir;
    }

    public void setProjectName(String projectName) {
        GriffonCompilerContext.projectName = projectName;
    }

    protected void compile() {
        GriffonCompilerContext.setup();
        super.compile();
    }

    protected CompilationUnit makeCompileUnit() {
        if(!GriffonCompilerContext.getConfigOption(GriffonCompilerContext.DISABLE_AUTO_IMPORTS)) {
            DefaultImportCompilerCustomizer defaultImportCompilerCustomizer = new DefaultImportCompilerCustomizer();
            defaultImportCompilerCustomizer.collectDefaultImportsPerArtifact();
            configuration.addCompilationCustomizers(defaultImportCompilerCustomizer);
        } else {
            log("Default imports feature disabled.");
        }

        CompilationUnit compilationUnit = super.makeCompileUnit();
        SourceUnitCollector.getInstance().clear();
        compilationUnit.addPhaseOperation(SourceUnitCollector.getInstance(), Phases.CONVERSION);

        if(!GriffonCompilerContext.getConfigOption(GriffonCompilerContext.DISABLE_LOGGING_INJECTION)) {
            compilationUnit.addPhaseOperation(new LoggingInjectionOperation(), Phases.CANONICALIZATION);
        } else {
            log("Conditional logging feature disabled.");
        }

        if(!GriffonCompilerContext.getConfigOption(GriffonCompilerContext.DISABLE_THREADING_INJECTION)) {
            compilationUnit.addPhaseOperation(new ThreadingInjectionOperation(), Phases.CANONICALIZATION);
        } else {
            log("Threading injection feature disabled.");
        }

        if(GriffonCompilerContext.getConfigOption(GriffonCompilerContext.DISABLE_AST_INJECTION)) {
            log("Artifact AST injection feature disabled.");
        }

        return compilationUnit;
    }
}
