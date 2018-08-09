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
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonController;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.Map;

@ServiceProviderFor(GriffonController.class)
public class ArgsController extends AbstractGriffonController {
    private ArgsModel model;
    private ArgsView view;
    @MVCMember @Nonnull
    private String arg1;
    private String arg2;

    @MVCMember
    public void setModel(@Nonnull ArgsModel model) {
        this.model = model;
    }

    @MVCMember
    public void setView(@Nonnull ArgsView view) {
        this.view = view;
    }

    @Nonnull
    public String getArg1() {
        return arg1;
    }

    @Nonnull
    public String getArg2() {
        return arg2;
    }

    @MVCMember
    public void setArg2(@Nonnull String arg2) {
        this.arg2 = arg2;
    }

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        getMvcGroup().getContext().put("KEY", "VALUE");
    }
}
