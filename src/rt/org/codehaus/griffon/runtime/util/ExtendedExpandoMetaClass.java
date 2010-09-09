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
package org.codehaus.griffon.runtime.util;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.GroovySystem;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

/**
 *
 * @author Andres Almiray
 */
public class ExtendedExpandoMetaClass extends ExpandoMetaClass {
    public static MetaClass metaClassFor(Class clazz) {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
        if(!(metaClass instanceof ExtendedExpandoMetaClass)) {
            metaClass = new ExtendedExpandoMetaClass(clazz, true, true);
            metaClass.initialize();
            GroovySystem.getMetaClassRegistry().setMetaClass(clazz, metaClass);
        }
        return metaClass;
    }
    
    private Closure methodMissingHandler;
    private Closure getPropertyMissingHandler;
    private Closure setPropertyMissingHandler;
    private static final Class[] METHOD_MISSING_ARGS = new Class[]{String.class, Object[].class};

    public ExtendedExpandoMetaClass(Class theClass) {
        super(theClass);
    }

    public ExtendedExpandoMetaClass(Class theClass, MetaMethod[] add) {
        super(theClass, add);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register) {
        super(theClass, register);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register, MetaMethod[] add) {
        super(theClass, register, add);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register, boolean allowChangesAfterInit) {
        super(theClass, register, allowChangesAfterInit);
    }

    public void setMethodMissingHandler(Closure methodMissingHandler) {
        this.methodMissingHandler = methodMissingHandler;
    }

    public void setStaticMethodMissingHandler(Closure staticMethodMissingHandler) {
        registerStaticMethod(METHOD_MISSING, staticMethodMissingHandler, METHOD_MISSING_ARGS);
    }

    public void setGetPropertyMissingHandler(Closure getPropertyMissingHandler) {
        this.getPropertyMissingHandler = getPropertyMissingHandler;
    }

    public void setSetPropertyMissingHandler(Closure setPropertyMissingHandler) {
        this.setPropertyMissingHandler = setPropertyMissingHandler;
    }

    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        if(methodMissingHandler != null) {
            return methodMissingHandler.call(new Object[]{methodName, arguments});
        }
        return super.invokeMissingMethod(instance, methodName, arguments);
    }

    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        return isGetter? handleMissingProperty(instance, propertyName) : handleMissingProperty(instance, propertyName, optionalValue);
    }

    // get
    private Object handleMissingProperty(Object instance, String propertyName) {
        if(getPropertyMissingHandler != null) {
            return getPropertyMissingHandler.call(new Object[]{propertyName});
        }
        return super.invokeMissingProperty(instance, propertyName, null, true);
    }

    // set
    private Object handleMissingProperty(Object instance, String propertyName, Object value) {
        if(setPropertyMissingHandler != null) {
            setPropertyMissingHandler.call(new Object[]{propertyName, value});
            return null;
        }
        return super.invokeMissingProperty(instance, propertyName, value, false);
    }
}
