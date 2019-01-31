/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.kordamp.jipsy.ServiceProviderFor;

@ServiceProviderFor(GriffonView.class)
public class RootView extends AbstractGriffonView implements Invokable {
    private RootController controller;
    private RootModel model;
    private boolean invoked;

    @MVCMember
    public void setController(RootController controller) {
        this.controller = controller;
    }

    @MVCMember
    public void setModel(RootModel model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        invoked = true;
    }

    public boolean isInvoked() {
        return invoked;
    }
}
