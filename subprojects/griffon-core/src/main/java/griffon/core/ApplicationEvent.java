/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.core;

import static griffon.util.GriffonNameUtils.getClassNameForLowerCaseHyphenSeparatedName;

/**
 * Defines all the events triggered by the application.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public enum ApplicationEvent {
    UNCAUGHT_EXCEPTION_THROWN,
    LOAD_ADDONS_START, LOAD_ADDONS_END, LOAD_ADDON_START, LOAD_ADDON_END,
    BOOTSTRAP_START, BOOTSTRAP_END,
    STARTUP_START, STARTUP_END,
    READY_START, READY_END,
    STOP_START, STOP_END,
    SHUTDOWN_REQUESTED, SHUTDOWN_ABORTED, SHUTDOWN_START,
    NEW_INSTANCE, DESTROY_INSTANCE,
    INITIALIZE_MVC_GROUP("InitializeMVCGroup"), CREATE_MVC_GROUP("CreateMVCGroup"), DESTROY_MVC_GROUP("DestroyMVCGroup"),
    WINDOW_SHOWN, WINDOW_HIDDEN, WINDOW_ATTACHED, WINDOW_DETACHED;

    /**
     * Display friendly name
     */
    private final String name;

    ApplicationEvent() {
        String name = name().toLowerCase().replaceAll("_", "-");
        this.name = getClassNameForLowerCaseHyphenSeparatedName(name);
    }

    ApplicationEvent(String name) {
        this.name = name;
    }

    /**
     * Returns the capitalized String representation of this Event object.
     *
     * @return a capitalized String
     */
    public String getName() {
        return this.name;
    }
}