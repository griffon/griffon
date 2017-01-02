/*
 * Copyright 2008-2017 the original author or authors.
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
import griffon.inject.Contextual;
import griffon.inject.MVCMember;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Named;

public class ChildController extends AbstractGriffonController {
    private ChildModel model;
    private ChildView view;
    private MVCGroup parentGroup;
    private RootController parentController;

    @Contextual @Named("KEY")
    private String value;

    private String theField;

    @Nonnull
    public String getVal() {
        return theField;
    }

    @Contextual
    public void setVal(@Named("KEY") String theField) {
        this.theField = theField;
    }

    public ChildModel getModel() {
        return model;
    }

    @MVCMember
    public void setModel(@Nonnull ChildModel model) {
        this.model = model;
    }

    public ChildView getView() {
        return view;
    }

    @MVCMember
    public void setView(@Nonnull ChildView view) {
        this.view = view;
    }

    public MVCGroup getParentGroup() {
        return parentGroup;
    }

    @MVCMember
    public void setParentGroup(@Nonnull MVCGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public RootController getParentController() {
        return parentController;
    }

    @MVCMember
    public void setParentController(@Nonnull RootController parentController) {
        this.parentController = parentController;
    }
}
