/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import griffon.core.artifact.GriffonController;
import griffon.core.mvc.MVCGroup;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.kordamp.jipsy.ServiceProviderFor;

@ServiceProviderFor(GriffonController.class)
public class SimpleController extends AbstractGriffonController {
    private SimpleModel model;
    private String key;
    private String mvcId;
    @MVCMember
    private MVCGroup parentGroup;
    @MVCMember
    private IntegrationModel parentModel;

    @MVCMember
    public void setModel(SimpleModel model) {
        this.model = model;
    }

    public String getMvcId() {
        return mvcId;
    }

    @MVCMember
    public void setMvcId(String mvcId) {
        this.mvcId = mvcId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MVCGroup getParentGroup() {
        return parentGroup;
    }

    public IntegrationModel getParentModel() {
        return parentModel;
    }
}
