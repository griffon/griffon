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
package org.codehaus.griffon.runtime.domain;

import griffon.core.addon.GriffonAddon;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.injection.Module;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.datastore.Datastore;
import griffon.plugins.domain.datastore.DatastoreFactory;
import griffon.plugins.domain.datastore.DatastoreHandler;
import griffon.plugins.domain.datastore.DatastoreStorage;
import griffon.plugins.domain.orm.BeanCriterionEvaluator;
import griffon.plugins.domain.orm.CriterionEvaluator;
import griffon.plugins.validation.constraints.ConstrainedPropertyAssembler;
import griffon.plugins.validation.constraints.ConstraintsEvaluator;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.domain.datastore.*;
import org.codehaus.griffon.runtime.validation.constraints.DefaultConstrainedPropertyAssembler;
import org.codehaus.griffon.runtime.validation.constraints.DefaultConstraintsEvaluator;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 */
@Named("domain")
@ServiceProviderFor(Module.class)
public class DomainModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(DatastoreHandler.class)
            .to(DefaultDatastoreHandler.class)
            .asSingleton();

        bind(DatastoreFactory.class)
            .to(DefaultDatastoreFactory.class)
            .asSingleton();

        bind(DatastoreStorage.class)
            .to(DefaultDatastoreStorage.class)
            .asSingleton();

        bind(Datastore.class)
            .withClassifier(named("default"))
            .to(DefaultDatastore.class)
            .asSingleton();

        bind(GriffonDomainHandler.class)
            .withClassifier(named("memory"))
            .to(MemoryGriffonDomainHandler.class)
            .asSingleton();

        bind(ArtifactHandler.class)
            .to(DomainArtifactHandler.class)
            .asSingleton();

        bind(ConstraintsEvaluator.class)
            .to(DefaultConstraintsEvaluator.class)
            .asSingleton();

        bind(CriterionEvaluator.class)
            .to(BeanCriterionEvaluator.class)
            .asSingleton();

        bind(ConstrainedPropertyAssembler.class)
            .to(DefaultConstrainedPropertyAssembler.class);

        bind(GriffonAddon.class)
            .to(DomainAddon.class)
            .asSingleton();
    }
}
