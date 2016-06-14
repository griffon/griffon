/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.builder.core.factory

/**
 * Enables MVC groups to be used as component nodes
 *
 * @author Andres Almiray
 * @author Alexander Klein
 */
@SuppressWarnings("rawtypes")
class AliasedMetaComponentFactory extends MetaComponentFactory {
    final String mvcType
    final Map<String, Object> attributes = [:]

    AliasedMetaComponentFactory(String mvcType, Map<String, Object> attributes = [:]) {
        this.mvcType = mvcType
        this.attributes.putAll(attributes ?: [:])
    }

    @Override
    boolean isLeaf() {
        return true
    }

    @Override
    protected Map resolveAttributes(Map attributes) {
        println this.attributes
        println attributes
        Map defaultMvcArgs = this.attributes.mvcArgs ?: [:]
        Map mvcArgs = attributes.mvcArgs ?: [:]
        Map attrs = [:]
        attrs.putAll(this.attributes)
        attrs.putAll(attributes)
        Map args = [:]
        args.putAll(defaultMvcArgs)
        args.putAll(mvcArgs)
        attrs.mvcArgs = args
        return attrs
    }

    @Override
    protected String resolveMvcId(Object name, Object value, Map attributes) {
        return attributes.containsKey('mvcId') ? attributes.remove('mvcId') : mvcType
    }
}