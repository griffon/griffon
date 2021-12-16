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
package griffon.core.editors;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public class EnumPropertyEditor extends AbstractPropertyEditor {
    private Class<? extends Enum> enumType;

    public Class<? extends Enum> getEnumType() {
        return enumType;
    }

    public void setEnumType(Class<? extends Enum> enumType) {
        this.enumType = enumType;
    }

    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof Enum) {
            handleAsString(value.toString());
        } else {
            throw illegalValue(value, enumType);
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        try {
            super.setValueInternal(Enum.valueOf(enumType, str));
        } catch (Exception e) {
            throw illegalValue(str, enumType, e);
        }
    }
}
