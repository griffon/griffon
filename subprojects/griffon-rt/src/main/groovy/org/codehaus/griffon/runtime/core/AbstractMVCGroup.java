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


import griffon.core.*;
import groovy.util.FactoryBuilderSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base implementation of the {@code MVCGroup} interface
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractMVCGroup implements MVCGroup {
    protected final GriffonApplication app;
    protected final MVCGroupConfiguration configuration;
    protected final String mvcId;
    protected final Map<String, Object> members = new LinkedHashMap<String, Object>();
    private boolean alive;
    private final Object[] lock = new Object[0];

    public AbstractMVCGroup(GriffonApplication app, MVCGroupConfiguration configuration, String mvcId, Map<String, Object> members) {
        this.app = app;
        this.configuration = configuration;
        this.mvcId = mvcId;
        this.members.putAll(members);
        this.alive = true;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public MVCGroupConfiguration getConfiguration() {
        return configuration;
    }

    public String getMvcType() {
        return configuration.getMvcType();
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

    public FactoryBuilderSupport getBuilder() {
        return (FactoryBuilderSupport) getMember("builder");
    }

    public Object getMember(String name) {
        checkIfAlive();
        return members.get(name);
    }

    public void destroy() {
        checkIfAlive();
        app.getMvcGroupManager().destroyMVCGroup(mvcId);
        members.clear();
        synchronized (lock) {
            alive = false;
        }
    }

    public boolean isAlive() {
        synchronized (lock) {
            return alive;
        }
    }

    protected void checkIfAlive() {
        if (!isAlive()) {
            throw new IllegalStateException("Group " + getMvcType() + ":" + mvcId + " has been destroyed already.");
        }
    }
}
