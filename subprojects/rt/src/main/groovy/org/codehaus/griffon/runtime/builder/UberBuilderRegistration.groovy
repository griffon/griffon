/*
 * Copyright 2007-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.codehaus.griffon.runtime.builder

/**
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class UberBuilderRegistration {
    private static final Closure[] EMPTY_CLOSURE_ARRAY = new Closure[0]

    Factory factory
    FactoryBuilderSupport builder
    String prefixString

    UberBuilderRegistration(String prefixString, FactoryBuilderSupport builder) {
        this.@prefixString = prefixString
        this.@builder = builder
    }

    UberBuilderRegistration(String prefixString, Factory factory) {
        this.@prefixString = prefixString
        this.@factory = factory
    }

    Factory nominateFactory(String name) {
        if (builder) {
            // need to turn off proxy to get at class durring lookup
            def oldProxy = builder.proxyBuilder
            try {
                builder.proxyBuilder = builder
                String localName = name
                if (prefixString && name.startsWith(prefixString)) {
                    localName = name.substring(prefixString.length())
                }
                localName = builder.getName(localName)
                if (builder.factories.containsKey(localName)) {
                    return builder.factories[localName]
                }
            } finally {
                builder.proxyBuilder = oldProxy
            }
        }
        if (factory) {
            if (name == prefixString) {
                return factory
            }
        }
        return null
    }

    Closure nominateExplicitMethod(String name) {
        if (builder) {
            // need to turn off proxy to get at class durring lookup
            def oldProxy = builder.proxyBuilder
            try {
                builder.proxyBuilder = builder
                String localName = name
                if (prefixString && name.startsWith(prefixString)) {
                    localName = name.substring(prefixString.length())
                }
                localName = builder.getName(localName)
                if (builder.getLocalExplicitMethods().containsKey(localName)) {
                    return builder.getLocalExplicitMethods()[localName]
                }
            } finally {
                builder.proxyBuilder = oldProxy
            }
        }
        return null
    }

    Closure[] nominateExplicitProperty(String name) {
        if (builder) {
            // need to turn off proxy to get at class durring lookup
            def oldProxy = builder.proxyBuilder
            try {
                builder.proxyBuilder = builder
                String localName = name
                if (prefixString && name.startsWith(prefixString)) {
                    localName = name.substring(prefixString.length())
                }
                localName = builder.getName(localName)
                if (builder.explicitProperties.containsKey(localName)) {
                    return builder.explicitProperties[localName]
                }
            } finally {
                builder.proxyBuilder = oldProxy
            }
        }
        return EMPTY_CLOSURE_ARRAY
    }

    String toString() {
        return "UberBuilderRegistration{ factory '$factory' builder '$builder' prefix '$prefixString' }"
    }
}
