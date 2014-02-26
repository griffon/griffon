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

import griffon.util.GriffonClassUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public final class GriffonDomainClassUtils {
    private static final Pattern PLURAL_PATTERN = Pattern.compile(".*[^aeiouy]y", Pattern.CASE_INSENSITIVE);

    private final Map<String, String> ENTITY_NAMES = new LinkedHashMap<>();
    private static final GriffonDomainClassUtils INSTANCE = new GriffonDomainClassUtils();

    public static GriffonDomainClassUtils getInstance() {
        return INSTANCE;
    }

    private GriffonDomainClassUtils() {
    }

    public void addTo(GriffonDomain owner, Map<String, Object> params, String relationshipName) {
        if (owner == null || params == null || params.isEmpty() || isBlank(relationshipName)) {
            return;
        }
        GriffonDomainClass<?> ownerDomainClass = (GriffonDomainClass) owner.getGriffonClass();
    }

    @SuppressWarnings("ConstantConditions")
    public void addTo(GriffonDomain owner, GriffonDomain target, String relationshipName) {
        if (owner == null || target == null || isBlank(relationshipName)) {
            return;
        }
        GriffonDomainClass<?> ownerDomainClass = (GriffonDomainClass) owner.getGriffonClass();
        GriffonDomainProperty relationship = ownerDomainClass.getPropertyByName(relationshipName);
        Collection collection = (Collection) relationship.getValue(owner);
        GriffonClassUtils.invokeInstanceMethod(collection, "add", target);
        // set owning side on target
    }

    @SuppressWarnings("unchecked")
    public void addAllTo(GriffonDomain owner, Collection targets, String relationshipName) {
        if (targets == null || targets.isEmpty()) return;
        for (Object target : targets) {
            if (target instanceof GriffonDomain) {
                addTo(owner, (GriffonDomain) target, relationshipName);
            } else if (target instanceof Map) {
                addTo(owner, (Map) target, relationshipName);
            }
        }
    }

    // ----------------------------------------------

    private String fetchAndSetEntityNameFor(GriffonDomainClass<?> domain) {
        String key = domain.getClazz().getName();
        String entityName = domain.getClazz().getSimpleName();
        boolean matchesIESRule = PLURAL_PATTERN.matcher(entityName).matches();
        entityName = matchesIESRule ? entityName.substring(0, entityName.length() - 1) + "ies" : entityName + "s";
        ENTITY_NAMES.put(key, entityName);
        return entityName;
    }
}