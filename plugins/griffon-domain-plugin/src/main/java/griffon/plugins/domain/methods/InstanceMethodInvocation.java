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
package griffon.plugins.domain.methods;

import griffon.plugins.domain.GriffonDomain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 */
public interface InstanceMethodInvocation extends MethodInvocation {
    /**
     * <p>Invokes the actual method. The target object and arguments are supplied.
     *
     * @param target     the target on which the method is invoked.
     * @param methodName the name of the method to be invoked
     * @param arguments  the arguments passed in the method call @return the return value of the dynamic method invocation.
     */
    @Nullable
    <T extends GriffonDomain> T invoke(@Nonnull T target, @Nonnull String methodName, Object... arguments);
}