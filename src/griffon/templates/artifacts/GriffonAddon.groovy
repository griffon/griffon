@artifact.package@class @artifact.name@ {


    // lifecycle methods

    // called once, after the addon is created
    //def addonInit(app) {
    //}

    // called once, after all addons have been inited
    //def addonPostInit(app) {
    //}

    // called many times, after creating a builder
    //def addonInit(app) {
    //}

    // called many times, after creating a builder and after
    // all addons have been inited
    //def addonPostInit(app) {
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
    //        get: { /* optional getter closuer},
    //        set: {val-> /* optional setter closuer},
    //  ]
    //]

    // adds new factories to all builders
    //def factories = [
    //    factory : /*instance that extends Factory*/
    //]


    //TODO enumerate FactoryBuilderSupporte delegate closures

}