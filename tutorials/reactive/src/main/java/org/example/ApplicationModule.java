/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import griffon.core.event.EventHandler;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.example.api.DefaultGithub;
import org.example.api.Github;
import org.example.api.GithubAPI;
import org.example.api.GithubAPIProvider;
import org.example.api.ObjectMapperProvider;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import static griffon.util.AnnotationUtils.named;

@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(Github.class)
            .to(DefaultGithub.class)
            .asSingleton();

        bind(GithubAPI.class)
            .toProvider(GithubAPIProvider.class)
            .asSingleton();

        bind(ObjectMapper.class)
            .toProvider(ObjectMapperProvider.class)
            .asSingleton();

        bind(String.class)
            .withClassifier(named(GithubAPI.GITHUB_API_URL_KEY))
            .toInstance("https://api.github.com");

        bind(EventHandler.class)
            .to(ApplicationEventHandler.class)
            .asSingleton();
    }
}