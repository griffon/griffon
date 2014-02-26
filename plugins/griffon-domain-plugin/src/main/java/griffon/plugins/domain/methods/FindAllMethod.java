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

import griffon.plugins.domain.orm.Criterion;

import java.util.Collection;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public interface FindAllMethod extends StaticMethodInvocation {
    String METHOD_NAME = "findAll";

    MethodSignature[] METHOD_SIGNATURES = new MethodSignature[]{
        new MethodSignature(true, Collection.class, METHOD_NAME, Object.class),
        new MethodSignature(true, Collection.class, METHOD_NAME, Object.class, Map.class),
        new MethodSignature(true, Collection.class, METHOD_NAME, Map.class),
        new MethodSignature(true, Collection.class, METHOD_NAME, Map.class, Map.class),
        new MethodSignature(true, Collection.class, METHOD_NAME, Criterion.class),
        new MethodSignature(true, Collection.class, METHOD_NAME, Criterion.class, Map.class)
    };
}