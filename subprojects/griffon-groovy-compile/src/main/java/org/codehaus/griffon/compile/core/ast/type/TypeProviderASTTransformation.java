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
package org.codehaus.griffon.compile.core.ast.type;

import griffon.metadata.TypeProviderFor;
import org.codehaus.griffon.compile.core.processor.type.Type;
import org.codehaus.griffon.compile.core.processor.type.TypeCollector;
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
 * @since 2.1.0
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.CLASS_GENERATION)
public class TypeProviderASTTransformation extends GipsyASTTransformation {
    public static final String NAME = TypeProviderASTTransformation.class.getName()
        + " (" + TypeProviderASTTransformation.class.getPackage().getImplementationVersion() + ")";

    private static final ClassNode SERVICE_PROVIDER_FOR_TYPE = makeClassSafe(TypeProviderFor.class);


    private Persistence persistence;
    private TypeCollector data;

    @Override
    protected ClassNode getAnnotationClassNode() {
        return SERVICE_PROVIDER_FOR_TYPE;
    }

    @Override
    protected void initialize(ModuleNode moduleNode) {
        super.initialize(moduleNode);

        File outputDir = moduleNode.getContext().getConfiguration().getTargetDirectory();
        persistence = new TypePersistence(NAME, options.dir(), outputDir, logger);
        data = new TypeCollector(persistence.getInitializer(), logger);

        // Initialize if possible
        for (String typeName : persistence.tryFind()) {
            data.getType(typeName);
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

        for (ClassNode type : findTypes(annotations)) {
            CheckResult implementationResult = isImplementation(classNode, type);
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
        if (data.types().isEmpty()) {
            logger.note(LogLocation.LOG_FILE, "Writing output");
            try {
                persistence.delete();
            } catch (IOException e) {
                logger.warning(LogLocation.LOG_FILE, "An error occurred while deleting data file");
            }
        } else {
            logger.note(LogLocation.LOG_FILE, "Writing output");
            for (Type type : data.types()) {
                try {
                    persistence.write(type.getName(), type.toProviderNamesList());
                } catch (IOException e) {
                    // TODO print out error
                }
            }
            persistence.writeLog();
        }
        // }
    }

    private CheckResult checkCurrentClass(ClassNode currentClass) {
        if (currentClass.isEnum()) {
            return CheckResult.valueOf("is not a class nor an interface");
        }
        if (!isPublic(currentClass.getModifiers())) {
            return CheckResult.valueOf("is not public");
        }

        if (isStatic(currentClass.getModifiers())) {
            return CheckResult.valueOf("is static");
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

    private void register(String typeName, ClassNode provider) {
        data.getType(typeName).addProvider(provider.getName());
    }
}