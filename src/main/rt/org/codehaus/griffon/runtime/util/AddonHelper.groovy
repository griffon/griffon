/*
 * Copyright 2009-2011 the original author or authors.
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
import griffon.core.GriffonAddon
import griffon.core.GriffonAddonDescriptor
import griffon.util.UIThreadHelper
import griffon.util.GriffonNameUtils
import griffon.util.Metadata

import org.codehaus.griffon.runtime.builder.UberBuilder
import org.codehaus.griffon.runtime.core.DefaultGriffonAddon
import org.codehaus.griffon.runtime.core.DefaultGriffonAddonDescriptor

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Helper class for dealing with addon initialization.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class AddonHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AddonHelper)
    
    public static final DELEGATE_TYPES = Collections.unmodifiableList([
            "attributeDelegates",
            "preInstantiateDelegates",
            "postInstantiateDelegates",
            "postNodeCompletionDelegates"
    ])

    static void handleAddonsAtStartup(GriffonApplication app) {
        LOG.info("Loading addons [START]")
        app.event(GriffonApplication.Event.LOAD_ADDONS_START.name, [app])

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

        app.addonManager.addons.each {name, addon ->
            try {
                addon.addonPostInit(app)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonPostInit') throw mme
            }
            app.event(GriffonApplication.Event.LOAD_ADDON_END.name, [name, addon, app])
            if(LOG.infoEnabled) LOG.info("Loaded addon $name")
        }

        app.event(GriffonApplication.Event.LOAD_ADDONS_END.name, [app, app.addonManager.addons])
        LOG.info("Loading addons [END]")
    }

    static void handleAddon(GriffonApplication app, Class addonClass, String prefix, String addonName) {
        GriffonAddonDescriptor addonDescriptor = app.addonManager.findAddonDescriptor(addonName)
        if(addonDescriptor) return

        def obj = addonClass.newInstance()
        String pluginName = GriffonNameUtils.getHyphenatedName(addonName - 'GriffonAddon')
        String addonVersion = Metadata.current['plugins.' + pluginName]
        GriffonAddon addon = new DefaultGriffonAddon(app, obj)
        addonDescriptor = new DefaultGriffonAddonDescriptor(prefix, addonName, pluginName, addonVersion, addon)

        app.addonManager.registerAddon(addonDescriptor)

        def addonMetaClass = obj.metaClass
        addonMetaClass.app = app
        addonMetaClass.newInstance = GriffonApplicationHelper.&newInstance.curry(app)
        UIThreadHelper.enhance(addonMetaClass)

        if(LOG.infoEnabled) LOG.info("Loading addon $addonName with class ${addon.class.name}")
        app.event(GriffonApplication.Event.LOAD_ADDON_START.name, [addonName, addon, app])

        addon.addonInit(app)
        addMVCGroups(app, addon.mvcGroups)
        addEvents(app, addon.events)
    }

    static void handleAddonsForBuilders(GriffonApplication app, UberBuilder builder, Map<String, MetaClass> targets) {
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

        app.addonManager.addons.each {name, addon ->
            try {
                addon.addonBuilderPostInit(app, builder)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonBuilderPostInit') throw mme
            }
        }
    }

    static void handleAddonForBuilder(GriffonApplication app, UberBuilder builder, Map<String, MetaClass> targets, def addonNode, String prefix) {
        def addonName = addonNode.key
        GriffonAddon addon = app.addonManager.addons[addonName]

        addon.addonBuilderInit(app, builder)

        DELEGATE_TYPES.each { String delegateType ->
            List<Closure> delegates = addon."$delegateType"
            delegateType = delegateType[0].toUpperCase() + delegateType[1..-2]
            delegates.each { Closure delegateValue ->
                builder."add$delegateType"(delegateValue)
            }
        }

        Map factories = addon.factories
        addFactories(builder, factories, addonName, prefix)

        Map methods = addon.methods
        addMethods(builder, methods, addonName, prefix)

        Map props = addon.props
        addProperties(builder, props, addonName, prefix)

        for (partialTarget in addonNode.value) {
            if (partialTarget.key == 'view') {
                // this needs special handling, skip it for now
                continue
            }
            MetaClass mc = targets[partialTarget.key]
            if (!mc) continue
            for (String itemName in partialTarget.value) {
                if (itemName == '*') {
                    if(methods && LOG.traceEnabled) LOG.trace("Injecting all methods on $partialTarget.key")
                    addMethods(mc, methods, prefix)
                    if(factories && LOG.traceEnabled) LOG.trace("Injecting all factories on $partialTarget.key")
                    addFactories(mc, factories, prefix, builder)
                    if(props && LOG.traceEnabled) LOG.trace("Injecting all properties on $partialTarget.key")
                    addProps(mc, props, prefix)
                    continue
                } else if (itemName == '*:methods') {
                    if(methods && LOG.traceEnabled) LOG.trace("Injecting all methods on $partialTarget.key")
                    addMethods(mc, methods, prefix)
                    continue
                } else if (itemName == '*:factories') {
                    if(factories && LOG.traceEnabled) LOG.trace("Injecting all factories on $partialTarget.key")
                    addFactories(mc, factories, prefix, builder)
                    continue
                } else if (itemName == '*:props') {
                    if(props && LOG.traceEnabled) LOG.trace("Injecting all properties on $partialTarget.key")
                    addProps(mc, props, prefix)
                    continue
                }

                def resolvedName = "${prefix}${itemName}"
                if (methods.containsKey(itemName)) {
                    if(LOG.traceEnabled) LOG.trace("Injected method ${resolvedName}() on $partialTarget.key")
                    mc."$resolvedName" = methods[itemName]
                } else if (props.containsKey(itemName)) {
                    Map accessors = props[itemName]
                    String beanName
                    if (itemName.length() > 1) {
                        beanName = itemName[0].toUpperCase() + itemName.substring(1)
                    } else {
                        beanName = itemName[0].toUpperCase()
                    }
                    if (accessors.containsKey('get')) {
                        if(LOG.traceEnabled) LOG.trace("Injected getter for ${beanName} on $partialTarget.key")
                        mc."get$beanName" = accessors['get']
                    }
                    if (accessors.containsKey('set')) {
                        if(LOG.traceEnabled) LOG.trace("Injected setter for ${beanName} on $partialTarget.key")
                        mc."set$beanName" = accessors['set']
                    }
                } else if (factories.containsKey(itemName)) {
                    if(LOG.traceEnabled) LOG.trace("Injected factory ${resolvedName} on $partialTarget.key")
                    mc."${resolvedName}" = {Object ... args -> builder."$resolvedName"(* args)}
                }
            }
        }
    }

    private static void addMethods(MetaClass mc, Map methods, String prefix) {
        methods.each { mk, mv -> mc."${prefix}${mk}" = mv }
    }
 
    private static void addFactories(MetaClass mc, Map factories, String prefix, UberBuilder builder) {
        factories.each { fk, fv -> 
            def resolvedName = "${prefix}${fk}"
            mc."$resolvedName" = {Object... args -> builder."$resolvedName"(*args) }
        }
    }

    private static void addProps(MetaClass mc, Map props, String prefix) {
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

    static void addMVCGroups(GriffonApplication app, Map<String, Map<String, String>> groups) {
        groups.each {k, v -> app.addMvcGroup(k, v) }
    }

    static void addFactories(UberBuilder builder, Map factories, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, [] as TreeSet)
        factories.each {String name, factoryOrBean ->
            if(factoryOrBean instanceof Factory) {
                builder.registerFactory(name, addonName, factoryOrBean)
            } else {
                builder.registerBeanFactory(name, addonName, factoryOrBean)
            }
        }
    }

    static void addMethods(UberBuilder builder, Map<String, Closure> methods, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, [] as TreeSet)
        methods.each {String name, Closure closure ->
            builder.registerExplicitMethod(name, addonName, closure)
        }
    }

    static void addProperties(UberBuilder builder, Map<String, List<Closure>> props, String addonName, String prefix) {
        builder.registrationGroup.get(addonName, [] as TreeSet)
        props.each {String name, Map<String, Closure> closures ->
            builder.registerExplicitProperty(name, addonName, closures.get, closures.set)
        }
    }

    static void addEvents(GriffonApplication app, Map<String, Closure> events) {
        events.each {String name, Closure event ->
            app.addApplicationEventListener(name, event)
        }
    }
}
