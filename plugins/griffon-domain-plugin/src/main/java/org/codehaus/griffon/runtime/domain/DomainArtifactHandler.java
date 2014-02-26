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

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.inject.Typed;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import org.codehaus.griffon.runtime.core.artifact.AbstractArtifactHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Handler for 'Domain' artifacts.
 *
 * @author Andres Almiray
 */
@Typed(GriffonDomain.class)
public class DomainArtifactHandler extends AbstractArtifactHandler<GriffonDomain> {
    @Inject
    public DomainArtifactHandler(@Nonnull GriffonApplication application) {
        super(application, GriffonDomain.class, GriffonDomainClass.TYPE, GriffonDomainClass.TRAILING);
    }

    @Nonnull
    public GriffonClass newGriffonClassInstance(@Nonnull Class<GriffonDomain> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return new DefaultGriffonDomainClass(getApplication(), clazz);
    }

    @SuppressWarnings("unchecked")
    public static boolean isDomainClass(Class<?> clazz) {
        return clazz != null &&
            !Enum.class.isAssignableFrom(clazz) &&
            GriffonDomain.class.isAssignableFrom(clazz);
    }
}
