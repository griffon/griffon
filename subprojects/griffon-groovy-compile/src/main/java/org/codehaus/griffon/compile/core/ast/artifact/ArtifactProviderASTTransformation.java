/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.artifact;

import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.compile.core.processor.artifact.Artifact;
import org.codehaus.griffon.compile.core.processor.artifact.ArtifactCollector;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.kordamp.gipsy.transform.GipsyASTTransformation;
import org.kordamp.jipsy.ServiceProviderFor;
import org.kordamp.jipsy.processor.CheckResult;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.CLASS_GENERATION)
public class ArtifactProviderASTTransformation extends GipsyASTTransformation {
    public static final String NAME = ArtifactProviderASTTransformation.class.getName()
        + " (" + ArtifactProviderASTTransformation.class.getPackage().getImplementationVersion() + ")";

    private static final ClassNode SERVICE_PROVIDER_FOR_TYPE = makeClassSafe(ArtifactProviderFor.class);


    private Persistence persistence;
    private ArtifactCollector data;

    @Override
    protected ClassNode getAnnotationClassNode() {
        return SERVICE_PROVIDER_FOR_TYPE;
    }

    @Override
    protected void initialize(ModuleNode moduleNode) {
        super.initialize(moduleNode);

        File outputDir = moduleNode.getContext().getConfiguration().getTargetDirectory();
        persistence = new ArtifactPersistence(NAME, options.dir(), outputDir, logger);
        data = new ArtifactCollector(persistence.getInitializer(), logger);

        // Initialize if possible
        for (String artifactName : persistence.tryFind()) {
            data.getArtifact(artifactName);
        }
        //data.cache();
    }

    @Override
    protected void removeStaleData(ClassNode classNode, ModuleNode moduleNode) {
        data.removeProvider(classNode.getName());
    }

    protected void handleAnnotations(ClassNode classNode, List<AnnotationNode> annotations, ModuleNode moduleNode) {
        CheckResult checkResult = checkCurrentClass(classNode);
        if (checkResult.isError()) {
            addError(checkResult.getMessage(), classNode, moduleNode.getContext());
            return;
        }

        for (ClassNode artifact : findArtifacts(annotations)) {
            CheckResult implementationResult = isImplementation(classNode, artifact);
            if (implementationResult.isError()) {
                addError(implementationResult.getMessage(), classNode, moduleNode.getContext());
            } else {
                register(artifact.getName(), classNode);
            }
        }
    }

    @Override
    protected void writeData() {
        // if (data.isModified()) {
            if (data.artifacts().isEmpty()) {
                logger.note(LogLocation.LOG_FILE, "Writing output");
                try {
                    persistence.delete();
                } catch (IOException e) {
                    logger.warning(LogLocation.LOG_FILE, "An error occurred while deleting data file");
                }
            } else {
                logger.note(LogLocation.LOG_FILE, "Writing output");
                for (Artifact artifact : data.artifacts()) {
                    try {
                        persistence.write(artifact.getName(), artifact.toProviderNamesList());
                    } catch (IOException e) {
                        // TODO print out error
                    }
                }
                persistence.writeLog();
            }
        // }
    }

    private CheckResult checkCurrentClass(ClassNode currentClass) {
        if (currentClass.isInterface()) {
            return CheckResult.valueOf("is not a class");
        }
        if (!isPublic(currentClass.getModifiers())) {
            return CheckResult.valueOf("is not a public class");
        }

        if (isStatic(currentClass.getModifiers())) {
            return CheckResult.valueOf("is a static class");
        }

        /*
        if (!hasNoArgsConstructor(currentClass)) {
            return CheckResult.valueOf("has no public no-args constructor");
        }
        */

        return CheckResult.OK;
    }

    private List<ClassNode> findArtifacts(List<AnnotationNode> annotations) {
        List<ClassNode> artifacts = new ArrayList<>();

        for (AnnotationNode annotation : annotations) {
            for (Expression expr : findCollectionValueMember(annotation, "value")) {
                if (expr instanceof ClassExpression) {
                    artifacts.add(((ClassExpression) expr).getType());
                }
            }
        }

        return artifacts;
    }

    private void register(String artifactName, ClassNode provider) {
        data.getArtifact(artifactName).addProvider(provider.getName());
    }
}