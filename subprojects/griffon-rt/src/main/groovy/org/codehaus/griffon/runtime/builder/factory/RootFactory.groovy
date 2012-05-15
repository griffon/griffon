/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.builder.factory

/**
 * Identifies the top level node of a script
 *
 * @author Andres Almiray
 */
class RootFactory extends AbstractFactory {
    final boolean leaf = true

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        String rootName = attributes.remove('name') ?: 'root'
        attributes.clear()
        if (null == value) {
            String scriptName = builder.variables[FactoryBuilderSupport.SCRIPT_CLASS_NAME]
            if (!scriptName) return null
            String varName = scriptName + '-' + rootName
            value = builder.variables[varName]
        } else if(value instanceof CharSequence) {
            String scriptName = builder.variables[FactoryBuilderSupport.SCRIPT_CLASS_NAME]
            if (!scriptName) return null
            String varName = scriptName + '-' + value.toString()
            value = builder.variables[varName]q
        } else if (value instanceof Class) {
            String varName = value.name + '-' + rootName
            return builder.variables[varName]
        }  else {
            String scriptName = builder.variables[FactoryBuilderSupport.SCRIPT_CLASS_NAME]
            if (!scriptName) return null
            String varName = scriptName + '-' + rootName
            builder.variables[varName] = value
            builder.variables[scriptName + '-root'] = value
        }
        value
    }
}
