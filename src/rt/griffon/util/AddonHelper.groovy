package griffon.util

import griffon.builder.UberBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: dannoferrin
 * Date: Jun 19, 2009
 * Time: 8:02:46 PM
 */
public class AddonHelper {

    public static final DELEGATE_TYPES = Collections.unmodifiableList([
            "attributeDelegates",
            "preInstantiateDelegates",
            "postInstantiateDelegates",
            "postNodeCompletionDelegates"
    ])

    static handleAddonsAtStartup(IGriffonApplication app) {
        Map addonDefs = new TreeMap();
        app.applicationProperties.each {String  addonDef, String addonClass ->
            def parts = addonDef.split(/\./, 3)
            if ('addon' != parts[0]) return

            String prefix = parts.length < 3 ? '' : parts[1]
            String addonName = parts.length < 3 ? parts[1] : parts[2]
            addonDefs[addonName] = [prefix, addonClass]
        }

        addonDefs.each {String addonName, List addonDef ->
            def addon = (addonDef[1] as Class).newInstance()

            app.addons[addonName] = addon
            app.addonPrefixes[addonName] = addonDef[0]

            def addonMetaClass = addon.metaClass

            try {
                addon.addonInit(app)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonInit') throw mme
            }

            def mvcGroups = addonMetaClass.getMetaProperty("mvcGroups")
            if (mvcGroups) addMVCGroups(app, addon.mvcGroups)

            def events = addonMetaClass.respondsTo('events')
            if (events) addEvents(app, addon.events)
        }

        app.addons.each {name, addon ->
            try {
                addon.addonPostInit(app)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonPostInit') throw mme
            }
        }
    }



    static handleAddonsForBuilders(IGriffonApplication app, UberBuilder builder) {
        app.addonPrefixes.each {String addonName, String prefix ->
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

            def factories = addonMetaClass.getMetaProperty('factories')
            if (factories) addFactories(builder, addon.factories, addonName, prefix)

            def methods = addon.metaClass.getMetaProperty("methods")
            if( methods ) addMethods(builder, addon.factories, addonName, prefix)
            def properties = addon.metaClass.getMetaProperty("props")
            if( properties ) addProperties(builder, addon.factories, addonName, prefix)

            def addonBuilderPostInit = addonMetaClass.respondsTo('addonBuilderPostInit')
            if (addonBuilderPostInit) postInits << addon
        }

        app.addons.each {name, addon ->
            try {
                addon.addonBuilderPostInit(app, builder)
            } catch (MissingMethodException mme) {
                if (mme.method != 'addonBuilderPostInit') throw mme
            }
        }
    }

    private static ignoreMissingPropertyException(Closure closure) {
        try {
            closure()
        } catch (MissingPropertyException ignored) {
            // ignore
        }
    }

    static addMVCGroups(IGriffonApplication app, Map<String, Map<String, String>> groups) {
        groups.each {k, v -> app.addMvcGroup(k, v) }
    }


    static addFactories(UberBuilder builder, Map<String, Factory> factories, String addonName, String prefix) {
        factories.each {String name, Factory factory ->
            builder.registerFactory(name, addonName, factory)
        }
    }

    static addMethods(UberBuilder builder, Map<String, Closure> methods, String addonName, String prefix) {
        methods.each {String name, Closure closure ->
            builder.registerExplicitMethod(name, addonName, closure)
        }
    }

    static addProperties(UberBuilder builder, Map<String, List<Closure>> methods, String addonName, String prefix) {
        methods.each {String name, Map<String, Closure> closures ->
            builder.registerExplicitProperty(name, addonName, closures.get, closures.set)
        }
    }

    static addEvents(IGriffonApplication app, Map<String, Closure> events) {
        events.each {String name, Closure event ->
            app.addApplicationEventListener(name, event)
        }
    }
}
