/* 
 * Copyright 2004-2012 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;

/**
 * A default implementation for Griffon classes that need to be registered and managed by a GriffonApplication,
 * but don't need any special handling.
 *
 * @author Graeme Rocher (Grails 0.1)
 * @author Andres Almiray
 * @since 0.9.1
 */
public class DefaultGriffonClass extends AbstractGriffonClass {
    public DefaultGriffonClass(GriffonApplication app, Class<?> clazz, String type, String trailingName) {
        super(app, clazz, type, trailingName);
    }

    public DefaultGriffonClass(GriffonApplication app, Class<?> clazz, String type) {
        super(app, clazz, type, "");
    }
}
