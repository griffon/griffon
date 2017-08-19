/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.builder.javafx.factory
/**
 * Enables MVC groups to be used as component nodes
 *
 * @author Andres Almiray
 * @author Alexander Klein
 */
@SuppressWarnings("rawtypes")
class AliasedFXMetaComponentFactory extends FXMetaComponentFactory {
    final String mvcType
    final Map<String, Object> attributes = [:]
    final boolean leaf

    AliasedFXMetaComponentFactory(String mvcType, boolean leaf = true, Map<String, Object> attributes = [:]) {
        this.leaf = leaf
        this.mvcType = mvcType
        this.attributes.putAll(attributes ?: [:])
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        return super.newInstance(builder, name, value, attributes + [value: value])
    }

    @Override
    protected Map resolveAttributes(Map attributes) {
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
}