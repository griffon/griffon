/*
 * Copyright 2008-2014 the original author or authors.
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
package com.acme;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.core.resources.InjectedResource;
import griffon.metadata.ArtifactProviderFor;
import griffon.plugins.theme.ThemeAware;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ThemeAware
@ArtifactProviderFor(GriffonModel.class)
public class ThemeAwareModel extends AbstractGriffonModel {
    @InjectedResource
    private String string;

    @InjectedResource
    private String nonThemed;

    @Inject
    public ThemeAwareModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        firePropertyChange("string", this.string, this.string = string);
    }

    public String getNonThemed() {
        return nonThemed;
    }

    public void setNonThemed(String nonThemed) {
        firePropertyChange("nonThemed", this.nonThemed, this.nonThemed = nonThemed);
    }
}
