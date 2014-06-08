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
package org.codehaus.griffon.compile.core.processor.artifact;

import griffon.metadata.ArtifactProviderFor;
import org.kordamp.jipsy.processor.*;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Andres Almiray
 */
@SupportedAnnotationTypes("*")
@SupportedOptions({Options.SPI_DIR_OPTION, Options.SPI_LOG_OPTION, Options.SPI_VERBOSE_OPTION, Options.SPI_DISABLED_OPTION})
public class ArtifactProviderProcessor extends AbstractSpiProcessor {
    public static final String NAME = ArtifactProviderProcessor.class.getName()
        + " (" + ArtifactProviderProcessor.class.getPackage().getImplementationVersion() + ")";

    private static final int MAX_SUPPORTED_VERSION = 8;

    private Persistence persistence;
    private ArtifactCollector data;

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return ArtifactProviderFor.class;
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

        persistence = new ArtifactPersistence(NAME, options.dir(), processingEnv.getFiler(), logger);
        data = new ArtifactCollector(persistence.getInitializer(), logger);

        // Initialize if possible
        for (String artifactName : persistence.tryFind()) {
            data.getArtifact(artifactName);
        }
        data.cache();
    }

    @Override
    protected void writeData() {
        if (data.isModified()) {
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

        for (TypeElement artifact : findArtifacts(currentClass)) {
            CheckResult implementationResult = isImplementation(currentClass, artifact);
            if (implementationResult.isError()) {
                reportError(currentClass, implementationResult);
            } else {
                register(createProperQualifiedName(artifact), currentClass);
            }
        }
    }

    private CheckResult checkCurrentClass(TypeElement currentClass) {
        if (currentClass.getKind() != ElementKind.CLASS) {
            return CheckResult.valueOf("is not a class");
        }

        if (!currentClass.getModifiers().contains(Modifier.PUBLIC)) {
            return CheckResult.valueOf("is not a public class");
        }

        return CheckResult.OK;
    }

    private List<TypeElement> findArtifacts(TypeElement classElement) {
        List<TypeElement> artifacts = new ArrayList<>();

        for (AnnotationMirror annotation : findAnnotationMirrors(classElement, getAnnotationClass().getName())) {
            artifacts.add(toElement(findSingleValueMember(annotation, "value")));
        }

        return artifacts;
    }

    private void register(String artifactName, TypeElement provider) {
        data.getArtifact(artifactName).addProvider(createProperQualifiedName(provider));
    }
}