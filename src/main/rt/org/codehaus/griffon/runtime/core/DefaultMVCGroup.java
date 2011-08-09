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


import java.util.LinkedHashMap;
import java.util.Map;

import griffon.core.*;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;

/**
 * Default implementation of the {@code MVCGroup} interface
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class DefaultMVCGroup implements MVCGroup {
    private final GriffonApplication app;
    private final MVCGroupConfiguration configurationDefault;
    private final String mvcId;
    private final Map<String, Object> members = new LinkedHashMap<String, Object>();

    public DefaultMVCGroup(GriffonApplication app, MVCGroupConfiguration configurationDefault, String mvcId, Map<String, Object> members) {
        this.app = app;
        this.configurationDefault = configurationDefault;
        this.mvcId = mvcId;
        this.members.putAll(members);
    }

    public GriffonApplication getApp() {
        return app;
    }

    public MVCGroupConfiguration getConfiguration() {
        return configurationDefault;
    }

    public String getMvcType() {
        return configurationDefault.getMvcType();
    }

    public String getMvcId() {
        return mvcId;
    }

    public GriffonModel getModel() {
        return (GriffonModel) getMember(GriffonModelClass.TYPE);
    }

    public GriffonView getView() {
        return (GriffonView) getMember(GriffonViewClass.TYPE);
    }

    public GriffonController getController() {
        return (GriffonController) getMember(GriffonControllerClass.TYPE);
    }

    public Object getMember(String name) {
        return members.get(name);
    }

    public void destroy() {
        GriffonApplicationHelper.destroyMVCGroup(app, mvcId);
        members.clear();
    }
}
