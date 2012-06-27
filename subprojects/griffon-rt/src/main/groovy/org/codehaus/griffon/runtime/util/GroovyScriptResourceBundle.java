/*
 * Copyright 2010-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.util;

import griffon.util.ConfigReader;
import groovy.lang.Script;
import groovy.util.ConfigObject;

import java.net.URL;
import java.util.*;

import static griffon.util.ConfigUtils.getConfigValue;
import static org.codehaus.griffon.runtime.util.GriffonApplicationHelper.createConfigReader;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class GroovyScriptResourceBundle extends ResourceBundle {
    private final ConfigObject config;
    private final List<String> keys = new ArrayList<String>();

    public GroovyScriptResourceBundle(URL location) {
        this(null, location);
    }

    public GroovyScriptResourceBundle(Script script) {
        this(null, script);
    }

    public GroovyScriptResourceBundle(String script) {
        this(null, script);
    }

    public GroovyScriptResourceBundle(Class scriptClass) {
        this(null, scriptClass);
    }

    public GroovyScriptResourceBundle(ConfigReader reader, URL location) {
        this(resolveConfigReader(reader).parse(location));
    }


    public GroovyScriptResourceBundle(ConfigReader reader, Script script) {
        this(resolveConfigReader(reader).parse(script));
    }

    public GroovyScriptResourceBundle(ConfigReader reader, String script) {
        this(resolveConfigReader(reader).parse(script));
    }

    public GroovyScriptResourceBundle(ConfigReader reader, Class scriptClass) {
        this(resolveConfigReader(reader).parse(scriptClass));
    }

    private static ConfigReader resolveConfigReader(ConfigReader reader) {
        return null != reader ? reader : createConfigReader();
    }

    private GroovyScriptResourceBundle(ConfigObject config) {
        this.config = config;
        keys.addAll(this.config.flatten(new LinkedHashMap()).keySet());
    }

    protected Object handleGetObject(String key) {
        Object value = getConfigValue(config, key, null);
        return null == value ? null : String.valueOf(value);
    }

    public Enumeration<String> getKeys() {
        final Iterator<String> keysIterator = keys.iterator();
        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return keysIterator.hasNext();
            }

            public String nextElement() {
                return keysIterator.next();
            }
        };
    }
}
