/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.builder.lanterna3.factory

import com.googlecode.lanterna.gui2.Window

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
class ApplicationFactory extends ComponentFactory {
    ApplicationFactory() {
        super(Window)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        String windowName = attributes.remove('name') ?: computeWindowName()
        String title = attributes.remove('title') ?: builder.application.configuration['application.title']
        Window window = builder.application.createApplicationContainer([title: title])
        builder.application.windowManager.attach(windowName, window)
        window
    }

    private static int COUNT = 0

    private static String computeWindowName() {
        'window' + (COUNT++)
    }
}
