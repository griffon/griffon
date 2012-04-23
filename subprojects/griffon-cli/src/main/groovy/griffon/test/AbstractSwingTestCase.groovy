/*
 * Copyright 2007-2012 the original author or authors.
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

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * Base classe for Swing relatedt test.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class AbstractSwingTestCase extends GroovyTestCase {
    private static boolean headless

    /**
     * A boolean indicating if we are running in headless mode.
     * Check this flag if you believe your test may make use of AWT/Swing
     * features, then simply return rather than running your test.
     *
     * @return true if running in headless mode
     */
    public static boolean isHeadless() {
        return headless
    }

    /**
     * Alias for isHeadless().
     *
     * @return true if running in headless mode
     */
    public static boolean getHeadless() {
        return isHeadless()
    }

    static {
        try {
            final Class jframe = Class.forName("javax.swing.JFrame")
            final Constructor constructor = jframe.getConstructor((Class[])[String])
            constructor.newInstance((String[])["testing"])
            headless = false
        } catch (java.awt.HeadlessException e) {
            headless = true
        } catch (UnsatisfiedLinkError e) {
            headless = true
        } catch (ClassNotFoundException e) {
            headless = true
        } catch (NoClassDefFoundError e) {
            headless = true
        } catch (IllegalAccessException e) {
            headless = true
        } catch (InstantiationException e) {
            headless = true
        } catch (NoSuchMethodException e) {
            headless = true
        } catch (InvocationTargetException e) {
            headless = true
        }
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
        UIThreadManager.instance.executeFuture(*args)
    }
}
