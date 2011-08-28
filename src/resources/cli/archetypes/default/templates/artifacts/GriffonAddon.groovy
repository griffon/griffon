@artifact.package@import griffon.core.GriffonApplication

class @artifact.name@ {
    // lifecycle methods

    // called once, after the addon is created
    //void addonInit(GriffonApplication app) {
    //}

    // called once, after all addons have been inited
    //void addonPostInit(GriffonApplication app) {
    //}

    // called many times, after creating a builder
    //void addonBuilderInit(GriffonApplication app, FactoryBuilderSupport builder) {
    //}

    // called many times, after creating a builder and after
    // all addons have been inited
    //void addonBuilderPostInit(GriffonApplication app, FactoryBuilderSupport builder) {
    //}


    // to add MVC Groups use create-mvc


    // builder fields, these are added to all builders.
    // closures can either be literal { it -> println it}
    // or they can be method closures: this.&method

    // adds methods to all builders
    //def methods = [
    //    methodName: { /*Closure*/ }
    //]

    // adds properties to all builders
    //def props = [
    //    propertyName: [
    //        get: { /* optional getter closure */ },
    //        set: {val-> /* optional setter closure */ },
    //  ]
    //]

    // adds new factories to all builders
    //def factories = [
    //    factory : /*instance that extends Factory*/
    //]

    // adds application event handlers
    //def events = [
    //    "StartupStart": {app -> /* event hadler code */ }
    //]

    // handle synthetic node properties or
    // intercept existing ones
    //def attributeDelegates = [
    //    {builder, node, attributes -> /*handler code*/ }
    //]

    // called before a node is instantiated
    //def preInstantiateDelegates = [
    //    {builder, attributes, value -> /*handler code*/ }
    //]

    // called after the node was instantiated
    //def postInstantiateDelegates = [
    //    {builder, attributes, node -> /*handler code*/ }
    //]

    // called after the node has been fully
    // processed, including child content
    //def postNodeCompletionDelegates = [
    //    {builder, parent, node -> /*handler code*/ }
    //]
}
