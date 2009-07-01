/*
 * Copyright 2008 the original author or authors.
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
package griffon.util

import griffon.builder.UberBuilder
import groovy.swing.factory.ComponentFactory
import groovy.swing.factory.LayoutFactory
import groovy.swing.factory.ScrollPaneFactory
import groovy.swing.factory.TableFactory
import java.awt.LayoutManager
import javax.swing.*

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Jul 29, 2008
 * Time: 5:58:16 PM
 */
class CompositeBuilderHelper {

    public static FactoryBuilderSupport createBuilder(IGriffonApplication app, Map targets) {
        UberBuilder uberBuilder = new UberBuilder()
        uberBuilder.setProperty('app', app)

        AddonHelper.handleAddonsForBuilders(app, uberBuilder)

        for (node in app.builderConfig) {
            String nodeName = node.key
            switch (nodeName) {
                case "addons" :
                    handleAddonsAtStartup(app, uberBuilder, node.value)
                    break
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

    private static handleFeatures(UberBuilder uberBuilder, features) {
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

    private static handleLocalBuilder(UberBuilder uberBuilder, Map targets, String prefixName, builderClassName) {
        Class builderClass = Class.forName(builderClassName.key) //FIXME get correct classloader
        FactoryBuilderSupport localBuilder = uberBuilder.uberInit(prefixName, builderClass)
        for (partialTarget in builderClassName.value) {
            if (partialTarget == 'view') {
                // this needs special handling, skip it for now
                continue
            }
            MetaClass mc = targets[partialTarget.key]?.getMetaClass()
            if (!mc) continue
            for (groupName in partialTarget.value) {
                if (groupName == "*") {
                    //FIXME handle add-all
                    continue
                }
                def factories = localBuilder.getLocalFactories()
                def methods = localBuilder.getLocalExplicitMethods()
                def properties = localBuilder.getLocalExplicitProperties()
                def groupItems = localBuilder.getRegistrationGroupItems(groupName)
                if (!groupItems) {
                    continue
                }
                for (itemName in groupItems) {
                    def resolvedName = "${prefixName}${itemName}"
                    if (methods.containsKey(itemName)) {
                        mc."$resolvedName" = methods[itemName]
                    } else if (properties.containsKey(itemName)) {
                        Closure[] accessors = properties[itemName];
                        String beanName
                        if (itemName.length() > 1) {
                            beanName = itemName[0].toUpperCase() + itemName.substring(1)
                        } else {
                            beanName = itemName[0].toUpperCase()
                        }
                        if (accessors[0]) {
                            mc."get$beanName" = accessors[0]
                        }
                        if (accessors[1]) {
                            mc."set$beanName" = accessors[1]
                        }
                    } else if (factories.containsKey(itemName)) {
                        mc."${resolvedName}" = {Object ... args -> uberBuilder."$resolvedName"(* args)}
                    }
                }
            }
        }
    }

    private static addFactories(UberBuilder uberBuilder, groupedFactories) {
        // is it too naive to just call registerFactory/registerBeanFactory here ?
        // TODO handle catch-all groupName "*" ?
        groupedFactories.each {groupName, factories ->
            uberBuilder.registrationGroups.get(groupName, new TreeSet<String>())
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
            uberBuilder.registrationGroups.get(groupName, new TreeSet<String>())
            methods.each {name, method ->
                uberBuilder.registerExplicitMethod(name, groupName, method)
            }
        }
    }

    private static addProperties(UberBuilder uberBuilder, groupedProperties) {
        // TODO handle catch-all groupName "*" ?
        groupedProperties.each {groupName, properties ->
            uberBuilder.registrationGroups.get(groupName, new TreeSet<String>())
            properties.each {name, propertyTuple ->
                uberBuilder.registerExplicitProperty(name, groupName, propertyTuple.get, propertyTuple.set)
            }
        }
    }

}
