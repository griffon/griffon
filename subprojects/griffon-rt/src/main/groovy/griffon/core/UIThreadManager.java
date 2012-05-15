/*
 * Copyright 2009-2012 the original author or authors.
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

import griffon.util.*;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
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

    private static abstract class ScriptOrRunnableRunner extends RunnableWithArgs {
        public void run(Object[] args) {
            if (args != null && args.length == 1) {
                if (args[0] instanceof Script) {
                    withScript((Script) args[0]);
                    return;
                } else if (args[0] instanceof Runnable) {
                    withRunnable((Runnable) args[0]);
                    return;
                }
            }
            throw new MissingMethodException(getMethodName(), UIThreadManager.class, args);
        }

        protected abstract String getMethodName();

        protected abstract void withScript(Script script);

        protected abstract void withRunnable(Runnable runnable);
    }

    private static final String EXECUTE_INSIDE_UI_SYNC = "execInsideUISync";
    private static final RunnableWithArgsClosure EXECUTE_INSIDE_UI_SYNC_RUNNER = new RunnableWithArgsClosure(INSTANCE,
            new ScriptOrRunnableRunner() {
                protected String getMethodName() {
                    return EXECUTE_INSIDE_UI_SYNC;
                }

                protected void withScript(Script script) {
                    INSTANCE.executeSync(script);
                }

                protected void withRunnable(Runnable runnable) {
                    INSTANCE.executeSync(runnable);
                }
            });

    private static final String EXECUTE_INSIDE_UI_ASYNC = "execInsideUIAsync";
    private static final RunnableWithArgsClosure EXECUTE_INSIDE_UI_ASYNC_RUNNER = new RunnableWithArgsClosure(INSTANCE,
            new ScriptOrRunnableRunner() {
                protected String getMethodName() {
                    return EXECUTE_INSIDE_UI_ASYNC;
                }

                protected void withScript(Script script) {
                    INSTANCE.executeAsync(script);
                }

                protected void withRunnable(Runnable runnable) {
                    INSTANCE.executeAsync(runnable);
                }
            });

    private static final String EXECUTE_OUTSIDE_UI = "execOutsideUI";
    private static final RunnableWithArgsClosure EXECUTE_OUTSIDE_UI_RUNNER = new RunnableWithArgsClosure(INSTANCE,
            new ScriptOrRunnableRunner() {
                protected String getMethodName() {
                    return EXECUTE_OUTSIDE_UI;
                }

                protected void withScript(Script script) {
                    INSTANCE.executeOutside(script);
                }

                protected void withRunnable(Runnable runnable) {
                    INSTANCE.executeOutside(runnable);
                }
            });

    private static final String IS_UITHREAD = "isUIThread";
    private static final CallableWithArgsClosure IS_UITHREAD_RUNNER = new CallableWithArgsClosure(INSTANCE,
            new CallableWithArgs<Boolean>() {
                public Boolean call(Object[] args) {
                    if (args.length == 0) {
                        return INSTANCE.isUIThread();
                    }
                    throw new MissingMethodException(IS_UITHREAD, UIThreadManager.class, args);
                }
            });

    private static final String EXECUTE_FUTURE = "execFuture";
    private static final CallableWithArgsClosure EXECUTE_FUTURE_RUNNER = new CallableWithArgsClosure(INSTANCE,
            new CallableWithArgs<Future>() {
                public Future call(Object[] args) {
                    if (args.length == 1 && args[0] instanceof Callable) {
                        return INSTANCE.executeFuture((Callable) args[0]);
                    } else if (args.length == 2 && args[0] instanceof ExecutorService && args[1] instanceof Callable) {
                        return INSTANCE.executeFuture((ExecutorService) args[0], (Callable) args[1]);
                    }
                    throw new MissingMethodException(EXECUTE_FUTURE, UIThreadManager.class, args);
                }
            });

    public static final String[] THREADING_METHOD_NAMES = new String[] {
            EXECUTE_INSIDE_UI_SYNC,
            EXECUTE_INSIDE_UI_SYNC,
            EXECUTE_OUTSIDE_UI,
            IS_UITHREAD,
            EXECUTE_FUTURE
    };

    public static void enhance(Script script) {
        if (script instanceof ThreadingHandler) return;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Enhancing script " + script);
        }

        script.getBinding().setVariable(EXECUTE_INSIDE_UI_SYNC, EXECUTE_INSIDE_UI_SYNC_RUNNER);
        script.getBinding().setVariable(EXECUTE_INSIDE_UI_SYNC, EXECUTE_INSIDE_UI_ASYNC_RUNNER);
        script.getBinding().setVariable(EXECUTE_OUTSIDE_UI, EXECUTE_OUTSIDE_UI_RUNNER);
        script.getBinding().setVariable(IS_UITHREAD, IS_UITHREAD_RUNNER);
        script.getBinding().setVariable(EXECUTE_FUTURE, EXECUTE_FUTURE_RUNNER);
    }

    public static void enhance(MetaClass metaClass) {
        if (metaClass instanceof ExpandoMetaClass) {
            ExpandoMetaClass mc = (ExpandoMetaClass) metaClass;
            if (LOG.isTraceEnabled()) {
                LOG.trace("Enhancing metaClass " + metaClass);
            }

            mc.registerInstanceMethod(EXECUTE_INSIDE_UI_SYNC, EXECUTE_INSIDE_UI_SYNC_RUNNER);
            mc.registerInstanceMethod(EXECUTE_INSIDE_UI_ASYNC, EXECUTE_INSIDE_UI_ASYNC_RUNNER);
            mc.registerInstanceMethod(EXECUTE_OUTSIDE_UI, EXECUTE_OUTSIDE_UI_RUNNER);
            mc.registerInstanceMethod(IS_UITHREAD, IS_UITHREAD_RUNNER);
            mc.registerInstanceMethod(EXECUTE_FUTURE, EXECUTE_FUTURE_RUNNER);
        }
    }

    public void setUIThreadHandler(UIThreadHandler threadHandler) {
        this.uiThreadHandler = threadHandler;
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
    public void executeAsync(final Script script) {
        getUIThreadHandler().executeAsync(new Runnable() {
            public void run() {
                script.run();
            }
        });
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
    public void executeSync(final Script script) {
        getUIThreadHandler().executeSync(new Runnable() {
            public void run() {
                script.run();
            }
        });
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
    public void executeOutside(final Script script) {
        getUIThreadHandler().executeOutside(new Runnable() {
            public void run() {
                script.run();
            }
        });
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
