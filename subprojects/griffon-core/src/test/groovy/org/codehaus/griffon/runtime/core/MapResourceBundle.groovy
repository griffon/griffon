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
package org.codehaus.griffon.runtime.core

import griffon.util.AbstractMapResourceBundle

import javax.annotation.Nonnull

class MapResourceBundle extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        entries['key.string'] = 'string'
        entries['key.boolean.type'] = true
        entries['key.boolean.string'] = 'true'
        entries['key.int.type'] = 42
        entries['key.int.string'] = '21'
        entries['key.long.type'] = 64L
        entries['key.long.string'] = '32'
        entries['key.float.type'] = 3.1416f
        entries['key.float.string'] = '6.2832'
        entries['key.double.type'] = 3.1416d
        entries['key.double.string'] = '6.2832'
        entries['key.date.string'] = '1970-12-24'
    }
}