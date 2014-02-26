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
package griffon.plugins.domain;

import griffon.core.GriffonApplication;
import griffon.metadata.ArtifactProviderFor;
import griffon.plugins.validation.constraints.ConstraintDef;
import griffon.transform.Domain;
import org.codehaus.griffon.runtime.domain.AbstractGriffonDomain;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static griffon.plugins.validation.constraints.Constraints.blank;
import static griffon.plugins.validation.constraints.Constraints.list;
import static griffon.plugins.validation.constraints.Constraints.map;
import static griffon.plugins.validation.constraints.Constraints.size;

@Domain
@ArtifactProviderFor(GriffonDomain.class)
public class Person extends AbstractGriffonDomain {
    @Inject
    public Person(@Nonnull GriffonApplication application) {
        super(application);
    }

    private Long id;
    private String name;
    private String lastname;

    public static final Map<String, List<ConstraintDef>> CONSTRAINTS = map()
        .e("name", list(blank(false), size(3, 10)))
        .e("lastname", list(blank(false), size(4, 20)));

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String toString() {
        return "Person[id=" + id + ", name=" + name + ", lastname=" + lastname + "]";
    }
}
