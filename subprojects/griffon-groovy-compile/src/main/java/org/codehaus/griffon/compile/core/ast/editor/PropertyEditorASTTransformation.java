/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.editor;

import griffon.metadata.PropertyEditorFor;
import org.codehaus.griffon.compile.core.processor.editor.PropertyEditorCollector;
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

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPublic;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.CLASS_GENERATION)
public class PropertyEditorASTTransformation extends GipsyASTTransformation {
    public static final String NAME = PropertyEditorASTTransformation.class.getName()
        + " (" + PropertyEditorASTTransformation.class.getPackage().getImplementationVersion() + ")";

    private static final ClassNode PROPERTY_EDITOR_FOR_TYPE = makeClassSafe(PropertyEditorFor.class);
    private static final ClassNode PROPERTY_EDITOR_TYPE = makeClassSafe(PropertyEditor.class);

    private Persistence persistence;
    private PropertyEditorCollector data;

    @Override
    protected ClassNode getAnnotationClassNode() {
        return PROPERTY_EDITOR_FOR_TYPE;
    }

    @Override
    protected void initialize(ModuleNode moduleNode) {
        super.initialize(moduleNode);

        File outputDir = moduleNode.getContext().getConfiguration().getTargetDirectory();
        persistence = new PropertyEditorPersistence(NAME, options.dir(), outputDir, logger);
        data = new PropertyEditorCollector(persistence.getInitializer(), logger);
        data.load();
    }

    @Override
    protected void removeStaleData(ClassNode classNode, ModuleNode moduleNode) {
        data.removeEditor(classNode.getName());
    }

    protected void handleAnnotations(ClassNode classNode, List<AnnotationNode> annotations, ModuleNode moduleNode) {
        CheckResult checkResult = checkCurrentClass(classNode);
        if (checkResult.isError()) {
            addError(checkResult.getMessage(), classNode, moduleNode.getContext());
            return;
        }

        for (ClassNode type : findTypes(annotations)) {
            CheckResult implementationResult = isImplementation(classNode, PROPERTY_EDITOR_TYPE);
            if (implementationResult.isError()) {
                addError(implementationResult.getMessage(), classNode, moduleNode.getContext());
            } else {
                register(type.getName(), classNode);
            }
        }
    }

    @Override
    protected void writeData() {
        // if (data.isModified()) {
            String content = data.toList();
            if (content.length() > 0) {
                logger.note(LogLocation.LOG_FILE, "Writing output");
                try {
                    persistence.write(PropertyEditor.class.getName(), content);
                } catch (IOException ioe) {
                    // TODO print out error
                }
                persistence.writeLog();
            } else {
                logger.note(LogLocation.LOG_FILE, "Writing output");
                try {
                    persistence.delete();
                } catch (IOException e) {
                    logger.warning(LogLocation.LOG_FILE, "An error occurred while deleting data file");
                }
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

        if (isAbstract(currentClass.getModifiers())) {
            return CheckResult.valueOf("is an abstract class");
        }

        if (!hasNoArgsConstructor(currentClass)) {
            return CheckResult.valueOf("has no public no-args constructor");
        }

        return CheckResult.OK;
    }

    private List<ClassNode> findTypes(List<AnnotationNode> annotations) {
        List<ClassNode> types = new ArrayList<>();

        for (AnnotationNode annotation : annotations) {
            for (Expression expr : findCollectionValueMember(annotation, "value")) {
                if (expr instanceof ClassExpression) {
                    types.add(((ClassExpression) expr).getType());
                }
            }
        }

        return types;
    }

    private void register(String type, ClassNode editor) {
        data.getPropertyEditor(type, editor.getName());
    }
}