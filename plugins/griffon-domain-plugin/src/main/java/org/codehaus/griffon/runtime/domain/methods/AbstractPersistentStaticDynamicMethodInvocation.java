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
package org.codehaus.griffon.runtime.domain.methods;

import griffon.core.Configuration;
import griffon.core.artifact.GriffonClass;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.methods.PersistentMethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractPersistentStaticDynamicMethodInvocation
    extends AbstractStaticDynamicMethodInvocation
    implements PersistentMethodInvocation {
    private static final String ERROR_CLAZZ_NULL = "Argument 'clazz' cannot be null";
    private final GriffonDomainHandler griffonDomainHandler;

    public AbstractPersistentStaticDynamicMethodInvocation(@Nonnull GriffonDomainHandler griffonDomainHandler, @Nonnull Pattern pattern) {
        super(pattern);
        this.griffonDomainHandler = requireNonNull(griffonDomainHandler, "Argument 'griffonDomainHandler' cannot be null");
    }

    @Nonnull
    @Override
    public GriffonDomainHandler getGriffonDomainHandler() {
        return griffonDomainHandler;
    }

    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return griffonDomainHandler.getApplication().getConfiguration();
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> GriffonDomainClass<T> getDomainClassFor(@Nonnull Class<T> clazz) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        GriffonClass griffonClass = griffonDomainHandler.getApplication().getArtifactManager().findGriffonClass(clazz);
        requireState(griffonClass instanceof GriffonDomainClass, "Class " + clazz.getName() + " is not a domain class.");
        return (GriffonDomainClass) griffonClass;
    }

    @Nullable
    @Override
    public final <T extends GriffonDomain> Object invoke(@Nonnull Class<T> clazz, @Nonnull String methodName, Object... arguments) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(methodName, "Argument 'methodName' cannot be blank");
        return invokeInternal(getDomainClassFor(clazz), methodName, normalizeArgs(arguments));
    }

    @Nullable
    protected abstract <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments);
}