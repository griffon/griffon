/*
 * Copyright 2008-2016 the original author or authors.
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

import org.apache.pivot.wtk.Slider
import org.apache.pivot.wtk.Span

/**
 * @author Andres Almiray
 */
class SliderFactory extends ComponentFactory {
    SliderFactory() {
        super(Slider)
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        def range = attributes.range
        if (range instanceof Range) {
            attributes.range = new Span(range.from as int, range.to as int)
        }
        return super.onHandleNodeAttributes(builder, node, attributes)
    }
}
