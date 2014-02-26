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

import griffon.plugins.domain.methods.DynamicMethodInvocation;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractInstanceDynamicMethodInvocation
    extends AbstractInstanceMethodInvocation
    implements DynamicMethodInvocation {
    private final Pattern pattern;

    public AbstractInstanceDynamicMethodInvocation(@Nonnull Pattern pattern) {
        this.pattern = requireNonNull(pattern, "Argument 'pattern' cannot be null");
    }

    @Nonnull
    protected Pattern getPattern() {
        return pattern;
    }

    public boolean isMethodMatch(@Nonnull String methodName) {
        requireNonBlank(methodName, "Argument 'methodName' cannot be blank");
        return this.pattern.matcher(methodName.subSequence(0, methodName.length())).find();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("{pattern=").append(pattern);
        sb.append('}');
        return sb.toString();
    }
}