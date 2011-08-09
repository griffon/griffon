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
package griffon.core;

import griffon.util.UIThreadHandler;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.MethodClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Helper class that can execute code inside the UI thread.
 *
 * @author Andres Almiray
 */
public final class UIThreadManager {
    // Shouldn't need to synchronize access to this field as setting its value
    // should be done at boot time
    private UIThreadHandler uiThreadHandler;
    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Logger LOG = LoggerFactory.getLogger(UIThreadManager.class);

    private static final UIThreadManager INSTANCE = new UIThreadManager();

    public static UIThreadManager getInstance() {
        return INSTANCE;
    }

    private UIThreadManager() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Default Executor set to run with " + Runtime.getRuntime().availableProcessors() + " processors");
        }
    }

    public static void enhance(Script script) {
        if(script instanceof ThreadingHandler) return;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Enhancing script " + script);
        }
        script.getBinding().setVariable("execSync", new MethodClosure(INSTANCE, "executeSync"));
        script.getBinding().setVariable("execAsync", new MethodClosure(INSTANCE, "executeAsync"));
        script.getBinding().setVariable("execOutside", new MethodClosure(INSTANCE, "executeOutside"));
        script.getBinding().setVariable("isUIThread", new MethodClosure(INSTANCE, "isUIThread"));
        script.getBinding().setVariable("execFuture", new MethodClosure(INSTANCE, "executeFuture"));
    }

    public static void enhance(MetaClass metaClass) {
        if (metaClass instanceof ExpandoMetaClass) {
            ExpandoMetaClass mc = (ExpandoMetaClass) metaClass;
            if (LOG.isTraceEnabled()) {
                LOG.trace("Enhancing metaClass " + metaClass);
            }
            mc.registerInstanceMethod("execSync", new MethodClosure(INSTANCE, "executeSync"));
            mc.registerInstanceMethod("execAsync", new MethodClosure(INSTANCE, "executeAsync"));
            mc.registerInstanceMethod("execOutside", new MethodClosure(INSTANCE, "executeOutside"));
            mc.registerInstanceMethod("isUIThread", new MethodClosure(INSTANCE, "isUIThread"));
            mc.registerInstanceMethod("execFuture", new MethodClosure(INSTANCE, "executeFuture"));
        }
    }

    public void setUIThreadHandler(UIThreadHandler threadHandler) {
        if (this.uiThreadHandler != null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("UIThreadHandler is already set, it can't be changed!");
            }
        } else {
            this.uiThreadHandler = threadHandler;
        }
    }

    public UIThreadHandler getUIThreadHandler() {
        if (this.uiThreadHandler == null) {
            try {
                // attempt loading of default UIThreadHandler -> Swing
                setUIThreadHandler((UIThreadHandler) getClass().getClassLoader().loadClass("griffon.swing.SwingUIThreadHandler").newInstance());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Can't locate a suitable UIThreadHandler.", e);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Can't locate a suitable UIThreadHandler.", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Can't locate a suitable UIThreadHandler.", e);
            }
        }
        return this.uiThreadHandler;
    }

    /**
     * True if the current thread is the UI thread.
     *
     * @return true if the current thread is the UI thread, false otherwise.
     */
    public boolean isUIThread() {
        return getUIThreadHandler().isUIThread();
    }

    /**
     * Executes a code block asynchronously on the UI thread.
     *
     * @param runnable a code block to be executed
     */
    public void executeAsync(Runnable runnable) {
        getUIThreadHandler().executeAsync(runnable);
    }

    /**
     * Executes a code block asynchronously on the UI thread.
     *
     * @param script a code block to be executed
     */
    public void executeAsync(Script script) {
        getUIThreadHandler().executeAsync(new MethodClosure(script, "run"));
    }

    /**
     * Executes a code block synchronously on the UI thread.
     *
     * @param runnable a code block to be executed
     */
    public void executeSync(Runnable runnable) {
        getUIThreadHandler().executeSync(runnable);
    }

    /**
     * Executes a code block synchronously on the UI thread.
     *
     * @param script a code block to be executed
     */
    public void executeSync(Script script) {
        getUIThreadHandler().executeSync(new MethodClosure(script, "run"));
    }

    /**
     * Executes a code block outside of the UI thread.
     *
     * @param runnable a code block to be executed
     */
    public void executeOutside(Runnable runnable) {
        getUIThreadHandler().executeOutside(runnable);
    }

    /**
     * Executes a code block outside of the UI thread.
     *
     * @param script a code block to be executed
     */
    public void executeOutside(Script script) {
        getUIThreadHandler().executeOutside(new MethodClosure(script, "run"));
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param callable a code block to be executed
     * @return a Future that contains the result of the execution
     */
    public Future executeFuture(Callable<?> callable) {
        return executeFuture(DEFAULT_EXECUTOR_SERVICE, callable);
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param executorService the ExecutorService to use. Will use the default ExecutorService if null.
     * @param callable        a code block to be executed
     * @return a Future that contains the result of the execution
     */
    public Future executeFuture(ExecutorService executorService, Callable<?> callable) {
        executorService = executorService != null ? executorService : DEFAULT_EXECUTOR_SERVICE;
        return executorService.submit(callable);
    }
}
