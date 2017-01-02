/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.type;

import griffon.metadata.TypeProviderFor;
import org.kordamp.jipsy.processor.AbstractSpiProcessor;
import org.kordamp.jipsy.processor.CheckResult;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Options;
import org.kordamp.jipsy.processor.Persistence;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Andres Almiray
 * @since 2.1.0
 */
@SupportedAnnotationTypes("*")
@SupportedOptions({Options.SPI_DIR_OPTION, Options.SPI_LOG_OPTION, Options.SPI_VERBOSE_OPTION, Options.SPI_DISABLED_OPTION})
public class TypeProviderProcessor extends AbstractSpiProcessor {
    public static final String NAME = TypeProviderProcessor.class.getName()
        + " (" + TypeProviderProcessor.class.getPackage().getImplementationVersion() + ")";

    private static final int MAX_SUPPORTED_VERSION = 8;

    private Persistence persistence;
    private TypeCollector data;

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return TypeProviderFor.class;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion[] svs = SourceVersion.values();
        for (int i = svs.length - 1; i >= 0; i--) {
            String name = svs[i].name();
            Matcher m = RELEASE_PATTERN.matcher(name);
            if (m.matches()) {
                int release = Integer.parseInt(m.group(1));
                if (release <= MAX_SUPPORTED_VERSION) return svs[i];
            }
        }

        return SourceVersion.RELEASE_6;
    }

    @Override
    protected void initialize() {
        super.initialize();

        persistence = new TypePersistence(NAME, options.dir(), processingEnv.getFiler(), logger);
        data = new TypeCollector(persistence.getInitializer(), logger);

        // Initialize if possible
        for (String typeName : persistence.tryFind()) {
            data.getType(typeName);
        }
        data.cache();
    }

    @Override
    protected void writeData() {
        if (data.isModified()) {
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
                        processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
                    }
                }
                persistence.writeLog();
            }
        }
    }

    @Override
    protected void removeStaleData(RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getRootElements()) {
            if (e instanceof TypeElement) {
                TypeElement currentClass = (TypeElement) e;
                data.removeProvider(createProperQualifiedName(currentClass));
            }
        }
    }

    @Override
    protected void handleElement(Element e) {
        if (!(e instanceof TypeElement)) {
            return;
        }

        TypeElement currentClass = (TypeElement) e;

        CheckResult checkResult = checkCurrentClass(currentClass);
        if (checkResult.isError()) {
            reportError(currentClass, checkResult);
            return;
        }

        for (TypeElement type : findTypes(currentClass)) {
            CheckResult implementationResult = isImplementation(currentClass, type);
            if (implementationResult.isError()) {
                reportError(currentClass, implementationResult);
            } else {
                register(createProperQualifiedName(type), currentClass);
            }
        }
    }

    private CheckResult checkCurrentClass(TypeElement currentClass) {
        if (currentClass.getKind() != ElementKind.CLASS || currentClass.getKind() != ElementKind.INTERFACE) {
            return CheckResult.valueOf("is not a class nor an interface");
        }

        if (!currentClass.getModifiers().contains(Modifier.PUBLIC)) {
            return CheckResult.valueOf("is not public");
        }

        return CheckResult.OK;
    }

    private List<TypeElement> findTypes(TypeElement classElement) {
        List<TypeElement> types = new ArrayList<>();

        for (AnnotationMirror annotation : findAnnotationMirrors(classElement, getAnnotationClass().getName())) {
            types.add(toElement(findSingleValueMember(annotation, "value")));
        }

        return types;
    }

    private void register(String typeName, TypeElement provider) {
        data.getType(typeName).addProvider(createProperQualifiedName(provider));
    }
}