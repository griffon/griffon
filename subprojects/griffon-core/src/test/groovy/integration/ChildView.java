/*
 * Copyright 2008-2015 the original author or authors.
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
package integration;

import griffon.core.mvc.MVCGroup;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

public class ChildView extends AbstractGriffonView implements Invokable {
    private RootController controller;
    private RootModel model;
    private MVCGroup parentGroup;
    private RootView parentView;
    private boolean invoked;

    public RootController getController() {
        return controller;
    }

    public void setController(RootController controller) {
        this.controller = controller;
    }

    public RootModel getModel() {
        return model;
    }

    public void setModel(RootModel model) {
        this.model = model;
    }

    public MVCGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(MVCGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public RootView getParentView() {
        return parentView;
    }

    public void setParentView(RootView parentView) {
        this.parentView = parentView;
    }

    @Override
    public void initUI() {
        invoked = true;
    }

    public boolean isInvoked() {
        return invoked;
    }
}
