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
package sample.javafx.kotlin

import griffon.core.artifact.GriffonService
import org.kordamp.jipsy.ServiceProviderFor
import griffon.util.GriffonNameUtils.isBlank
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService

@ServiceProviderFor(GriffonService::class)
class SampleService : AbstractGriffonService() {
    fun sayHello(input: String?): String {
        return if (isBlank(input)) {
            application.messageSource.getMessage("greeting.default")
        } else {
            application.messageSource.getMessage("greeting.parameterized", listOf(input))
        }
    }
}