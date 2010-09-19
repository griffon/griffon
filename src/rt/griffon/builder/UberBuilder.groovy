/*
 * Copyright 2007-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package griffon.builder

import griffon.core.GriffonView
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.griffon.runtime.util.ExtendedExpandoMetaClass

/**
 * @author Danno.Ferrin
 * Date: Nov 7, 2007
 * Time: 2:50:58 PM
 */
class UberBuilder extends FactoryBuilderSupport {

    protected final Map builderLookup = new LinkedHashMap()
    protected final List<UberBuilderRegistration> builderRegistration = new LinkedList<UberBuilderRegistration>()

    public UberBuilder() {
        loadBuilderLookups()
    }

    public UberBuilder(Object[] builders) {
        this()
        builders.each {if (it) uberInit(null, it)}
    }

    protected Object loadBuilderLookups() {}

    public final uberInit(Object prefix, Map builders) {
        if (prefix) {
            throw new IllegalArgumentException("Prefixed maps not supported")
        } else {
            return builders.collect {k, v -> uberInit(k, v)}
        }
    }

    public final uberInit(Object prefix, Object[] builders) {
        if (prefix) {
            throw new IllegalArgumentException("Prefixed maps not supported")
        } else {
            return builders.collect {v -> uberInit(prefix, v)}
        }
    }

    public final uberInit(Object prefix, Object builderKey) {
        def builder = builderLookup[builderKey]
        // make sure we won't self-loop
        if (builder && (builder != builderKey)) {
            // if we get more than one, we have more than this base case, so look it up
            return uberInit(prefix, builder)
        } else {
            throw new IllegalArgumentException("Cannot uberinit indirectly via key '$builderKey'")
        }
    }

    protected uberInit(Object prefix, Class klass) {
        if (builderLookup.containsKey(klass)) {
            return uberInit(prefix, builderLookup[klass])
        } else if (FactoryBuilderSupport.isAssignableFrom(klass)) {
            return uberInit(prefix, klass.newInstance())
        } else {
            throw new IllegalArgumentException("Cannot uberinit indirectly from class'${klass.name}'")
        }
    }

    protected uberInit(Object prefix, FactoryBuilderSupport fbs) {
        builderRegistration.add(new UberBuilderRegistration(prefix, fbs))
        getVariables().putAll(fbs.variables)
        fbs.variables.clear()
        for (Closure delegate in fbs.attributeDelegates) {
            delegate.delegate = fbs
            proxyBuilder.@attributeDelegates.add(delegate)
        }
        for (Closure delegate in fbs.preInstantiateDelegates) {
            delegate.delegate = fbs
            proxyBuilder.@preInstantiateDelegates.add(delegate)
        }
        for (Closure delegate in fbs.postInstantiateDelegates) {
            delegate.delegate = fbs
            proxyBuilder.@postInstantiateDelegates.add(delegate)
        }
        for (Closure delegate in fbs.postNodeCompletionDelegates) {
            delegate.delegate = fbs
            proxyBuilder.@postNodeCompletionDelegates.add(delegate)
        }

        fbs.setProxyBuilder(this)
        return fbs
    }

    protected uberInit(Object prefix, Factory factory) {
        builderRegistration.add(new UberBuilderRegistration(prefix, factory))
    }

    Factory resolveFactory(Object name, Map attributes, Object value) {
        for (UberBuilderRegistration ubr in builderRegistration) {
            Factory factory = ubr.nominateFactory(name)
            if (factory) {
                if (ubr.builder) {
                    getProxyBuilder().getContext().put(CHILD_BUILDER, ubr.builder)
                } else {
                    getProxyBuilder().getContext().put(CHILD_BUILDER, proxyBuilder)
                }

                return factory
            }
        }
        return super.resolveFactory(name, attributes, value)
    }

    protected Closure resolveExplicitMethod(String methodName, Object args) {
        for (UberBuilderRegistration ubr in builderRegistration) {
            Closure explcitMethod = ubr.nominateExplicitMethod(methodName)
            if (explcitMethod) {
                return explcitMethod
            }
        }
        return super.resolveExplicitMethod(methodName, args)
    }

    protected void setClosureDelegate(Closure closure, Object node) {
        closure.setDelegate(currentBuilder)
    }

    public Object build(Script script) {
        synchronized (script) {
            MetaClass scriptMetaClass = script.getMetaClass()
/*
            script.setMetaClass(new UberInterceptorMetaClass(scriptMetaClass, new UberBuilderInterceptor(this)))
*/
            if(script instanceof GriffonView) {
                 UberBuilderInterceptor interceptor = new UberBuilderInterceptor(this)
                 ExtendedExpandoMetaClass mc = script.getGriffonClass().getMetaClass()
                 mc.setMethodMissingHandler(interceptor.&callInvokeMethod)
                 mc.setSstaticMethodMissingHandler(interceptor.&callInvokeMethod)
                 mc.setGetPropertyMissingHandler(interceptor.&callGetProperty)
                 mc.setSetPropertyMissingHandler(interceptor.&callSetProperty)
            } else {
                script.setMetaClass(new UberInterceptorMetaClass(scriptMetaClass, new UberBuilderInterceptor(this)))
            }

            script.setBinding(this)
            return script.run()
        }
    }

    public Object getProperty(String property) {
        for (UberBuilderRegistration ubr in builderRegistration) {
            Closure[] accessors = ubr.nominateExplicitProperty(property)
            if (accessors) {
                if (accessors[0] == null) {
                    // write only property
                    throw new MissingPropertyException(property + " is declared as write only")
                } else {
                    return accessors[0].call()
                }
            }
        }
        return super.getProperty(property)
    }

    public void setProperty(String property, Object newValue) {
        for (UberBuilderRegistration ubr in builderRegistration) {
            Closure[] accessors = ubr.nominateExplicitProperty(property)
            if (accessors) {
                if (accessors[1] == null) {
                    // read only property
                    throw new MissingPropertyException(property + " is declared as read only")
                } else {
                    accessors[1].call(newValue)
                }
            }
        }
        super.setProperty(property, newValue)
    }

    public void dispose() {
        builderRegistration.each {UberBuilderRegistration ubr ->
            try {
                ubr.builder.dispose()
            } catch(UnsupportedOperationException uoe) {
                // Sometimes an UOE may appear due to a TriggerBinding
                // see http://jira.codehaus.org/browse/GRIFFON-165
                // however there is little that can be done so we
                // ignore the exception for the time being
            }
        }
        super.dispose()
    }
}

class UberBuilderRegistration {

    Factory factory
    FactoryBuilderSupport builder
    String prefixString

    public UberBuilderRegistration(String prefixString, FactoryBuilderSupport builder) {
        this.@prefixString = prefixString
        this.@builder = builder
    }

    public UberBuilderRegistration(String prefixString, Factory factory) {
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
        return null
    }

    public String toString() {
        return "UberBuilderRegistration{ factory '$factory' builder '$builder' prefix '$prefixString'"
    }
}

class UberBuilderInterceptor {
    final UberBuilder uberBuilder
    
    UberBuilderInterceptor(UberBuilder uberBuilder) {
        this.uberBuilder = uberBuilder
    }

    Object callInvokeMethod(String methodName, Object[] arguments) {
        // attempt method resolution
        for (UberBuilderRegistration reg in uberBuilder.builderRegistration) {
            try {
                def builder = reg.builder
                if (builder.getMetaClass().respondsTo(builder, methodName)) {
                    return InvokerHelper.invokeMethod(builder, methodName, arguments)
                }
            } catch (MissingMethodException mme) {
                if (mme.method != methodName) {
                    throw mme
                }
                // drop the exception, there will be many
            }
        }

        return uberBuilder.invokeMethod(methodName, arguments)
    }

    Object callGetProperty(String propertyName) {
        uberBuilder.getProperty(propertyName)
    }

    void callSetProperty(String propertyName, Object value) {
        uberBuilder.setProperty(propertyName, value)
    }
}

class UberInterceptorMetaClass extends DelegatingMetaClass {
    UberBuilderInterceptor interceptor

    public UberInterceptorMetaClass(MetaClass delegate, UberBuilderInterceptor interceptor) {
        super(delegate)
        this.interceptor = interceptor
    }

    public Object invokeMethod(Object object, String methodName, Object arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }

            try {
                return interceptor.callInvokeMethod(methodName, arguments)
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

    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }

            try {
                return interceptor.callInvokeMethod(methodName, arguments)
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

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        try {
            return delegate.invokeMethod(object, methodName, arguments)
        } catch (MissingMethodException mme) {
            if (mme.method != methodName) {
                throw mme
            }

            try {
                return interceptor.callInvokeMethod(methodName, arguments)
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

    public Object getProperty(Object o, String s) {
        try {
            return interceptor.callGetProperty(s)
        } catch (MissingPropertyException mpe) {
            return super.getProperty(o, s)
        }
    }

    public void setProperty(Object o, String s, Object o1) {
        try {
            interceptor.callSetProperty(s, o1)
        } catch (MissingPropertyException mpe) {
            // mpe.printStackTrace(System.out)
            super.getProperty(o, s, o1)
        }
    }
}
