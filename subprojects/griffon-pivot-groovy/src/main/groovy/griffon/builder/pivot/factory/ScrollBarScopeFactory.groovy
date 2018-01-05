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
package griffon.builder.pivot.factory

import org.apache.pivot.wtk.ScrollBar

/**
 * @author Andres Almiray
 */
class ScrollBarScopeFactory extends AbstractFactory {
    boolean isLeaf() {
        return true
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        ['start', 'end', 'extent'].each { prop ->
            if (!attributes.containsKey(prop)) {
                throw new IllegalArgumentException("In $name you must define a value for $prop: of type int")
            }
        }

        new ScrollBar.Scope(
                attributes.remove('start') as int,
                attributes.remove('end') as int,
                attributes.remove('extend') as int
        )
    }
}
