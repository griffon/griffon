/*
 * Copyright 2016 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import griffon.metadata.TypeProviderFor;
import lombok.Builder;

@TypeProviderFor(JsonEntity.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository implements Comparable<Repository> {
    private String name;
    private String fullName;
    private String description;
    private String htmlUrl;

    @Builder
    public static Repository build(String name, String fullName, String description, String htmlUrl) {
        Repository repository = new Repository();
        repository.name = name;
        repository.fullName = fullName;
        repository.description = description;
        repository.htmlUrl = htmlUrl;
        return repository;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("html_url")
    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public int compareTo(Repository other) {
        if (other == null || !(other instanceof Repository)) return 1;
        return name.compareTo(other.name);
    }
}