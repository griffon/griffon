/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.codehaus.griffon.runtime.core.view;

import griffon.core.ApplicationConfiguration;
import griffon.core.CallableWithArgs;
import griffon.core.view.WindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of a per window {@code WindowDisplayHandler} that can be configured via a DSL.<p>
 * This is the default {@code WindowDisplayHandler} used by {@code SwingApplication}. It expects a applicationConfiguration
 * entry in <code>griffon-app/conf/Config.groovy</code> that looks like the following one<p>
 * <pre>
 *     windowManager {
 *         myWindowName = [
 *             show: {window -> ... },
 *             hide: {window -> ... }
 *         ]
 *         myOtherWindowName = [
 *             show: {window -> ... }
 *         ]
 *     }
 * </pre>
 * <p/>
 * For these settings to work you must specify a <code>name:</code> property on the Window/Frame instance. This
 * {@code WindowDisplayHandler} is smart enough to use the default show/hide behavior should any or both are not specified
 * or if a window name does not have a matching applicationConfiguration. The default behavior will also be used if the Window/Frame
 * does not have a value for its <code>name:</code> property.<p>
 * There's a third option that can be set for each configured window, and that is a delegate {@code WindowDisplayHandler} that
 * will be used for that window alone. The following example shows how it can be configured<p>
 * <pre>
 *     windowManager {
 *         myWindowName = [
 *             handler: new MyCustomWindowDisplayHandler()
 *         ]
 *         myOtherWindowName = [
 *             show: {window -> ... }
 *         ]
 *     }
 * </pre>
 * <p/>
 * Lastly, a global handler can be specified for all windows that have not been configured. If specified, this handler will
 * override the usage of the default one. It can be configured as follows<p>
 * <pre>
 *     windowManager {
 *         defaultHandler = new MyCustomWindowDisplayHandler()
 *         myOtherWindowName = [
 *             show: {window -> ... }
 *         ]
 *     }
 * </pre>
 * <p/>
 * Fine grained control for default <code>show</code> and <code>hide</code> is also possible, by specifying <code>defaultShow</code>
 * and/or <code>defaultHide</code> properties at the global level. These properties take precedence over <code>defaultHandler</code> .
 * <p/>
 * <pre>
 *     windowManager {
 *         defaultHide = {window -> ... }
 *         myOtherWindowName = [
 *             show: {window -> ... }
 *         ]
 *     }
 * </pre>
 * <p/>
 * <strong>Note:</strong> the value for <code>show</code> and <code>hide</code> can be either a Closure or a {@code RunnableWithArgs}.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class ConfigurableWindowDisplayHandler<W> implements WindowDisplayHandler<W> {
    protected static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    protected static final String ERROR_WINDOW_NULL = "Argument 'window' cannot be null";

    private final ApplicationConfiguration applicationConfiguration;
    private final WindowDisplayHandler<W> delegateWindowsDisplayHandler;

    @Inject
    public ConfigurableWindowDisplayHandler(@Nonnull ApplicationConfiguration applicationConfiguration, @Nonnull @Named("defaultWindowDisplayHandler") WindowDisplayHandler<W> delegateWindowsDisplayHandler) {
        this.applicationConfiguration = requireNonNull(applicationConfiguration, "Argument 'applicationConfiguration' cannot be null");
        this.delegateWindowsDisplayHandler = requireNonNull(delegateWindowsDisplayHandler, "Argument 'delegateWindowsDisplayHandler' cannot be null");
    }

    @SuppressWarnings("unchecked")
    public void show(@Nonnull String name, @Nonnull W window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("show");
            if (canBeRun(handler)) {
                run((CallableWithArgs) handler, window);
                return;
            } else if (options.get("handler") instanceof WindowDisplayHandler) {
                ((WindowDisplayHandler<W>) options.get("handler")).show(name, window);
                return;
            }
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object handler = options.get("defaultShow");
            if (canBeRun(handler)) {
                run((CallableWithArgs) handler, window);
                return;
            }
        }

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
                run((CallableWithArgs) handler, window);
                return;
            } else if (options.get("handler") instanceof WindowDisplayHandler) {
                ((WindowDisplayHandler<W>) options.get("handler")).hide(name, window);
                return;
            }
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object handler = options.get("defaultHide");
            if (canBeRun(handler)) {
                run((CallableWithArgs) handler, window);
                return;
            }
        }
        fetchDefaultWindowDisplayHandler().hide(name, window);
    }

    public WindowDisplayHandler<W> getDelegateWindowsDisplayHandler() {
        return delegateWindowsDisplayHandler;
    }

    protected boolean canBeRun(@Nullable Object obj) {
        return obj instanceof CallableWithArgs;
    }

    protected void run(CallableWithArgs handler, W window) {
        handler.call(window);
    }

    protected Map<String, Object> windowManagerBlock() {
        return applicationConfiguration.get("windowManager", Collections.<String, Object>emptyMap());
    }

    protected Map<String, Object> windowBlock(String windowName) {
        Map<String, Object> options = windowManagerBlock();
        return getConfigValue(options, windowName, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected WindowDisplayHandler<W> fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return handler instanceof WindowDisplayHandler ? (WindowDisplayHandler<W>) handler : delegateWindowsDisplayHandler;
    }
}