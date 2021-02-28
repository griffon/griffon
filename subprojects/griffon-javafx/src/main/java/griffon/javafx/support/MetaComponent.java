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
package griffon.javafx.support;

import griffon.annotations.core.Nonnull;
import javafx.beans.DefaultProperty;

import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 2.12.0
 */
@DefaultProperty("mvcArgs")
public class MetaComponent {
    private String mvcType;
    private String mcvId;
    private final List<MvcArg> mvcArgs = new ArrayList<>();

    public String getMvcType() {
        return mvcType;
    }

    public void setMvcType(String mvcType) {
        this.mvcType = mvcType;
    }

    public String getMcvId() {
        return mcvId;
    }

    public void setMcvId(String mcvId) {
        this.mcvId = mcvId;
    }

    @Nonnull
    public List<MvcArg> getMvcArgs() {
        return mvcArgs;
    }

    public static class MvcArg {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(@Nonnull String name) {
            this.name = requireNonBlank(name, "Argument 'name' must not be blank");
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
