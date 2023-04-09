/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package integration;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.Contextual;
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonController;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Inject;
import javax.inject.Named;

@ServiceProviderFor(GriffonController.class)
public class IntegrationController extends AbstractGriffonController {
    private IntegrationModel model;
    private String key;

    @Inject
    private IntegrationService sampleService;

    @MVCMember
    public void setModel(@Nonnull IntegrationModel model) {
        this.model = model;
    }

    public void abort() {
        throw new RuntimeException("Abort!");
    }

    public void sayHello() {
        final String result = sampleService.sayHello(model.getInput());
        executeInsideUIAsync(() -> model.setOutput(result));
    }

    public void throwException() {
        throw new RuntimeException("Boom!");
    }

    public void handleException() {
        throw new RuntimeException("Boom!");
    }

    public void contextualSuccess(@Contextual @Named("key") String key) {
        this.key = key;
    }

    public void contextualFailure(@Contextual @Nonnull @Named("undefined") String key) {
        // empty
    }

    public String getKey() {
        return key;
    }
}
