package org.codehaus.griffon.runtime.core

import griffon.core.GriffonApplication
import griffon.test.GriffonUnitTestCase
import griffon.test.mock.MockGriffonApplication

import griffon.util.ApplicationHolder

class DefaultGriffonAddonTests extends GriffonUnitTestCase {
    private GriffonApplication app
    private final FactoryBuilderSupport builder = new FactoryBuilderSupport() {}

    void setUp() {
        app = new MockGriffonApplication()
        ApplicationHolder.application = app
        app.artifactManager = new DefaultArtifactManager(app)
    }

    void testWrapEmptyGriffonAddon() {
        def addon = new EmptyGriffonAddon()
        DefaultGriffonAddon wrapper = new DefaultGriffonAddon(app, addon)

        wrapper.addonInit(app)
        wrapper.addonBuilderInit(app, builder)
        wrapper.addonBuilderPostInit(app, builder)
        wrapper.addonPostInit(app)

        assert !wrapper.factories
        assert !wrapper.methods
        assert !wrapper.events
        assert !wrapper.props
        assert !wrapper.attributeDelegates
        assert !wrapper.preInstantiateDelegates
        assert !wrapper.postInstantiateDelegates
        assert !wrapper.postNodeCompletionDelegates
    }

    void testWrapPartialGriffonAddon() {
        def addon = new PartialGriffonAddon()
        DefaultGriffonAddon wrapper = new DefaultGriffonAddon(app, addon)

        assert !addon.invokedMethod
        wrapper.addonInit(app)
        assert 'addonInit' == addon.invokedMethod
        wrapper.addonBuilderInit(app, builder)
        assert 'addonBuilderInit' == addon.invokedMethod
        wrapper.addonBuilderPostInit(app, builder)
        assert 'addonBuilderPostInit' == addon.invokedMethod
        wrapper.addonPostInit(app)
        assert 'addonPostInit' == addon.invokedMethod

        assert !wrapper.factories
        assert !wrapper.methods
        assert !wrapper.events
        assert !wrapper.props
        assert !wrapper.attributeDelegates
        assert !wrapper.preInstantiateDelegates
        assert !wrapper.postInstantiateDelegates
        assert !wrapper.postNodeCompletionDelegates
    }

    void testWrapFullGriffonAddon() {
        def addon = new FullGriffonAddon()
        DefaultGriffonAddon wrapper = new DefaultGriffonAddon(app, addon)

        assert !addon.invokedMethod
        wrapper.addonInit(app)
        assert 'addonInit' == addon.invokedMethod
        wrapper.addonBuilderInit(app, builder)
        assert 'addonBuilderInit' == addon.invokedMethod
        wrapper.addonBuilderPostInit(app, builder)
        assert 'addonBuilderPostInit' == addon.invokedMethod
        wrapper.addonPostInit(app)
        assert 'addonPostInit' == addon.invokedMethod

        assert wrapper.factories.aNode
        assert wrapper.methods.aMethod
        assert wrapper.events.anEvent
        assert wrapper.props.aProp
        assert !wrapper.attributeDelegates
        assert !wrapper.preInstantiateDelegates
        assert !wrapper.postInstantiateDelegates
        assert !wrapper.postNodeCompletionDelegates
        
        assert wrapper.mvcGroups.aGroup
        assert wrapper.mvcGroups.aGroup.model == 'SampleModel'
        assert wrapper.mvcGroups.aGroup.view == 'SampleView'
        assert wrapper.mvcGroups.aGroup.controller == 'SampleController'
    }
}

class EmptyGriffonAddon {}

class PartialGriffonAddon {
    String invokedMethod

    void addonInit(GriffonApplication app) {
        invokedMethod = 'addonInit'
    }

    void addonPostInit(GriffonApplication app) {
        invokedMethod = 'addonPostInit'
    }

    void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {
        invokedMethod = 'addonBuilderInit'
    }

    void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {
        invokedMethod = 'addonBuilderPostInit'
    }
}

class FullGriffonAddon extends PartialGriffonAddon {
    def factories = [
        aNode: EmptyGriffonAddon
    ]

    def methods = [
        aMethod: {-> }
    ]

    def props = [
        aProp: [
            get: {-> },
            set: {v -> },
        ]
    ]

    def events = [
        anEvent: {-> }
    ]

    def mvcGroups = [
        aGroup: [
            model: 'SampleModel',
            view: 'SampleView',
            controller: 'SampleController',
        ]
    ]
}
