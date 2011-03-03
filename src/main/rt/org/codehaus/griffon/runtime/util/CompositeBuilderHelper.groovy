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

import org.codehaus.griffon.runtime.builder.UberBuilder
import groovy.swing.factory.ComponentFactory
import groovy.swing.factory.LayoutFactory
import groovy.swing.factory.ScrollPaneFactory
import groovy.swing.factory.TableFactory
import java.awt.LayoutManager
import javax.swing.*
import griffon.core.GriffonApplication

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Helper class that initialzes a CompositeBuilder with the builder configuration read from the application.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class CompositeBuilderHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeBuilderHelper)

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
        if(features) LOG.debug("Applying 'features' config node to builders")
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
                case "properties":
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
        if(LOG.debugEnabled) LOG.debug("Initializing builder ${builderClass.name}")
        FactoryBuilderSupport localBuilder = uberBuilder.uberInit(prefixName, builderClass)
        for (partialTarget in builderClassName.value) {
            if (partialTarget.key == 'view') {
                // this needs special handling, skip it for now
                continue
            }

            MetaClass mc = targets[partialTarget.key]
            if(!mc) continue

            if(LOG.debugEnabled) LOG.debug("Injecting builder contributions to $partialTarget.key using ${partialTarget.value}")
            for (String injectionName in partialTarget.value) {
                def factories = localBuilder.getLocalFactories()
                def methods = localBuilder.getLocalExplicitMethods()
                def props = localBuilder.getLocalExplicitProperties()

                Closure processInjection = {String injectedName ->                
                    def resolvedName = "${prefixName}${injectedName}"
                    if (methods.containsKey(injectedName)) {
                        if(LOG.traceEnabled) LOG.trace("Injected method ${resolvedName}() on $partialTarget.key")
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
                            if(LOG.traceEnabled) LOG.trace("Injected getter for ${beanName} on $partialTarget.key")
                            mc."get$beanName" = accessors[0]
                        }
                        if (accessors[1]) {
                            if(LOG.traceEnabled) LOG.trace("Injected setter for ${beanName} on $partialTarget.key")
                            mc."set$beanName" = accessors[1]
                        }
                    } else if (factories.containsKey(injectedName)) {
                        if(LOG.traceEnabled) LOG.trace("Injected factory ${resolvedName} on $partialTarget.key")
                        mc."${resolvedName}" = {Object ... args -> uberBuilder."$resolvedName"(* args)}
                    }
                }

                if (injectionName == "*") {
                    for(group in localBuilder.getRegistrationGroups()) {
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
        // is it too naive to just call registerFactory/registerBeanFactory here ?
        // TODO handle catch-all groupName "*" ?
        groupedFactories.each {groupName, factories ->
            uberBuilder.registrationGroups.get(groupName, [] as TreeSet)
            factories.each {name, factory ->
                if (Factory.class.isAssignableFrom(factory.getClass())) {
                    uberBuilder.registerFactory(name, groupName, factory)
                } else {
                    registerBeanFactory(uberBuilder, name, groupName, factory)
                }
            }
        }
    }

    // FIXME copied from SwingBuilder
    // FIXME refactor for specific UI toolkit handler
    private static registerBeanFactory(UberBuilder uberBuilder, String nodeName, String groupName, Class klass) {
        // poke at the type to see if we need special handling
        if (LayoutManager.isAssignableFrom(klass)) {
            uberBuilder.registerFactory(nodeName, groupName, new LayoutFactory(klass))
        } else if (JScrollPane.isAssignableFrom(klass)) {
            uberBuilder.registerFactory(nodeName, groupName, new ScrollPaneFactory(klass))
        } else if (JTable.isAssignableFrom(klass)) {
            uberBuilder.registerFactory(nodeName, groupName, new TableFactory(klass))
        } else if (JComponent.isAssignableFrom(klass)
                || JApplet.isAssignableFrom(klass)
                || JDialog.isAssignableFrom(klass)
                || JFrame.isAssignableFrom(klass)
                || JWindow.isAssignableFrom(klass)
        ) {
            uberBuilder.registerFactory(nodeName, groupName, new ComponentFactory(klass))
        } else {
            uberBuilder.registerBeanFactory(nodeName, groupName, klass)
        }
    }

    private static addMethods(UberBuilder uberBuilder, groupedMethods) {
        // TODO handle catch-all groupName "*" ?
        groupedMethods.each {groupName, methods ->
            uberBuilder.registrationGroups.get(groupName, [] as TreeSet)
            methods.each {name, method ->
                uberBuilder.registerExplicitMethod(name, groupName, method)
            }
        }
    }

    private static addProperties(UberBuilder uberBuilder, groupedProperties) {
        // TODO handle catch-all groupName "*" ?
        groupedProperties.each {groupName, properties ->
            uberBuilder.registrationGroups.get(groupName, [] as TreeSet)
            properties.each {name, propertyTuple ->
                uberBuilder.registerExplicitProperty(name, groupName, propertyTuple.get, propertyTuple.set)
            }
        }
    }
}
