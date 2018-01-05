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
package griffon.util;

import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyDescriptor {
    private final String name;
    private final Class<?> type;
    private final Method readMethod;
    private final Method writeMethod;

    public PropertyDescriptor(String name, Class<?> type, Method readMethod, Method writeMethod) {
        this.name = name;
        this.type = type;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        PropertyDescriptor that = (PropertyDescriptor) o;

        if (!name.equals(that.name)) { return false; }
        if (!type.equals(that.type)) { return false; }
        if (readMethod != null ? !readMethod.equals(that.readMethod) : that.readMethod != null) { return false; }
        return writeMethod != null ? writeMethod.equals(that.writeMethod) : that.writeMethod == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (readMethod != null ? readMethod.hashCode() : 0);
        result = 31 * result + (writeMethod != null ? writeMethod.hashCode() : 0);
        return result;
    }
}
