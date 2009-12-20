/*
 * Copyright 2009 the original author or authors.
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

/**
 * Helper class that can execute code inside the UI thread.
 *
 * @author Andres Almiray
 */
@Singleton
class UIThreadHelper {
    // Shouldn't need to synchronize access to this field as setting its value
    // should be done at boot time
    private UIThreadHandler uiThreadHandler

    public void setUIThreadHandler(UIThreadHandler threadHandler) {
        if(!this.uiThreadHandler) {
            this.uiThreadHandler = threadHandler
        } else {
            // TODO emmit a warning: UIThread is already set, you can't change it
            // TODO use app logger
        }
    }

    public UIThreadHandler getUIThreadHandler() {
        if(!this.uiThreadHandler) {
            try {
                // attempt loading of default UIThreadHandler -> Swing
                setUIThreadHandler(getClass().classLoader.loadClass("griffon.util.SwingUIThreadHandler").newInstance())
            } catch(ClassNotFoundException e) {
                throw new IllegalStateException("Can't locate a suitable UIThreadHandler. Did you forget to register one?")
            }
        }
        this.uiThreadHandler
    }

    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread() {
        getUIThreadHandler().isUIThread()
    }

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void executeAsync(Runnable runnable) {
        getUIThreadHandler().executeAsync(runnable)
    }

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void executeAsync(Script script) {
        getUIThreadHandler().executeAsync(script.&run)
    }

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void executeSync(Runnable runnable) {
        getUIThreadHandler().executeSync(runnable)
    }

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void executeSync(Script script) {
        getUIThreadHandler().executeSync(script.&run)
    }

    /**
     * Executes a code block outside of the UI thread.
     */
    void executeOutside(Runnable runnable) {
        getUIThreadHandler().executeOutside(runnable)
    }

    /**
     * Executes a code block outside of the UI thread.
     */
    void executeOutside(Script script) {
        getUIThreadHandler().executeOutside(script.&run)
    }
}
