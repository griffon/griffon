/*
 * Copyright 2008-2012 the original author or authors.
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
package griffon.test

import griffon.core.UIThreadManager

/**
 * Support class for writing unit tests in Griffon. It mainly provides
 * access to various mocking options, while making sure that the meta-
 * class magic does not leak outside of a single test.
 * It also provides access to the threading facilities exposed by
 * {@code UIThreadManager}.
 */
class GriffonUnitTestCase extends GroovyTestCase {
    Map savedMetaClasses

    protected void setUp() {
        super.setUp()
        savedMetaClasses = [:]
    }

    protected void tearDown() {
        super.tearDown()

        // Restore all the saved meta classes.
        savedMetaClasses.each { clazz, metaClass ->
            GroovySystem.metaClassRegistry.removeMetaClass(clazz)
            GroovySystem.metaClassRegistry.setMetaClass(clazz, metaClass)
        }
    }

    /**
     * Use this method when you plan to perform some meta-programming
     * on a class. It ensures that any modifications you make will be
     * cleared at the end of the test.
     * @param clazz The class to register.
     */
    protected void registerMetaClass(Class clazz) {
        // If the class has already been registered, then there's nothing to do.
        if (savedMetaClasses.containsKey(clazz)) return

        // Save the class's current meta class.
        savedMetaClasses[clazz] = clazz.metaClass

        // Create a new EMC for the class and attach it.
        def emc = new ExpandoMetaClass(clazz, true, true)
        emc.initialize()
        GroovySystem.metaClassRegistry.setMetaClass(clazz, emc)
    }

    /**
     * Creates a new Griffon mock for the given class. Use it as you
     * would use MockFor and StubFor.
     * @param clazz The class to mock.
     * @param loose If <code>true</code>, the method returns a loose-
     * expectation mock, otherwise it returns a strict one. The default
     * is a strict mock.
     */
    protected GriffonMock mockFor(Class clazz, boolean loose = false) {
        registerMetaClass(clazz)
        return new GriffonMock(clazz, loose)
    }

    /** Executes code synchronously inside the UI thread */
    def execSync = UIThreadManager.instance.&executeSync
    /** Executes code asynchronously inside the UI thread */
    def execAsync = UIThreadManager.instance.&executeAsync
    /** Executes code outside the UI thread */
    def execOutside = UIThreadManager.instance.&executeOutside
    /** True if the current thread is the UI thread */
    def isUIThread = UIThreadManager.instance.&isUIThread
    /** Schedules a block of code as a Future */
    def execFuture = { Object... args ->
        UIThreadManager.instance.executeFuture(* args)
    }
}
