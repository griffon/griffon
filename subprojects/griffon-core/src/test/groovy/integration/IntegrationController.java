/*
 * Copyright 2008-2014 the original author or authors.
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

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.inject.Inject;

public class IntegrationController extends AbstractGriffonController {
    private IntegrationModel model;

    @Inject
    private IntegrationService sampleService;

    public void setModel(IntegrationModel model) {
        this.model = model;
    }

    public void abort() {
        throw new RuntimeException("Abort!");
    }

    public void sayHello() {
        final String result = sampleService.sayHello(model.getInput());
        runInsideUIAsync(new Runnable() {
            @Override
            public void run() {
                model.setOutput(result);
            }
        });
    }

    public void throwException() {
        throw new RuntimeException("Boom!");
    }

    public void handleException() {
        throw new RuntimeException("Boom!");
    }
}
