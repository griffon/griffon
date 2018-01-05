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
package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class GithubAPIProvider implements Provider<GithubAPI> {
    @Inject
    @Named(GithubAPI.GITHUB_API_URL_KEY)
    private String githubApiUrl;

    @Inject private ObjectMapper objectMapper;

    @Override
    public GithubAPI get() {
        return new Retrofit.Builder()
            .baseUrl(githubApiUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(GithubAPI.class);
    }
}