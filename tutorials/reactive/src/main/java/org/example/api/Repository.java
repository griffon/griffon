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
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository implements Comparable<Repository> {
    private String name;
    private String description;
    @Setter(onMethod = @__({@JsonProperty("full_name")}))
    private String fullName;
    @Setter(onMethod = @__({@JsonProperty("html_url")}))
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

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public int compareTo(Repository other) {
        if (other == null || !(other instanceof Repository)) { return 1; }
        return name.compareTo(other.name);
    }
}