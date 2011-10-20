/*
 * Copyright 2008-2011 the original author or authors.
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
package griffon.swing;

import griffon.core.GriffonApplication;
import griffon.util.ConfigUtils;
import griffon.util.GriffonNameUtils;
import griffon.util.RunnableWithArgs;
import groovy.lang.Closure;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation of a per window {@code WindowDisplayHandler} that can be configured via a DSL.<p>
 * This is the default {@code WindowDisplayHandler} used by {@code SwingApplication}. It expects a configuration
 * entry in <code>griffon-app/conf/Config.groovy</code> that looks like the following one<p>
 * <pre>
 * swing {
 *     windowManager {
 *         myWindowName = [
 *             show: {window, app -> ... },
 *             hide: {window, app -> ... }
 *         ]
 *         myOtherWindowName = [
 *             show: {window, app -> ... }
 *         ]
 *     }
 * }
 * </pre>
 * <p/>
 * For these settings to work you must specify a <code>name:</code> property on the Window/Frame instance. This
 * {@code WindowDisplayHandler} is smart enough to use the default show/hide behavior should any or both are not specified
 * or if a window name does not have a matching configuration. The default behavior will also be used if the Window/Frame
 * does not have a value for its <code>name:</code> property.<p>
 * There's a third option that can be set for each configured window, and that is a delegate {@code WindowDisplayHandler} that
 * will be used for that window alone. The following example shows how it can be configured<p>
 * <pre>
 * swing {
 *     windowManager {
 *         myWindowName = [
 *             handler: new MyCustomWindowDisplayHandler()
 *         ]
 *         myOtherWindowName = [
 *             show: {window, app -> ... }
 *         ]
 *     }
 * }
 * </pre>
 * <p/>
 * Lastly, a global handler can be specified for all windows that have not been configured. If specified, this handler will
 * override the usage of the default one. It can be configured as follows<p>
 * <pre>
 * swing {
 *     windowManager {
 *         defaultHandler = new MyCustomWindowDisplayHandler()
 *         myOtherWindowName = [
 *             show: {window, app -> ... }
 *         ]
 *     }
 * }
 * </pre>
 * <p/>
 * Fine grained control for default <code>show</code> and <code>hide</code> is also possible, by specifying <code>defaultShow</code>
 * and/or <code>defaultHide</code> properties at the global level. These properties take precedence over <code>defaultHandler</code> .
 * <p/>
 * <pre>
 * swing {
 *     windowManager {
 *         defaultHide = {window, app -> ... }
 *         myOtherWindowName = [
 *             show: {window, app -> ... }
 *         ]
 *     }
 * }
 * </pre>
 * <p/>
 * <strong>Note:</strong> the value for <code>show</code> and <code>hide</code> can be either a Closure or a {@code RunnableWithArgs}.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class ConfigurableWindowDisplayHandler implements WindowDisplayHandler {
    private static final WindowDisplayHandler DEFAULT_WINDOW_DISPLAY_HANDLER = new DefaultWindowDisplayHandler();

    public void show(Window window, GriffonApplication application) {
        String name = window.getName();
        if (!GriffonNameUtils.isBlank(name)) {
            Map<String, Object> options = windowBlock(application, name);
            if (!options.isEmpty()) {
                Object handler = options.get("show");
                if (canBeRun(handler)) {
                    run(handler, window, application);
                    return;
                } else if (options.get("handler") instanceof WindowDisplayHandler) {
                    ((WindowDisplayHandler) options.get("handler")).show(window, application);
                    return;
                }
            }
        }
        Map<String, Object> options = windowManagerBlock(application);
        if (!options.isEmpty()) {
            Object handler = options.get("defaultShow");
            if (canBeRun(handler)) {
                run(handler, window, application);
                return;
            }
        }
        fetchDefaultWindowDisplayHandler(application).show(window, application);
    }

    public void hide(Window window, GriffonApplication application) {
        String name = window.getName();
        if (!GriffonNameUtils.isBlank(name)) {
            Map<String, Object> options = windowBlock(application, name);
            if (!options.isEmpty()) {
                Object handler = options.get("hide");
                if (canBeRun(handler)) {
                    run(handler, window, application);
                    return;
                } else if (options.get("handler") instanceof WindowDisplayHandler) {
                    ((WindowDisplayHandler) options.get("handler")).hide(window, application);
                    return;
                }
            }
        }
        Map<String, Object> options = windowManagerBlock(application);
        if (!options.isEmpty()) {
            Object handler = options.get("defaultHide");
            if (canBeRun(handler)) {
                run(handler, window, application);
                return;
            }
        }
        fetchDefaultWindowDisplayHandler(application).hide(window, application);
    }

    private boolean canBeRun(Object obj) {
        return obj instanceof Closure || obj instanceof RunnableWithArgs;
    }

    private void run(Object obj, Window window, GriffonApplication application) {
        if (obj instanceof Closure) {
            ((Closure) obj).call(window, application);
        } else {
            ((RunnableWithArgs) obj).run(new Object[]{window, application});
        }
    }

    private Map<String, Object> windowManagerBlock(GriffonApplication application) {
        Map<String, Object> block = (Map<String, Object>) ConfigUtils.getConfigValue(application.getConfig(), "swing.windowManager");
        return block != null ? block : Collections.<String, Object>emptyMap();
    }

    private Map<String, Object> windowBlock(GriffonApplication application, String windowName) {
        Map<String, Object> options = windowManagerBlock(application);
        Map<String, Object> block = (Map<String, Object>) options.get(windowName);
        return block != null ? block : Collections.<String, Object>emptyMap();
    }

    private WindowDisplayHandler fetchDefaultWindowDisplayHandler(GriffonApplication application) {
        Object handler = windowManagerBlock(application).get("defaultHandler");
        return handler instanceof WindowDisplayHandler ? (WindowDisplayHandler) handler : DEFAULT_WINDOW_DISPLAY_HANDLER;
    }
}