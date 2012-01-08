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

package org.codehaus.griffon.runtime.core;


import griffon.core.GriffonApplication;
import griffon.core.MVCGroup;
import griffon.core.MVCGroupConfiguration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Base implementation of the {@code MVCGroupConfiguration} interface
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractMVCGroupConfiguration implements MVCGroupConfiguration {
    protected final Map<String, String> members = new LinkedHashMap<String, String>();
    protected final String mvcType;
    protected final GriffonApplication app;

    public AbstractMVCGroupConfiguration(GriffonApplication app, String mvcType, Map<String, String> members) {
        this.app = app;
        this.mvcType = mvcType;
        this.members.putAll(members);
    }

    public final GriffonApplication getApp() {
        return app;
    }

    public final String getMvcType() {
        return mvcType;
    }

    public final Map<String, String> getMembers() {
        return Collections.unmodifiableMap(members);
    }

    public final MVCGroup create() {
        return create(mvcType, Collections.<String, Object>emptyMap());
    }

    public final MVCGroup create(String mvcId) {
        return create(mvcId, Collections.<String, Object>emptyMap());
    }

    public final MVCGroup create(Map<String, Object> args) {
        return create(mvcType, args);
    }

    public final MVCGroup create(String mvcId, Map<String, Object> args) {
        return instantiateMVCGroup(!isBlank(mvcId) ? mvcId : mvcType, args);
    }

    protected abstract MVCGroup instantiateMVCGroup(String mvcId, Map<String, Object> args);
}
