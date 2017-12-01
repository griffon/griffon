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
package griffon.pivot.support;

import griffon.exceptions.InstanceMethodInvocationException;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PivotUtils {
    private PivotUtils() {
        // prevent instantiation
    }

    /**
     * Searches a component by name in a particular component hierarchy.<p>
     * A component must have a value for its <tt>name</tt> property if it's
     * to be found with this method.<br/>
     * This method performs a depth-first search.
     *
     * @param name the value of the component's <tt>name</tt> property
     * @param root the root of the component hierarchy from where searching
     *             searching should start
     *
     * @return the component reference if found, null otherwise
     */
    @Nullable
    public static Component findComponentByName(@Nonnull String name, @Nonnull Container root) {
        requireNonNull(root, "Argument 'root' must not be null");
        requireNonBlank(name, "Argument 'name' must not be blank");
        if (name.equals(root.getName())) {
            return root;
        }

        for (Component comp : root) {
            if (name.equals(comp.getName())) {
                return comp;
            }
            if (comp instanceof Container) {
                Component found = findComponentByName(name, (Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }

        // finally attempt calling getContent()
        try {
            Component component = (Component) invokeExactInstanceMethod(root, "getContent");
            if (component != null) {
                if (name.equals(component.getName())) {
                    return component;
                } else if (component instanceof Container) {
                    return findComponentByName(name, (Container) component);
                }
            }
        } catch (InstanceMethodInvocationException imie) {
            // ignore
        }

        return null;
    }
}
