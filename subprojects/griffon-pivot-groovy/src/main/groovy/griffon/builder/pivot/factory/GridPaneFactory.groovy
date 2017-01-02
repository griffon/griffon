/*
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
package griffon.builder.pivot.factory

import org.apache.pivot.wtk.Component
import org.apache.pivot.wtk.GridPane

/**
 * @author Andres Almiray
 */
class GridPaneFactory extends ComponentFactory {
    GridPaneFactory() {
        super(GridPane)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof GridPane.Row) parent.rows.add(child)
        else super.setChild(builder, parent, child)
    }
}

/**
 * @author Andres Almiray
 */
class GridPaneRowFactory extends ComponentFactory {
    GridPaneRowFactory() {
        super(GridPane.Row)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) parent.add(child)
        else super.setChild(builder, parent, child)
    }
}
