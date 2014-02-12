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
package integration;

import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SimpleController extends AbstractGriffonController {
    private SimpleModel model;
    private String key;
    private String mvcId;

    @Inject
    public SimpleController(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setModel(SimpleModel model) {
        this.model = model;
    }

    public String getMvcId() {
        return mvcId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMvcId(String mvcId) {
        this.mvcId = mvcId;
    }
}
