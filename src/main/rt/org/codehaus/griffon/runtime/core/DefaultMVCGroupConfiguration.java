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

package org.codehaus.griffon.runtime.core;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

import griffon.core.GriffonApplication;
import griffon.core.MVCGroup;
import griffon.core.MVCGroupConfiguration;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;

/**
 * Default implementation of the {@code MVCGroupConfiguration} interface
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class DefaultMVCGroupConfiguration implements MVCGroupConfiguration {
    private final Map<String, String> configuration = new LinkedHashMap<String, String>();
    private final String mvcType;
    private final GriffonApplication app;

    public DefaultMVCGroupConfiguration(GriffonApplication app, String mvcType, Map<String, String> configuration) {
        this.app = app;
        this.mvcType = mvcType;
        this.configuration.putAll(configuration);
    }

    public GriffonApplication getApp() {
        return app;
    }

    public String getMvcType() {
        return mvcType;
    }

    public Map<String, String> getConfiguration() {
        return Collections.unmodifiableMap(configuration);
    }

    public void create() {
        create(mvcType, Collections.<String, Object>emptyMap());
    }

    public void create(String mvcId) {
        create(mvcId, Collections.<String, Object>emptyMap());
    }

    public void create(Map<String, Object> args) {
        create(mvcType, args);
    }

    public MVCGroup create(String mvcId, Map<String, Object> args) {
        mvcId = !isBlank(mvcId) ? mvcId : mvcType;
        return new DefaultMVCGroup(app, this, mvcId, GriffonApplicationHelper.buildMVCGroup(app, mvcType, mvcId, args));
    }
}
