/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCGroup;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.kordamp.jipsy.ServiceProviderFor;

@ServiceProviderFor(GriffonView.class)
public class ChildView extends AbstractGriffonView implements Invokable {
    private ChildController controller;
    private ChildModel model;
    private MVCGroup parentGroup;
    private RootView parentView;
    private boolean invoked;

    public ChildController getController() {
        return controller;
    }

    @MVCMember
    public void setController(ChildController controller) {
        this.controller = controller;
    }

    public ChildModel getModel() {
        return model;
    }

    @MVCMember
    public void setModel(ChildModel model) {
        this.model = model;
    }

    public MVCGroup getParentGroup() {
        return parentGroup;
    }

    @MVCMember
    public void setParentGroup(MVCGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public RootView getParentView() {
        return parentView;
    }

    @MVCMember
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
