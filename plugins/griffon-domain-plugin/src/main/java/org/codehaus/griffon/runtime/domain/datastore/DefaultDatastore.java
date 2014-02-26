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
package org.codehaus.griffon.runtime.domain.datastore;

import griffon.core.GriffonApplication;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonClass;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.datastore.Dataset;
import griffon.plugins.domain.datastore.Datastore;
import griffon.plugins.domain.orm.CriterionEvaluator;
import griffon.transform.Domain;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultDatastore implements Datastore {
    private final GriffonApplication application;
    private final String name;
    private final Map<String, Dataset<? extends GriffonDomain>> DATASETS = new ConcurrentHashMap<>();
    private final CriterionEvaluator criterionEvaluator;

    @Inject
    public DefaultDatastore(@Nonnull GriffonApplication application, @Nonnull String name, @Nonnull CriterionEvaluator criterionEvaluator) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        this.criterionEvaluator = requireNonNull(criterionEvaluator, "Argument 'criterionEvaluator' cannot be null");
        this.name = name;
    }

    private static class ClassNode {
        private Class<?> superclass;
        private final Class<?> clazz;
        private final Set<Class<?>> subclasses = new LinkedHashSet<>();

        private ClassNode(@Nonnull Class<?> clazz) {
            this.clazz = clazz;
        }
    }

    @PostConstruct
    private void setup() {
        Map<Class<?>, ClassNode> classes = new LinkedHashMap<>();
        for (GriffonClass domainClass : application.getArtifactManager().getClassesOfType(GriffonDomainClass.TYPE)) {
            Class clazz = domainClass.getClazz();
            if (!classes.containsKey(clazz)) {
                classes.put(clazz, new ClassNode(clazz));
            }

            Class<?> superclass = clazz.getSuperclass();
            while (superclass != null) {
                if (superclass.isAnnotationPresent(Domain.class)) {
                    ClassNode classNode = classes.get(superclass);
                    if (classNode == null) {
                        classNode = new ClassNode(superclass);
                        classes.put(superclass, classNode);
                    }
                    classNode.subclasses.add(clazz);
                } else {
                    break;
                }
                clazz = superclass;
                superclass = superclass.getSuperclass();
            }
        }

        /*
        Set<Class<?>> toRemove = new LinkedHashSet<>();
        for (Class<?> clazz : classes.keySet()) {
            if (subclasses.contains(clazz)) {
                toRemove.add(clazz);
            }
        }
        for (Class<?> clazz : toRemove) {
            classes.remove(clazz);
        }
        */

        System.out.println(classes);
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Dataset<T> dataset(@Nonnull String name) {
        requireNonBlank(name, "Argument 'name' cannot be blank");
        Dataset dataset = DATASETS.get(name);
        if (dataset == null) {
            ArtifactManager artifactManager = application.getArtifactManager();
            GriffonClass griffonClass = artifactManager.findGriffonClass(name, GriffonDomainClass.TYPE);
            dataset = dataset((GriffonDomainClass) griffonClass);
        }
        return dataset;
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends GriffonDomain> Dataset<T> dataset(@Nonnull GriffonDomainClass<T> domainClass) {
        requireNonNull(domainClass, "Argument 'domainClass' cannot be null");
        Dataset dataset = DATASETS.get(domainClass.getName());
        if (dataset == null) {
            dataset = new DefaultDataset(domainClass, criterionEvaluator);
            DATASETS.put(dataset.getName(), dataset);
        }
        return dataset;
    }
}
