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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.util.Properties;

import org.codehaus.groovy.ant.Groovyc;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.classgen.*;
import org.codehaus.groovy.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public class GriffonCompiler extends Groovyc {
    protected org.codehaus.griffon.compiler.ResolveVisitor resolveVisitor;

    private static final Logger LOG = LoggerFactory.getLogger(GriffonCompiler.class);
    private static final Map<String, String[]> IMPORTS_PER_ARTIFACT_TYPE = new LinkedHashMap<String, String[]>();
    private static final String[] DEFAULT_IMPORTS = {"griffon.core.", "griffon.util."};

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
        collectDefaultImportsPerArtifact();
        super.compile();
    }

    protected CompilationUnit makeCompileUnit() {
        CompilationUnit compilationUnit = super.makeCompileUnit();
        
        if(!GriffonCompilerContext.getConfigOption(GriffonCompilerContext.DISABLE_AUTO_IMPORTS)) {
            compilationUnit.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation() {
                public void call(SourceUnit source, GeneratorContext context,
                                 ClassNode classNode) throws CompilationFailedException {
                    processGriffonSource(source, context, classNode);
                }
            }, Phases.CONVERSION);
        
            // WATCH OUT!! We add our own ResolveVisitor before Groovy's
            resolveVisitor = new org.codehaus.griffon.compiler.ResolveVisitor(compilationUnit);
            compilationUnit.addPhaseOperation(resolve, Phases.CONVERSION);
        } else {
            log("Default imports feature disabled.");
        }

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

    private final CompilationUnit.SourceUnitOperation resolve = new CompilationUnit.SourceUnitOperation() {
        private boolean called = false;

        public void call(SourceUnit source) throws CompilationFailedException {
            if(!called) {
                if(LOG.isDebugEnabled()) {
                    LOG.debug("Default imports for all artifacts: "+Arrays.toString(DEFAULT_IMPORTS));
                    for(Map.Entry<String, String[]> imports : IMPORTS_PER_ARTIFACT_TYPE.entrySet()) {
                        LOG.debug("Default imports per "+imports.getKey()+": "+ Arrays.toString(imports.getValue()));
                    }
                }
                called = true;
            }

            List<ClassNode> classes = source.getAST().getClasses();
            for (ClassNode node : classes) {
                // TODO: watch out for changes on Groovy core regarding inner class handling
                // on the original class from which this one was copied
                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
                scopeVisitor.visitClass(node);
                resolveVisitor.startResolving(node, source);
            }
        }
    };

    private void processGriffonSource(SourceUnit source, GeneratorContext context,
                             ClassNode classNode) throws CompilationFailedException {
        if(GriffonCompilerContext.isGriffonArtifact(source) ||
           GriffonCompilerContext.isGriffonAddon(source) ||
           GriffonCompilerContext.isGriffonScript(source) ||
           GriffonCompilerContext.isTestSource(source)) {
            Map<ClassNode, String[]> imports = resolveVisitor.getAdditionalImports();
            String artifactPath = GriffonCompilerContext.getArtifactPath(source.getName());
            if(artifactPath != null) {
                imports.put(classNode, GriffonCompilerContext.merge(DEFAULT_IMPORTS, IMPORTS_PER_ARTIFACT_TYPE.get(artifactPath)));
            } else {
                imports.put(classNode, DEFAULT_IMPORTS);
            }
        }
    }

    private void collectDefaultImportsPerArtifact() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = null;
        
        try {
            urls = cl.getResources("META-INF/griffon-default-imports.properties");
        } catch(IOException ioe) {
            return;
        }

        while(urls.hasMoreElements()) {
           URL url = urls.nextElement();
           try {
               Properties props = new Properties();
               props.load(url.openStream());

               Enumeration<?> types = props.propertyNames();
               while(types.hasMoreElements()) {
                   String type = (String) types.nextElement();
                   String[] existingImports = IMPORTS_PER_ARTIFACT_TYPE.get(type);
                   String[] incomingImports = ((String)props.get(type)).split(",");

                   if(incomingImports.length == 0) continue;
                   IMPORTS_PER_ARTIFACT_TYPE.put(type, GriffonCompilerContext.merge(existingImports, incomingImports));
               }
           } catch(Exception e) {
               if(LOG.isDebugEnabled()) LOG.debug("Could not process default imports from "+url+" => "+e);
           }
        }
    }
}
