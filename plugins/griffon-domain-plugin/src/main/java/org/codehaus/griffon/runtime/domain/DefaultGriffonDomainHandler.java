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
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.methods.InstanceMethodInvocation;
import griffon.plugins.domain.methods.StaticMethodInvocation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static java.util.Collections.emptyMap;

/**
 * @author Andres Almiray
 */
public class DefaultGriffonDomainHandler extends AbstractGriffonDomainHandler {
    @Inject
    public DefaultGriffonDomainHandler(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected Map<String, InstanceMethodInvocation> getInstanceMethods() {
        return emptyMap();
    }

    @Nonnull
    @Override
    protected Map<String, StaticMethodInvocation> getStaticMethods() {
        return emptyMap();
    }

    @Nonnull
    public String getMapping() {
        return "default";
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected <T extends GriffonDomain> T doInvokeInstanceMethod(@Nonnull InstanceMethodInvocation method, @Nonnull T target, @Nonnull String methodName, Object... args) {
        throw (RuntimeException) sanitize(new UnsupportedOperationException("Domain method " + methodName + " is not supported by mapping '" + getMapping() + "'"));
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected <T extends GriffonDomain> Object doInvokeStaticMethod(@Nonnull StaticMethodInvocation method, @Nonnull Class<T> clazz, @Nonnull String methodName, Object... args) {
        throw (RuntimeException) sanitize(new UnsupportedOperationException("Domain method " + methodName + " is not supported by mapping '" + getMapping() + "'"));
    }
}
