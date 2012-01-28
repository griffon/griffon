/*
 * Copyright 2011-2012 the original author or authors.
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
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Andres Almiray
 * @since 0.9.3
 */
public class DefaultImportCompilerCustomizer extends CompilationCustomizer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultImportCompilerCustomizer.class);
    private static final Map<String, String[]> IMPORTS_PER_ARTIFACT_TYPE = new LinkedHashMap<String, String[]>();
    private static final String[] DEFAULT_IMPORTS = {"griffon.core.", "griffon.util."};

    public DefaultImportCompilerCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(SourceUnit sourceUnit, GeneratorContext generatorContext, ClassNode classNode) throws CompilationFailedException {
        if (GriffonCompilerContext.isGriffonArtifact(sourceUnit) ||
                GriffonCompilerContext.isGriffonAddon(sourceUnit) ||
                GriffonCompilerContext.isGriffonScript(sourceUnit) ||
                GriffonCompilerContext.isTestSource(sourceUnit)) {
            final ModuleNode ast = sourceUnit.getAST();
            String artifactPath = GriffonCompilerContext.getArtifactPath(sourceUnit.getName());
            String[] imports = DEFAULT_IMPORTS;
            if (artifactPath != null) {
                imports = GriffonCompilerContext.merge(DEFAULT_IMPORTS, IMPORTS_PER_ARTIFACT_TYPE.get(artifactPath));
            }

            for (String importStmnt : imports) {
                if (importStmnt.endsWith(".")) {
                    ast.addStarImport(importStmnt);
                }
            }
        }
    }

    public void collectDefaultImportsPerArtifact() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = null;

        try {
            urls = cl.getResources("META-INF/griffon-default-imports.properties");
        } catch (IOException ioe) {
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try {
                Properties props = new Properties();
                props.load(url.openStream());

                Enumeration<?> types = props.propertyNames();
                while (types.hasMoreElements()) {
                    String type = (String) types.nextElement();
                    String[] existingImports = IMPORTS_PER_ARTIFACT_TYPE.get(type);
                    String[] incomingImports = ((String) props.get(type)).split(",");

                    if (incomingImports.length == 0) continue;
                    IMPORTS_PER_ARTIFACT_TYPE.put(type, GriffonCompilerContext.merge(existingImports, incomingImports));
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) LOG.debug("Could not process default imports from " + url + " => " + e);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Default imports for all artifacts: " + Arrays.toString(DEFAULT_IMPORTS));
            for (Map.Entry<String, String[]> imports : IMPORTS_PER_ARTIFACT_TYPE.entrySet()) {
                LOG.debug("Default imports per " + imports.getKey() + ": " + Arrays.toString(imports.getValue()));
            }
        }
    }
}
