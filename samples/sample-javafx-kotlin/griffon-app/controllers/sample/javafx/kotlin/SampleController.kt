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
package sample.javafx.kotlin

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import griffon.annotations.core.Nonnull
import javax.inject.Inject

@ServiceProviderFor(GriffonController::class)
class SampleController : AbstractGriffonController() {
    @set:[MVCMember Nonnull]
    lateinit var model: SampleModel

    @Inject
    lateinit var sampleService: SampleService

    @ControllerAction
    fun sayHello() {
        val result = sampleService.sayHello(model.input)
        executeInsideUIAsync { model.output = result }
    }
}