/*
 * Copyright 2007-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.builder

import griffon.core.GriffonArtifact

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Danno.Ferrin
 * Date: Nov 7, 2007
 * Time: 2:50:58 PM
 */
class UberBuilder extends FactoryBuilderSupport {
    private static final Logger LOG = LoggerFactory.getLogger(UberBuilder)
    protected final Map builderLookup = new LinkedHashMap()
    protected final List<UberBuilderRegistration> builderRegistration = [] as LinkedList

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
            try{
                MetaClass scriptMetaClass = script.getMetaClass()
                boolean isArtifact = script instanceof GriffonArtifact
                if(isArtifact) scriptMetaClass = script.getGriffonClass().getMetaClass()
                if(!(scriptMetaClass instanceof UberInterceptorMetaClass)) {
                    MetaClass uberMetaClass = new UberInterceptorMetaClass(scriptMetaClass, this)
                    script.setMetaClass(uberMetaClass)
                    if(isArtifact) script.getGriffonClass().setMetaClass(uberMetaClass)
                }
                script.setBinding(this)
                return script.run()
            } catch(x){
                if(LOG.errorEnabled) LOG.error("An error occurred while building $script", x)
                throw x
            }
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
