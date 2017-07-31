/*
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
package org.codehaus.griffon.runtime.swing;

import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.core.RunnableWithArgs;
import griffon.core.view.WindowDisplayHandler;
import griffon.exceptions.InstanceNotFoundException;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JInternalFrame;
import java.awt.Window;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableSwingWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements SwingWindowDisplayHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableSwingWindowDisplayHandler.class);
    private static final String HANDLER = "handler";

    @Inject
    public ConfigurableSwingWindowDisplayHandler(@Nonnull GriffonApplication application, @Nonnull @Named("defaultWindowDisplayHandler") SwingWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(application, delegateWindowsDisplayHandler);
    }

    public void show(@Nonnull String name, @Nonnull JInternalFrame window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("show");
            if (canBeRun(handler)) {
                LOG.trace("Showing {} with show: handler", name);
                run(handler, name, window);
                return;
            } else if (options.get(HANDLER) instanceof SwingWindowDisplayHandler) {
                LOG.trace("Showing {} with handler: handler", name);
                ((SwingWindowDisplayHandler) options.get(HANDLER)).show(name, window);
                return;
            }
        }

        SwingWindowDisplayHandler handler = resolveSwingWindowDisplayHandler(name);
        if (handler != null) {
            LOG.trace("Showing {} with injected handler", name);
            handler.show(name, window);
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

    public void hide(@Nonnull String name, @Nonnull JInternalFrame window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("hide");
            if (canBeRun(handler)) {
                LOG.trace("Hiding {} with hide: handler", name);
                run(handler, name, window);
                return;
            } else if (options.get(HANDLER) instanceof SwingWindowDisplayHandler) {
                LOG.trace("Hiding {} with handler: handler", name);
                ((SwingWindowDisplayHandler) options.get(HANDLER)).hide(name, window);
                return;
            }
        }

        SwingWindowDisplayHandler handler = resolveSwingWindowDisplayHandler(name);
        if (handler != null) {
            LOG.trace("Hiding {} with injected handler", name);
            handler.hide(name, window);
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

    protected void run(@Nonnull Object handler, @Nonnull String name, @Nonnull JInternalFrame window) {
        if (handler instanceof RunnableWithArgs) {
            ((RunnableWithArgs) handler).run(name, window);
        } else if (handler instanceof CallableWithArgs) {
            ((CallableWithArgs<?>) handler).call(name, window);
        }
    }

    @Nonnull
    @Override
    protected SwingWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (SwingWindowDisplayHandler) (handler instanceof SwingWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }

    @Override
    protected boolean handleShowByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            SwingWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
            LOG.trace("Showing {} with injected handler", name);
            handler.show(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleShowByInjectedHandler(name, window);
        }
    }

    @Override
    protected boolean handleHideByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            SwingWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
            LOG.trace("Hiding {} with injected handler", name);
            handler.hide(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleHideByInjectedHandler(name, window);
        }
    }

    protected SwingWindowDisplayHandler resolveSwingWindowDisplayHandler(@Nonnull String name) {
        try {
            return getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
        } catch (InstanceNotFoundException infe1) {
            try {
                WindowDisplayHandler handler = getApplication().getInjector()
                    .getInstance(WindowDisplayHandler.class, named(name));
                if (handler instanceof SwingWindowDisplayHandler) {
                    return ((SwingWindowDisplayHandler) handler);
                }
            } catch (InstanceNotFoundException infe2) {
                // ignore
            }
        }

        return null;
    }
}
