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
package griffon.core.properties;

import griffon.annotations.core.Nonnull;
import griffon.exceptions.PropertyVetoException;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface VetoableChangeListener {
    /**
     * This method gets called when a constrained property is changed.
     *
     * @param evt a <code>PropertyChangeEvent</code> object describing the
     *            event source and the property that has changed.
     * @throws griffon.exceptions.PropertyVetoException if the recipient wishes the property
     *                                                  change to be rolled back.
     */
    void vetoableChange(@Nonnull PropertyChangeEvent evt) throws PropertyVetoException;
}
