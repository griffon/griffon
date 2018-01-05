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
package sample.pivot.java;

import griffon.core.artifact.GriffonService;
import griffon.core.i18n.MessageSource;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;

@javax.inject.Singleton
@ArtifactProviderFor(GriffonService.class)
public class SampleService extends AbstractGriffonService {
    public String sayHello(String input) {
        MessageSource messageSource = getApplication().getMessageSource();
        if (isBlank(input)) {
            return messageSource.getMessage("greeting.default");
        } else {
            return messageSource.getMessage("greeting.parameterized", asList(input));
        }
    }
}