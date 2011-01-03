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

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class UberInterceptorMetaClass extends DelegatingMetaClass {
    UberBuilder factory

    UberInterceptorMetaClass(MetaClass delegate, UberBuilder factory) {
        super(delegate)
        this.factory = factory
    }

    Object invokeMethod(Object object, String methodName, Object arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }
            // attempt method resolution
            for (UberBuilderRegistration reg in factory.builderRegistration) {
                try {
                    def builder = reg.builder
                    if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                        return InvokerHelper.invokeMethod(builder, methodName, arguments)
                    }
                } catch (MissingMethodException mme2) {
                    if (mme2.method != methodName) {
                        throw mme2
                    }
                    // drop the exception, there will be many
                }
            }
            // dispatch to factories if it is not a literal method
            try {
                return factory.invokeMethod(methodName, arguments)
            } catch (MissingMethodException mme2) {
                if (mme2.method != methodName) {
                    throw mme2
                }
                //LOGME mme2.printStackTrace(System.out)
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
    }

    Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }

            // attempt method resolution
            for (UberBuilderRegistration reg in factory.builderRegistration) {
                try {
                    def builder = reg.builder
                    if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                        return InvokerHelper.invokeMethod(builder, methodName, arguments)
                    }
                } catch (MissingMethodException mme2) {
                    if (mme2.method != methodName) {
                        throw mme2
                    }

                    // drop the exception, there will be many
                }
            }
            // dispatch to factories if it is not a literal method
            try {
                return factory.invokeMethod(methodName, arguments)
            } catch (MissingMethodException mme2) {
                if (mme2.method != methodName) {
                    throw mme
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
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }
            // attempt method resolution
            for (UberBuilderRegistration reg in factory.builderRegistration) {
                try {
                    def builder = reg.builder
                    if (!builder.getMetaClass().respondsTo(builder, methodName).isEmpty()) {
                        return InvokerHelper.invokeMethod(builder, methodName, arguments)
                    }
                } catch (MissingMethodException mme2) {
                    if (mme2.method != methodName) {
                        throw mme2
                    }
                    // drop the exception, there will be many
                }
            }
            // dispatch to factories if it is not a literal method
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
    }

    Object getProperty(Object o, String s) {
        try {
            return factory.getProperty(s)
        } catch (MissingPropertyException mpe) {
            //LOGME mpe.printStackTrace(System.out)
            return super.getProperty(o, s)
        }
    }

    void setProperty(Object o, String s, Object o1) {
        try {
            factory.setProperty(s, o1)
        } catch (MissingPropertyException mpe) {
            mpe.printStackTrace(System.out)
            super.getProperty(o, s, o1)
        }
    }
}
