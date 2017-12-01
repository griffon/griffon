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
package griffon.builder.javafx.factory

import groovyx.javafx.factory.StageFactory
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window

/**
 *
 * @author Dean Iverson
 */
class ApplicationFactory extends StageFactory {
    ApplicationFactory() {
        super(Stage)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        Window window = builder.application.createApplicationContainer([:])
        String windowName = (attributes.remove('name') ?: attributes.id) ?: computeWindowName()
        builder.application.windowManager.attach(windowName, window)
        window
    }

    private static int COUNT = 0

    private static String computeWindowName() {
        'window' + (COUNT++)
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        def stage = node as Stage
        // stage.width = 800
        // stage.height = 600

        attributes.each { key, value ->
            if (key == "title")
                stage.title = value
        }

        if (!stage.title && builder.application.configuration['application.title']) {
            stage.title = builder.application.configuration['application.title']
        }

        def style = attributes.remove("style")
        if (style == null) {
            style = StageStyle.DECORATED;
        }
        if (style instanceof String) {
            style = StageStyle.valueOf(style.toUpperCase())
        }
        stage.style = style

        builder.context.put("sizeToScene", attributes.remove("sizeToScene"))
        builder.context.put("centerOnScreen", attributes.remove("centerOnScreen"))

        return true
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        if (node instanceof Stage) {
            if (builder.context.sizeToScene || node.getWidth() == -1) {
                node.sizeToScene()
            }
            if (builder.context.centerOnScreen) {
                node.centerOnScreen();
            }
        }
    }
}
