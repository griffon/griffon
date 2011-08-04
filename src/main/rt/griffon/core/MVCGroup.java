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

package griffon.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Defines the contents of an MVC group
 *
 * @author Andres Almiray
 */
public class MVCGroup {
    private final Map<String, String> configuration = new LinkedHashMap<String, String>();
    private final Map<String, Object> members = new LinkedHashMap<String, Object>();
    private final String mvcType;
    private final GriffonApplication app;
    private String mvcId;
    private boolean initialized;
    private final Object lock = new Object();

    public MVCGroup(GriffonApplication app, String mvcType, Map<String, String> configuration) {
        this.app = app;
        this.mvcType = mvcType;
        this.configuration.putAll(configuration);
    }

    public String getMvcType() {
        return mvcType;
    }

    public Map<String, String> getConfiguration() {
        return Collections.unmodifiableMap(configuration);
    }

    public String getMvcId() {
        return mvcId;
    }

    public boolean isInitialized() {
        synchronized (lock) {
            return initialized;
        }
    }

    public GriffonController getController() {
        return (GriffonController) getMember(GriffonControllerClass.TYPE);
    }

    public GriffonModel getModel() {
        return (GriffonModel) getMember(GriffonModelClass.TYPE);
    }

    public GriffonView getView() {
        return (GriffonView) getMember(GriffonViewClass.TYPE);
    }

    public Object getMember(String name) {
        return members.get(name);
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

    public void create(String mvcId, Map<String, Object> args) {
        synchronized (lock) {
            if (initialized) return;
            this.mvcId = !isBlank(mvcId) ? mvcId : mvcType;
            members.putAll(app.buildMVCGroup(args, mvcType, mvcId));
            initialized = true;
        }
    }

    public void destroy() {
        synchronized (lock) {
            if (initialized) {
                app.destroyMVCGroup(mvcId);
                members.clear();
                mvcId = null;
                initialized = false;
            }
        }
    }
}
