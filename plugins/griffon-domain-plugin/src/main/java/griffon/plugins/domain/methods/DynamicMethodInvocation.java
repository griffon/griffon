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

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 */
public interface DynamicMethodInvocation {
    /**
     * <p>Checks if a method name matches the criteria of the implementation class.
     *
     * @param methodName the static method name
     * @return result of criteria match test
     */
    boolean isMethodMatch(@Nonnull String methodName);
}