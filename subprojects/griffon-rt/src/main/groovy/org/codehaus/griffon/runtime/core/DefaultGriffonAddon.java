/*
 * Copyright 2010-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;
import groovy.lang.*;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 0.9.2
 */
public class DefaultGriffonAddon extends AbstractGriffonAddon {
    private final Object addonDelegate;

    public DefaultGriffonAddon(GriffonApplication app, Object addonDelegate) {
        super(app, "griffon.addon" + addonDelegate.getClass().getName());
        this.addonDelegate = addonDelegate;
    }

    public void setApp(GriffonApplication app) {
        MetaClass metaClass = InvokerHelper.getMetaClass(addonDelegate);
        if (metaClass instanceof ExpandoMetaClass) {
            ExpandoMetaClass mc = (ExpandoMetaClass) metaClass;
            mc.registerBeanProperty("app", app);
        }
    }

    public Map<String, Object> getFactories() {
        try {
            return (Map<String, Object>) InvokerHelper.getProperty(addonDelegate, "factories");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Closure> getMethods() {
        try {
            return (Map<String, Closure>) InvokerHelper.getProperty(addonDelegate, "methods");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Map<String, Closure>> getProps() {
        try {
            return (Map<String, Map<String, Closure>>) InvokerHelper.getProperty(addonDelegate, "props");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Closure> getEvents() {
        try {
            return (Map<String, Closure>) InvokerHelper.getProperty(addonDelegate, "events");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Map<String, Object>> getMvcGroups() {
        try {
            return (Map<String, Map<String, Object>>) InvokerHelper.getProperty(addonDelegate, "mvcGroups");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public List<Closure> getAttributeDelegates() {
        try {
            return (List<Closure>) InvokerHelper.getProperty(addonDelegate, "attributeDelegates");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyList();
        }
    }

    public List<Closure> getPreInstantiateDelegates() {
        try {
            return (List<Closure>) InvokerHelper.getProperty(addonDelegate, "preInstantiateDelegates");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyList();
        }
    }

    public List<Closure> getPostInstantiateDelegates() {
        try {
            return (List<Closure>) InvokerHelper.getProperty(addonDelegate, "postInstantiateDelegates");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyList();
        }
    }

    public List<Closure> getPostNodeCompletionDelegates() {
        try {
            return (List<Closure>) InvokerHelper.getProperty(addonDelegate, "postNodeCompletionDelegates");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyList();
        }
    }

    public Map<String,Map<String, Object>> getActionInterceptors() {
        try {
            return (Map<String, Map<String, Object>>) InvokerHelper.getProperty(addonDelegate, "actionInterceptors");
        } catch (MissingPropertyException mpe) {
            return Collections.emptyMap();
        }
    }

    public void addonInit(GriffonApplication app) {
        try {
            InvokerHelper.invokeMethod(addonDelegate, "addonInit", app);
        } catch (MissingMethodException mme) {
            if (!mme.getMethod().equals("addonInit")) {
                throw mme;
            }
        }
    }

    public void addonPostInit(GriffonApplication app) {
        try {
            InvokerHelper.invokeMethod(addonDelegate, "addonPostInit", app);
        } catch (MissingMethodException mme) {
            if (!mme.getMethod().equals("addonPostInit")) {
                throw mme;
            }
        }
    }

    public void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {
        try {
            InvokerHelper.invokeMethod(addonDelegate, "addonBuilderInit", new Object[]{app, builder});
        } catch (MissingMethodException mme) {
            if (!mme.getMethod().equals("addonBuilderInit")) {
                throw mme;
            }
        }
    }

    public void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {
        try {
            InvokerHelper.invokeMethod(addonDelegate, "addonBuilderPostInit", new Object[]{app, builder});
        } catch (MissingMethodException mme) {
            if (!mme.getMethod().equals("addonBuilderPostInit")) {
                throw mme;
            }
        }
    }
}
