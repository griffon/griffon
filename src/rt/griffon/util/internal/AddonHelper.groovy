/*
 * Copyright 2009-2010 the original author or authors.
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

package griffon.util.internal

import griffon.builder.UberBuilder
import griffon.core.GriffonApplication
import griffon.util.UIThreadHelper

/**
 * Helper class for dealing with addon initialization.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class AddonHelper {
    public static final DELEGATE_TYPES = Collections.unmodifiableList([
            "attributeDelegates",
            "preInstantiateDelegates",
            "postInstantiateDelegates",
            "postNodeCompletionDelegates"
    ])

    static handleAddonsAtStartup(GriffonApplication app) {
        app.event("LoadAddonsStart", [app])

        for (node in app.builderConfig) {
            String nodeName = node.key
            switch (nodeName) {
                case "addons" :
                case "features":
                    // reserved words, not addon prefixes
                    break
                default:
                    if (nodeName == "root") nodeName = ""
                    node.value.each {addon ->
                        Class addonClass = Class.forName(addon.key) //FIXME get correct classloader
                        if (!FactoryBuilderSupport.isAssignableFrom(addonClass)) {
                            AddonHelper.handleAddon(app, addonClass, nodeName, addon.key)
                        }
                    }
            }
        }

        app.addons.each {name, addon ->
            try {
                addon.addonPostInit(app)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonPostInit') throw mme
            }
            app.event("LoadAddonEnd", [name, addon, app])
        }

        app.event("LoadAddonsEnd", [app, app.addons])
    }

    static def handleAddon(GriffonApplication app, Class addonClass, String prefix, String addonName) {
        def addon = addonClass.newInstance()

        app.addons[addonName] = addon
        app.addonPrefixes[addonName] = prefix

        def addonMetaClass = addon.metaClass
        addonMetaClass.app = app
        addonMetaClass.newInstance = GriffonApplicationHelper.&newInstance.curry(app)
        UIThreadHelper.enhance(addonMetaClass)

        app.event("LoadAddonStart", [addonName, addon, app])

        try {
            addon.addonInit(app)
        } catch (MissingMethodException mme) {
            if (mme.method != 'addonInit') throw mme
        }

        def mvcGroups = addonMetaClass.getMetaProperty("mvcGroups")
        if (mvcGroups) addMVCGroups(app, addon.mvcGroups)

        def events = addonMetaClass.getMetaProperty('events')
        if (events) addEvents(app, addon.events)
    }

    static handleAddonsForBuilders(GriffonApplication app, UberBuilder builder, Map targets) {
        for (node in app.builderConfig) {
            String nodeName = node.key
            switch (nodeName) {
                case "addons" :
                case "features":
                    // reserved words, not addon prefixes
                    break
                default:
                    if (nodeName == "root") nodeName = ""
                    node.value.each {addon ->
                        Class addonClass = Class.forName(addon.key) //FIXME get correct classloader
                        if (!FactoryBuilderSupport.isAssignableFrom(addonClass)) {
                            AddonHelper.handleAddonForBuilder(app, builder, targets, addon, nodeName)
                        }
                    }
            }
        }

        app.addons.each {name, addon ->
            try {
                addon.addonBuilderPostInit(app, builder)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonBuilderPostInit') throw mme
            }
        }
    }

    static handleAddonForBuilder(GriffonApplication app, UberBuilder builder, Map targets, def addonNode, String prefix) {
        def addonName = addonNode.key
        def addon = app.addons[addonName]
        def addonMetaClass = addon.metaClass

        try {
            addon.addonBuilderInit(app, builder)
        } catch (MissingMethodException mme) {
            if (mme.method != 'addonBuilderInit') throw mme
        }

        DELEGATE_TYPES.each { String delegateType ->
            ignoreMissingPropertyException {
                List<Closure> delegates = addon."$delegateType"
                delegateType = delegateType[0].toUpperCase() + delegateType[1..-2]
                delegates.each { Closure delegateValue ->
                    builder."add$delegateType"(delegateValue)
                }
            }
        }

        MetaProperty factoriesMP = addonMetaClass.getMetaProperty('factories')
        Map factories = [:]
        if (factoriesMP) {
            factories = factoriesMP.getProperty(addon)
            addFactories(builder, factories, addonName, prefix)
        }

        MetaProperty methodsMP = addonMetaClass.getMetaProperty('methods')
        Map methods = [:]
        if (methodsMP) {
            methods = methodsMP.getProperty(addon)
            addMethods(builder, methods, addonName, prefix)
        }

        MetaProperty propsMP = addonMetaClass.getMetaProperty('props')
        Map props = [:]
        if (propsMP) {
            props = propsMP.getProperty(addon)
            addProperties(builder, props, addonName, prefix)
        }

        for (partialTarget in addonNode.value) {
            if (partialTarget.key == 'view') {
                // this needs special handling, skip it for now
                continue
            }
            MetaClass mc = targets[partialTarget.key]?.getMetaClass()
            if (!mc) continue
            for (String itemName in partialTarget.value) {
                if (itemName == '*') {
                    addMethods(mc, methods, prefix)
                    addFactories(mc, factories, prefix, builder)
                    addProps(mc, props, prefix)
                    continue
                } else if (itemName == '*:methods') {
                    addMethods(mc, methods, prefix)
                    continue
                } else if (itemName == '*:factories') {
                    addFactories(mc, factories, prefix, builder)
                    continue
                } else if (itemName == '*:props') {
                    addProps(mc, props, prefix)
                    continue
                }

                def resolvedName = "${prefix}${itemName}"
                if (methods.containsKey(itemName)) {
                    mc."$resolvedName" = methods[itemName]
                } else if (props.containsKey(itemName)) {
                    Map accessors = props[itemName];
                    String beanName
                    if (itemName.length() > 1) {
                        beanName = itemName[0].toUpperCase() + itemName.substring(1)
                    } else {
                        beanName = itemName[0].toUpperCase()
                    }
                    if (accessors.containsKey('get')) {
                        mc."get$beanName" = accessors['get']
                    }
                    if (accessors.containsKey('set')) {
                        mc."set$beanName" = accessors['set']
                    }
                } else if (factories.containsKey(itemName)) {
                    mc."${resolvedName}" = {Object ... args -> builder."$resolvedName"(* args)}
                }
            }
        }
    }

    private static addMethods(MetaClass mc, Map methods, String prefix) {
        methods.each { mk, mv -> mc."${prefix}${mk}" = mv }
    }
 
    private static addFactories(MetaClass mc, Map factories, String prefix, UberBuilder builder) {
        factories.each { fk, fv -> 
            def resolvedName = "${prefix}${fk}"
            mc."$resolvedName" = {Object... args -> builder."$resolvedName"(*args) }
        }
    }

    private static addProps(MetaClass mc, Map props, String prefix) {
        props.each = { pk, accessors ->
            String beanName
            if (pk.length() > 1) {
                beanName = pk[0].toUpperCase() + pk.substring(1)
            } else {
                beanName = pk[0].toUpperCase()
            }
            if (accessors.containsKey('get')) mc."get$beanName" = accessors['get']
            if (accessors.containsKey('set')) mc."set$beanName" = accessors['set']
        }
    }

    private static ignoreMissingPropertyException(Closure closure) {
        try {
            closure()
        } catch (MissingPropertyException ignored) {
            // ignore
        }
    }

    static addMVCGroups(GriffonApplication app, Map<String, Map<String, String>> groups) {
        groups.each {k, v -> app.addMvcGroup(k, v) }
    }

    static addFactories(UberBuilder builder, Map factories, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, new TreeSet<String>())
        factories.each {String name, factoryOrBean ->
            if(factoryOrBean instanceof Factory) {
                builder.registerFactory(name, addonName, factoryOrBean)
            } else {
                builder.registerBeanFactory(name, addonName, factoryOrBean)
            }
        }
    }

    static addMethods(UberBuilder builder, Map<String, Closure> methods, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, new TreeSet<String>())
        methods.each {String name, Closure closure ->
            builder.registerExplicitMethod(name, addonName, closure)
        }
    }

    static addProperties(UberBuilder builder, Map<String, List<Closure>> methods, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, new TreeSet<String>())
        methods.each {String name, Map<String, Closure> closures ->
            builder.registerExplicitProperty(name, addonName, closures.get, closures.set)
        }
    }

    static addEvents(GriffonApplication app, Map<String, Closure> events) {
        events.each {String name, Closure event ->
            app.addApplicationEventListener(name, event)
        }
    }
}
