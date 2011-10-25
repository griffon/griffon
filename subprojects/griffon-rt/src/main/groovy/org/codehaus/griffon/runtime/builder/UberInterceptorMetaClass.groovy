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

import griffon.util.MethodUtils
import java.lang.reflect.InvocationTargetException
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class UberInterceptorMetaClass extends DelegatingMetaClass {
    private static final Logger LOG = LoggerFactory.getLogger(UberInterceptorMetaClass)
    UberBuilder factory

    UberInterceptorMetaClass(MetaClass delegate, UberBuilder factory) {
        super(delegate)
        this.factory = factory
    }

    private Object doInvokeInstanceMethod(Object object, String methodName, Object arguments) {
        Class klass = object instanceof Class ? object : object.getClass()
        try {
            return MethodUtils.invokeMethod(object, methodName, arguments)
        } catch (NoSuchMethodException nsme) {
            throw new MissingMethodException(methodName, klass, [arguments] as Object[])
        } catch (IllegalAccessException iae) {
            throw new MissingMethodException(methodName, klass, [arguments] as Object[])
        } catch (InvocationTargetException ite) {
            throw new MissingMethodException(methodName, klass, [arguments] as Object[])
        }
    }

    private Object doInvokeInstanceMethod(Object object, String methodName, Object[] arguments) {
        Class klass = object instanceof Class ? object : object.getClass()
        try {
            return MethodUtils.invokeMethod(object, methodName, arguments)
        } catch (NoSuchMethodException nsme) {
            throw new MissingMethodException(methodName, klass, arguments)
        } catch (IllegalAccessException iae) {
            throw new MissingMethodException(methodName, klass, arguments)
        } catch (InvocationTargetException ite) {
            throw new MissingMethodException(methodName, klass, arguments)
        }
    }

    private Object doInvokeStaticMethod(Object object, String methodName, Object[] arguments) {
        Class klass = object instanceof Class ? object : object.getClass()
        try {
            return MethodUtils.invokeStaticMethod(klass, methodName, arguments)
        } catch (NoSuchMethodException nsme) {
            throw new MissingMethodException(methodName, klass, arguments)
        } catch (IllegalAccessException iae) {
            throw new MissingMethodException(methodName, klass, arguments)
        } catch (InvocationTargetException ite) {
            throw new MissingMethodException(methodName, klass, arguments)
        }
    }

    private Object invokeFactoryMethod(String methodName, Object arguments, MissingMethodException mme) {
        try {
            return factory.invokeMethod(methodName, arguments)
        } catch (MissingMethodException mme2) {
            if (mme2.method != methodName) {
                throw mme2
            }
            // chain secondary exception
            Throwable root = mme
            while (root.getCause() != null) {
                root = root.getCause()
            }
            root.initCause(mme2)
            // throw original
            throw mme
        }
    }

    private Object invokeFactoryMethod(String methodName, Object[] arguments, MissingMethodException mme) {
        try {
            return factory.invokeMethod(methodName, arguments)
        } catch (MissingMethodException mme2) {
            if (mme2.method != methodName) {
                throw mme2
            }
            // chain secondary exception
            Throwable root = mme
            while (root.getCause() != null) {
                root = root.getCause()
            }
            root.initCause(mme2)
            // throw original
            throw mme
        }
    }

    private void exceptionIfMethodNotFound(String methodName, MissingMethodException mme) {
        if (mme.method != methodName) {
            throw mme
        }
    }

    Object invokeMethod(Object object, String methodName, Object arguments) {
        // try {
        //     return invokeMethod(object, methodName, arguments);
        // } catch (MissingMethodException mme) {
        //     exceptionIfMethodNotFound(methodName, mme);
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme2) {
            exceptionIfMethodNotFound(methodName, mme2);
            // attempt method resolution
            for (UberBuilderRegistration reg in factory.builderRegistration) {
                try {
                    def builder = reg.builder
                    if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                        return InvokerHelper.invokeMethod(builder, methodName, arguments)
                    }
                } catch (MissingMethodException mme3) {
                    exceptionIfMethodNotFound(methodName, mme3);
                    // drop the exception, there will be many
                }
            }
            // dispatch to factories if it is not a literal method
            return invokeFactoryMethod(methodName, arguments, mme2)
        }
        // }
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        // try {
        //     return invokeMethod(object, methodName, arguments);
        // } catch (MissingMethodException mme) {
        //     exceptionIfMethodNotFound(methodName, mme);
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme2) {
            exceptionIfMethodNotFound(methodName, mme2);
            // attempt method resolution
            for (UberBuilderRegistration reg in factory.builderRegistration) {
                try {
                    def builder = reg.builder
                    if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                        return InvokerHelper.invokeMethod(builder, methodName, arguments)
                    }
                } catch (MissingMethodException mme3) {
                    exceptionIfMethodNotFound(methodName, mme3);
                    // drop the exception, there will be many
                }
            }
            // dispatch to factories if it is not a literal method
            return invokeFactoryMethod(methodName, arguments, mme2)
        }
        // }
    }

    Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        try {
            if (object instanceof Class) {
                return doInvokeInstanceMethod(object, methodName, arguments)
            } else {
                return doInvokeStaticMethod(object, methodName, arguments)
            }
        } catch (MissingMethodException mme) {
            exceptionIfMethodNotFound(methodName, mme);
            try {
                return delegate.invokeMethod(object, methodName, arguments)
            } catch (MissingMethodException mme2) {
                exceptionIfMethodNotFound(methodName, mme2);

                // attempt method resolution
                for (UberBuilderRegistration reg in factory.builderRegistration) {
                    try {
                        def builder = reg.builder
                        if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                            return InvokerHelper.invokeMethod(builder, methodName, arguments)
                        }
                    } catch (MissingMethodException mme3) {
                        exceptionIfMethodNotFound(methodName, mme3);

                        // drop the exception, there will be many
                    }
                }
                // dispatch to factories if it is not a literal method
                return invokeFactoryMethod(methodName, arguments, mme2)
            }
        }
    }

    Object getProperty(Object o, String s) {
        try {
            return super.getProperty(o, s)
        } catch (MissingPropertyException mpe) {
            return factory.getProperty(s)
        }
    }

    void setProperty(Object o, String s, Object o1) {
        try {
            super.setProperty(o, s, o1)
        } catch (MissingPropertyException mpe) {
            factory.setProperty(s, o1)
        }
    }
}
