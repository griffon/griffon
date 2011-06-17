/*
 * Copyright 2008-2011 the original author or authors.
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
package org.codehaus.griffon.runtime.util

import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.builder.UberBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Helper class that initializes a CompositeBuilder with the builder configuration read from the application.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class CompositeBuilderHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeBuilderHelper)
    private final static CompositeBuilderCustomizer builderCustomizer

    static {
        ClassLoader classLoader = CompositeBuilderHelper.class.classLoader
        try {
            URL url = classLoader.getResource('META-INF/services/' + CompositeBuilderCustomizer.class.name)
            String className = url.text.trim()
            builderCustomizer = classLoader.loadClass(className).newInstance()
        } catch (Exception e) {
            builderCustomizer = new DefaultCompositeBuilderCustomizer()
        }
    }

    static FactoryBuilderSupport createBuilder(GriffonApplication app, Map<String, MetaClass> targets) {
        UberBuilder uberBuilder = new UberBuilder()
        uberBuilder.setProperty('app', app)

        LOG.debug('Configuring builders with addon contributions')
        AddonHelper.handleAddonsForBuilders(app, uberBuilder, targets)

        for (node in app.builderConfig) {
            String nodeName = node.key
            switch (nodeName) {
                case "features":
                    handleFeatures(uberBuilder, node.value)
                    break
                default:
                    if (nodeName == "root") nodeName = ""
                    node.value.each {builder ->
                        handleLocalBuilder(uberBuilder, targets, nodeName, builder)
                    }
            }
        }

        return uberBuilder
    }

    static handleFeatures(UberBuilder uberBuilder, features) {
        if (features) LOG.debug("Applying 'features' config node to builders")
        for (feature in features) {
            switch (feature.key) {
                case ~/.*Delegates/:
                    def delegateType = feature.key - "s"
                    delegateType = delegateType[0].toUpperCase() + delegateType[1..-1]
                    feature.value.each {delegateValue ->
                        uberBuilder."add$delegateType"(delegateValue)
                    }
                    break
                case "factories":
                    addFactories(uberBuilder, feature.value)
                    break
                case "methods":
                    addMethods(uberBuilder, feature.value)
                    break
                case "props":
                    addProperties(uberBuilder, feature.value)
                    break
            }
        }
    }

    static handleLocalBuilder(UberBuilder uberBuilder, Map<String, MetaClass> targets, String prefixName, builderClassName) {
        Class builderClass = Class.forName(builderClassName.key) //FIXME get correct classloader
        if (!FactoryBuilderSupport.isAssignableFrom(builderClass)) {
            return;
        }
        if (LOG.debugEnabled) LOG.debug("Initializing builder ${builderClass.name}")
        FactoryBuilderSupport localBuilder = uberBuilder.uberInit(prefixName, builderClass)
        for (partialTarget in builderClassName.value) {
            if (partialTarget.key == 'view') {
                // this needs special handling, skip it for now
                continue
            }

            MetaClass mc = targets[partialTarget.key]
            if (!mc) continue

            if (LOG.debugEnabled) LOG.debug("Injecting builder contributions to $partialTarget.key using ${partialTarget.value}")
            for (String injectionName in partialTarget.value) {
                def factories = localBuilder.getLocalFactories()
                def methods = localBuilder.getLocalExplicitMethods()
                def props = localBuilder.getLocalExplicitProperties()

                Closure processInjection = {String injectedName ->
                    String resolvedName = prefixName + injectedName
                    if (methods.containsKey(injectedName)) {
                        if (LOG.traceEnabled) LOG.trace("Injected method ${resolvedName}() on $partialTarget.key")
                        mc."$resolvedName" = methods[injectedName]
                    } else if (props.containsKey(injectedName)) {
                        Closure[] accessors = props[injectedName]
                        String beanName
                        if (injectedName.length() > 1) {
                            beanName = injectedName[0].toUpperCase() + injectedName.substring(1)
                        } else {
                            beanName = injectedName[0].toUpperCase()
                        }
                        if (accessors[0]) {
                            if (LOG.traceEnabled) LOG.trace("Injected getter for ${beanName} on $partialTarget.key")
                            mc."get$beanName" = accessors[0]
                        }
                        if (accessors[1]) {
                            if (LOG.traceEnabled) LOG.trace("Injected setter for ${beanName} on $partialTarget.key")
                            mc."set$beanName" = accessors[1]
                        }
                    } else if (factories.containsKey(injectedName)) {
                        if (LOG.traceEnabled) LOG.trace("Injected factory ${resolvedName} on $partialTarget.key")
                        mc."${resolvedName}" = {Object... args -> uberBuilder."$resolvedName"(* args)}
                    }
                }

                if (injectionName == "*") {
                    for (group in localBuilder.getRegistrationGroups()) {
                        localBuilder.getRegistrationGroupItems(group).each processInjection
                    }
                    continue
                }

                def groupItems = localBuilder.getRegistrationGroupItems(injectionName)
                if (groupItems) {
                    groupItems.each processInjection
                } else {
                    processInjection(injectionName)
                }
            }
        }
    }

    private static addFactories(UberBuilder uberBuilder, groupedFactories) {
        for (group in groupedFactories) {
            String groupName = group.key
            groupName = groupName == 'root' || groupName == '*' ? '' : groupName
            uberBuilder.@registrationGroup.get(groupName, [] as TreeSet)
            group.value.each {name, factory ->
                if (Factory.class.isAssignableFrom(factory.getClass())) {
                    builderCustomizer.registerFactory(uberBuilder, name, groupName, factory)
                } else if (factory instanceof Class) {
                    builderCustomizer.registerBeanFactory(uberBuilder, name, groupName, factory)
                } else {
                    throw new IllegalArgumentException("[builder config] value of factory '$groupName:$name' is neither a Factory nor a Class instance.")
                }
            }
        }
    }

    private static addMethods(UberBuilder uberBuilder, groupedMethods) {
        for (group in groupedMethods) {
            String groupName = group.key
            groupName = groupName == 'root' || groupName == '*' ? '' : groupName
            uberBuilder.@registrationGroup.get(groupName, [] as TreeSet)
            group.value.each {name, method ->
                if (method instanceof Closure) {
                    builderCustomizer.registerExplicitMethod(uberBuilder, name, groupName, method)
                } else {
                    throw new IllegalArgumentException("[builder config] value of method '$groupName:$name' is not a Closure.")
                }
            }
        }
    }

    private static addProperties(UberBuilder uberBuilder, groupedProperties) {
        for (group in groupedProperties) {
            String groupName = group.key
            groupName = groupName == 'root' || groupName == '*' ? '' : groupName
            uberBuilder.@registrationGroup.get(groupName, [] as TreeSet)
            group.value.each {name, propertyTuple ->
                builderCustomizer.registerExplicitProperty(uberBuilder, name, groupName, propertyTuple.get, propertyTuple.set)
            }
        }
    }
}
