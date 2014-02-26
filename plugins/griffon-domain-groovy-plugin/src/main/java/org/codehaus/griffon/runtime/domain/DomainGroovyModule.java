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

import griffon.core.injection.Module;
import griffon.inject.DependsOn;
import griffon.plugins.domain.orm.CriterionEvaluator;
import griffon.plugins.domain.orm.GroovyAwareBeanCriterionEvaluator;
import griffon.plugins.validation.constraints.ConstrainedPropertyAssembler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.validation.constraints.GroovyAwareConstrainedPropertyAssembler;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 */
@DependsOn("domain")
@Named("domain-groovy")
@ServiceProviderFor(Module.class)
public class DomainGroovyModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ConstrainedPropertyAssembler.class)
            .to(GroovyAwareConstrainedPropertyAssembler.class);

        bind(CriterionEvaluator.class)
            .to(GroovyAwareBeanCriterionEvaluator.class)
            .asSingleton();
    }
}
