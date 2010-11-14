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
package org.codehaus.griffon.runtime.metaclass;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.GroovySystem;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;

import org.codehaus.groovy.runtime.MethodClosure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
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
    
    private MethodInterceptor methodInterceptor;
    private final Logger log;
    private static final Class[] METHOD_MISSING_ARGS = new Class[]{String.class, Object[].class};

    public ExtendedExpandoMetaClass(Class theClass) {
        super(theClass);
        log = createLogger(theClass);
    }

    public ExtendedExpandoMetaClass(Class theClass, MetaMethod[] add) {
        super(theClass, add);
        log = createLogger(theClass);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register) {
        super(theClass, register);
        log = createLogger(theClass);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register, MetaMethod[] add) {
        super(theClass, register, add);
        log = createLogger(theClass);
    }

    public ExtendedExpandoMetaClass(Class theClass, boolean register, boolean allowChangesAfterInit) {
        super(theClass, register, allowChangesAfterInit);
        log = createLogger(theClass);
    }
    
    private static Logger createLogger(Class theClass) {
        return LoggerFactory.getLogger(ExtendedExpandoMetaClass.class.getName()+"["+ theClass.getName() +"]");
    }
    
    public boolean isAdjusted() {
        return methodInterceptor != null;
    }
    
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        if(methodInterceptor != null) {
        log.debug("Setting methodInterceptor "+methodInterceptor);
            this.methodInterceptor = methodInterceptor;
            registerStaticMethod(METHOD_MISSING, new MethodClosure(methodInterceptor, "callInvokeMethod"), METHOD_MISSING_ARGS);
        }
    }

    public Object invokeMethod(Object instance, String methodName, Object[] arguments) {
        try {
            log.debug("Invoking method "+methodName+"()");
            return super.invokeMethod(instance, methodName, arguments);
        } catch(MissingMethodException e) {
            return invokeMissingMethod(instance, methodName, arguments);
        }
    }

    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        if(methodInterceptor != null) {
            log.debug("Invoking method "+methodName+"() via methodInterceptor");
            return methodInterceptor.callInvokeMethod(methodName, arguments);
        }
        log.debug("Invoking method "+methodName+"() via super.invokeMissingMethod()");
        return super.invokeMissingMethod(instance, methodName, arguments);
    }

    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        return isGetter? handleMissingProperty(instance, propertyName) : handleMissingProperty(instance, propertyName, optionalValue);
    }

    // get
    private Object handleMissingProperty(Object instance, String propertyName) {
        if(methodInterceptor != null) {
            log.debug("Getting property "+propertyName+" via methodInterceptor");
            return methodInterceptor.callGetProperty(propertyName);
        }
        log.debug("Getting property "+propertyName+" via super.invokeMissingProperty");
        return super.invokeMissingProperty(instance, propertyName, null, true);
    }

    // set
    private Object handleMissingProperty(Object instance, String propertyName, Object value) {
        if(methodInterceptor != null) {
            log.debug("Setting property "+propertyName+" via methodInterceptor");
            methodInterceptor.callSetProperty(propertyName, value);
            return null;
        }
        log.debug("Setting property "+propertyName+" via super.invokeMissingProperty");
        return super.invokeMissingProperty(instance, propertyName, value, false);
    }
}
