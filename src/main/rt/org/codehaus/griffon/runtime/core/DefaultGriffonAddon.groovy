/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.codehaus.griffon.runtime.core

import griffon.core.GriffonApplication
import griffon.core.GriffonAddon

/**
 *
 * @author Andres Almiray
 *
 * @since 0.9.2
 */
class DefaultGriffonAddon extends AbstractGriffonAddon {
    private final Object addonDelegate
    
    DefaultGriffonAddon(GriffonApplication app, Object addonDelegate) {
        super(app, 'griffon.addon' + addonDelegate.class.name)
        this.addonDelegate = addonDelegate
    }

    void setApp(GriffonApplication app) {
        super.setApp(app)
        addonDelegate.metaClass.app = app
    }

    Map<String, ?> getFactories() {
        ignoreMissingProperty{ addonDelegate.factories } ?: [:]
    }

    Map<String, Closure> getMethods() {
        ignoreMissingProperty{ addonDelegate.methods } ?: [:]
    }

    Map<String, Map<String, Closure>> getProps() {
        ignoreMissingProperty{ addonDelegate.props } ?: [:]
    }

    Map<String, Closure> getEvents() {
        ignoreMissingProperty{ addonDelegate.events } ?: [:]
    }

    Map<String, Map<String, String>> getMvcGroups() {
        ignoreMissingProperty{ addonDelegate.mvcGroups } ?: [:]
    }

    List<Closure> getAttributeDelegates() {
        ignoreMissingProperty{ addonDelegate.attributeDelegates } ?: []
    }

    List<Closure> getPreInstantiateDelegates() {
        ignoreMissingProperty{ addonDelegate.preInstantiateDelegates } ?: []
    }

    List<Closure> getPostInstantiateDelegates() {
        ignoreMissingProperty{ addonDelegate.postInstantiateDelegates } ?: []
    }

    List<Closure> getPostNodeCompletionDelegates() {
        ignoreMissingProperty{ addonDelegate.postNodeCompletionDelegates } ?: []
    }

    void addonInit(GriffonApplication app) {
        ignoreMissingMethod('addonInit') { addonDelegate.addonInit(app) }
    }

    void addonPostInit(GriffonApplication app) {
        ignoreMissingMethod('addonPostInit') { addonDelegate.addonPostInit(app) }
    }

    void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {
        ignoreMissingMethod('addonBuilderInit') { addonDelegate.addonBuilderInit(app, builder) }
    }

    void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {
        ignoreMissingMethod('addonBuilderPostInit') { addonDelegate.addonBuilderPostInit(app, builder) }
    }

    private ignoreMissingProperty(Closure closure) {
        try {
            return closure()
        } catch(MissingPropertyException mpe) {
            // ignore
        }
        return null
    }

    private void ignoreMissingMethod(String methodName, Closure closure) {
        try {
            closure()
        } catch(MissingMethodException mme) {
            if(mme.method != methodName) throw mme
        }
    }
}
