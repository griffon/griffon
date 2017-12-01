/*
 * SPDX-License-Identifier: Apache-2.0
 *
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

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonNameUtils.isBlank;

public class ListPropertyEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (isBlank(text)) {
            setValue(new ArrayList<>());
        } else {
            List<String> value = new ArrayList<String>();
            for (String s : text.trim().split(",")) {
                value.add(s.trim());
            }
            setValue(value);
        }
    }
}