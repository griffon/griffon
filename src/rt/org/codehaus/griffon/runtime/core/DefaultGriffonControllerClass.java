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
import griffon.core.GriffonControllerClass;
import griffon.util.GriffonClassUtils;
import java.beans.PropertyDescriptor;

import groovy.lang.Closure;
import groovy.lang.MetaProperty;
import groovy.lang.MetaMethod;

import java.util.Set;
import java.util.TreeSet;
import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public class DefaultGriffonControllerClass extends DefaultGriffonClass implements GriffonControllerClass {
    protected final Set<String> actionsCache = new TreeSet<String>();

    public DefaultGriffonControllerClass(GriffonApplication app, Class<?> clazz) {
        super(app, clazz, TYPE, TRAILING);
    }

    public void resetCaches() {
	    super.resetCaches();
	    actionsCache.clear();
    }

    public String[] getActionNames() {
	    if(actionsCache.isEmpty()) {
	        for(PropertyDescriptor pd : getPropertyDescriptors()) {
		         String propertyName = pd.getName();
		         if(!actionsCache.contains(propertyName) &&
		            !GriffonClassUtils.isEventHandler(propertyName) &&
		            getPropertyValue(propertyName, Closure.class) != null) {
			          actionsCache.add(propertyName);
		         }
	        }
	        for(MetaProperty p : getMetaProperties()) {
		         String propertyName = p.getName();
		         if(GriffonClassUtils.isGetter(p, true)) {
			         propertyName = GriffonClassUtils.uncapitalize(propertyName.substring(3));
		         }
		         if(!actionsCache.contains(propertyName) &&
		            !GriffonClassUtils.isEventHandler(propertyName) &&
		            isClosureMetaProperty(p)) {
			          actionsCache.add(propertyName);
		         }
	        }/*
	        for(Method method : getClazz().getMethods()) {
		         String methodName = method.getName();
		         if(!actionsCache.contains(methodName) &&
		            GriffonClassUtils.isPlainMethod(method) &&
		            !GriffonClassUtils.isEventHandler(methodName)) {
			          actionsCache.add(methodName);
		         }
	        }*/
	        for(MetaMethod method : getMetaClass().getMethods()) {
		         String methodName = method.getName();
		         if(!actionsCache.contains(methodName) &&
		            GriffonClassUtils.isPlainMethod(method) &&
		            !GriffonClassUtils.isEventHandler(methodName)) {
			          actionsCache.add(methodName);
		         }
	        }
	    }
	
	    return (String[]) actionsCache.toArray(new String[actionsCache.size()]);
    }
}