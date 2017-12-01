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
package griffon.swing;

import griffon.swing.support.SwingUtils;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Simple implementation of {@code GriffonApplication} that runs in applet mode.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 * @since 0.1
 */
public class SwingGriffonApplet extends AbstractGriffonApplet {
    private static final long serialVersionUID = -992561285851621751L;

    private boolean appletContainerDispensed = false;

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        if (appletContainerDispensed) {
            return SwingUtils.createApplicationFrame(this, attributes);
        } else {
            appletContainerDispensed = true;
            return this;
        }
    }
}
