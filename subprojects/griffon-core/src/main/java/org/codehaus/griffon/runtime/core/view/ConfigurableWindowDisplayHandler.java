/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.core.view;

import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.core.RunnableWithArgs;
import griffon.core.view.WindowDisplayHandler;
import griffon.exceptions.InstanceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of a per window {@code WindowDisplayHandler} that can be configured via a DSL.<p>
 * This is the default {@code WindowDisplayHandler} used by {@code SwingApplication}. It expects a configuration
 * entry in <code>griffon-app/conf/Config.groovy</code> that looks like the following one<p>
 * <pre>
 *     windowManager {
 *         myWindowName = [
 *             show: {name, window -> ... },
 *             hide: {name, window -> ... }
 *         ]
 *         myOtherWindowName = [
 *             show: {name, window -> ... }
 *         ]
 *     }
 * </pre>
 * <p>
 * For these settings to work you must specify a <code>name:</code> property on the Window/Frame instance. This
 * {@code WindowDisplayHandler} is smart enough to use the default show/hide behavior should any or both are not specified
 * or if a window name does not have a matching configuration. The default behavior will also be used if the Window/Frame
 * does not have a value for its <code>name:</code> property.<p>
 * There's a third option that can be set for each configured window, and that is a delegate {@code WindowDisplayHandler} that
 * will be used for that window alone. The following example shows how it can be configured<p>
 * <pre>
 *     windowManager {
 *         myWindowName = [
 *             handler: new MyCustomWindowDisplayHandler()
 *         ]
 *         myOtherWindowName = [
 *             show: {name, window -> ... }
 *         ]
 *     }
 * </pre>
 * <p>
 * Lastly, a global handler can be specified for all windows that have not been configured. If specified, this handler will
 * override the usage of the default one. It can be configured as follows<p>
 * <pre>
 *     windowManager {
 *         defaultHandler = new MyCustomWindowDisplayHandler()
 *         myOtherWindowName = [
 *             show: {name, window -> ... }
 *         ]
 *     }
 * </pre>
 * <p>
 * Fine grained control for default <code>show</code> and <code>hide</code> is also possible, by specifying <code>defaultShow</code>
 * and/or <code>defaultHide</code> properties at the global level. These properties take precedence over <code>defaultHandler</code> .
 * <p>
 * <pre>
 *     windowManager {
 *         defaultHide = {name, window -> ... }
 *         myOtherWindowName = [
 *             show: {name, window -> ... }
 *         ]
 *     }
 * </pre>
 * <p>
 * <strong>Note:</strong> the value for <code>show</code> and <code>hide</code> can be either a Closure or a {@code RunnableWithArgs}.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableWindowDisplayHandler<W> implements WindowDisplayHandler<W> {
    protected static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    protected static final String ERROR_WINDOW_NULL = "Argument 'window' must not be null";
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableWindowDisplayHandler.class);
    private static final String HANDLER = "handler";
    private final GriffonApplication application;
    private final WindowDisplayHandler<W> delegateWindowsDisplayHandler;

    @Inject
    public ConfigurableWindowDisplayHandler(@Nonnull GriffonApplication application, @Nonnull @Named("defaultWindowDisplayHandler") WindowDisplayHandler<W> delegateWindowsDisplayHandler) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
        this.delegateWindowsDisplayHandler = requireNonNull(delegateWindowsDisplayHandler, "Argument 'delegateWindowsDisplayHandler' must not be null");
    }

    @SuppressWarnings("unchecked")
    public void show(@Nonnull String name, @Nonnull W window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("show");
            if (canBeRun(handler)) {
                LOG.trace("Showing {} with show: handler", name);
                run(handler, name, window);
                return;
            } else if (options.get(HANDLER) instanceof WindowDisplayHandler) {
                LOG.trace("Showing {} with handler: handler", name);
                ((WindowDisplayHandler<W>) options.get(HANDLER)).show(name, window);
                return;
            }
        }

        if (handleShowByInjectedHandler(name, window)) {
            return;
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object defaultShow = options.get("defaultShow");
            if (canBeRun(defaultShow)) {
                LOG.trace("Showing {} with defaultShow: handler", name);
                run(defaultShow, name, window);
                return;
            }
        }

        LOG.trace("Showing {} with default handler", name);
        fetchDefaultWindowDisplayHandler().show(name, window);
    }

    @SuppressWarnings("unchecked")
    public void hide(@Nonnull String name, @Nonnull W window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("hide");
            if (canBeRun(handler)) {
                LOG.trace("Hiding {} with hide: handler", name);
                run(handler, name, window);
                return;
            } else if (options.get(HANDLER) instanceof WindowDisplayHandler) {
                LOG.trace("Hiding {} with handler: handler", name);
                ((WindowDisplayHandler<W>) options.get(HANDLER)).hide(name, window);
                return;
            }
        }

        if (handleHideByInjectedHandler(name, window)) {
            return;
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object defaultHide = options.get("defaultHide");
            if (canBeRun(defaultHide)) {
                LOG.trace("Hiding {} with defaultHide: handler", name);
                run(defaultHide, name, window);
                return;
            }
        }

        LOG.trace("Hiding {} with default handler", name);
        fetchDefaultWindowDisplayHandler().hide(name, window);
    }

    @SuppressWarnings("unchecked")
    protected boolean handleShowByInjectedHandler(@Nonnull String name, @Nonnull W window) {
        try {
            WindowDisplayHandler<W> handler = getApplication().getInjector()
                .getInstance(WindowDisplayHandler.class, named(name));
            LOG.trace("Showing {} with injected handler", name);
            handler.show(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            // ignore
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean handleHideByInjectedHandler(@Nonnull String name, @Nonnull W window) {
        try {
            WindowDisplayHandler<W> handler = getApplication().getInjector()
                .getInstance(WindowDisplayHandler.class, named(name));
            LOG.trace("Hiding {} with injected handler", name);
            handler.hide(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            // ignore
        }
        return false;
    }

    public WindowDisplayHandler<W> getDelegateWindowsDisplayHandler() {
        return delegateWindowsDisplayHandler;
    }

    protected boolean canBeRun(@Nullable Object obj) {
        return obj instanceof RunnableWithArgs || obj instanceof CallableWithArgs;
    }

    protected void run(@Nonnull Object handler, @Nonnull String name, @Nonnull W window) {
        if (handler instanceof RunnableWithArgs) {
            ((RunnableWithArgs) handler).run(name, window);
        } else if (handler instanceof CallableWithArgs) {
            ((CallableWithArgs<?>) handler).call(name, window);
        }
    }

    protected Map<String, Object> windowManagerBlock() {
        return application.getConfiguration().get("windowManager", Collections.<String, Object>emptyMap());
    }

    protected Map<String, Object> windowBlock(String windowName) {
        Map<String, Object> options = windowManagerBlock();
        return getConfigValue(options, windowName, Collections.<String, Object>emptyMap());
    }

    protected GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected WindowDisplayHandler<W> fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return handler instanceof WindowDisplayHandler ? (WindowDisplayHandler<W>) handler : delegateWindowsDisplayHandler;
    }
}