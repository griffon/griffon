/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.core.mvc.MVCGroup;
import griffon.inject.Contextual;
import griffon.inject.MVCMember;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

public class ChildController extends AbstractGriffonController {
    private ChildModel model;
    private ChildView view;
    private MVCGroup parentGroup;
    private RootController parentController;

    @MVCMember(converter = ListConverter.class)
    private List<String> list1 = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();
    private List<String> list3 = new ArrayList<>();

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

    @Nonnull
    public List<String> getList1() {
        return list1;
    }

    @MVCMember(converter = ListConverter.class)
    public void setList2(List<String> list) {
        this.list2.addAll(list);
    }

    @Nonnull
    public List<String> getList2() {
        return list2;
    }

    @MVCMember
    public void setList3(List<String> list) {
        this.list3.addAll(list);
    }

    @Nonnull
    public List<String> getList3() {
        return list3;
    }
}
