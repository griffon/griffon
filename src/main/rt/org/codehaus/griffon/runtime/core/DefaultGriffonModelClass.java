/*
 * Copyright 2010 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;
import griffon.core.GriffonModelClass;
import griffon.util.GriffonClassUtils;
import java.beans.PropertyDescriptor;

import groovy.lang.Closure;
import groovy.lang.MetaProperty;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public class DefaultGriffonModelClass extends DefaultGriffonClass implements GriffonModelClass {
    protected final Set<String> propertiesCache = new TreeSet<String>();
    private static final Set<String> BINDABLE_PROPERTIES = new TreeSet<String>(
        Arrays.asList("propertyChangeListeners", "vetoableChangeListeners"));

    public DefaultGriffonModelClass(GriffonApplication app, Class<?> clazz) {
        super(app, clazz, TYPE, TRAILING);
    }

    public void resetCaches() {
        super.resetCaches();
        propertiesCache.clear();
    }

    public String[] getPropertyNames() {
        if(propertiesCache.isEmpty()) {
            for(PropertyDescriptor pd : getPropertyDescriptors()) {
                 String propertyName = pd.getName();
                 if(!propertiesCache.contains(propertyName) &&
                    !GriffonClassUtils.isEventHandler(propertyName) &&
                    getPropertyValue(propertyName, Closure.class) == null &&
                    !STANDARD_PROPERTIES.contains(propertyName) &&
                    !BINDABLE_PROPERTIES.contains(propertyName)) {
                     propertiesCache.add(propertyName);
                 }
            }
            for(MetaProperty p : getMetaProperties()) {
                 String propertyName = p.getName();
                 if(GriffonClassUtils.isGetter(p, true)) {
                     propertyName = GriffonClassUtils.uncapitalize(propertyName.substring(3));
                 }
                 if(!propertiesCache.contains(propertyName) &&
                    !GriffonClassUtils.isEventHandler(propertyName) &&
                    !isClosureMetaProperty(p) &&
                    !STANDARD_PROPERTIES.contains(propertyName) &&
                    !BINDABLE_PROPERTIES.contains(propertyName)) {
                      propertiesCache.add(propertyName);
                 }
            }
        }
    
        return (String[]) propertiesCache.toArray(new String[propertiesCache.size()]);
    }
}
