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
package org.codehaus.griffon.runtime.theme;

import griffon.core.GriffonApplication;
import griffon.core.injection.Qualified;
import griffon.core.resources.ResourceResolver;
import griffon.plugins.theme.ThemeManager;
import org.codehaus.griffon.runtime.core.AbstractVetoable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyVetoException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultThemeManager extends AbstractVetoable implements ThemeManager {
    private final Map<String, ResourceResolver> themes = new LinkedHashMap<>();
    private String currentTheme;

    @Inject
    public DefaultThemeManager(@Nonnull GriffonApplication application) {
        requireNonNull(application, "Argument 'application' must not be null");

        ResourceResolver applicationResourceResolver = application.getResourceResolver();
        for (Qualified<ResourceResolver> qualified : application.getInjector().getQualifiedInstances(ResourceResolver.class)) {
            if (!applicationResourceResolver.equals(qualified.getInstance())) {
                String name = nameFor(qualified);
                if (!isBlank(name)) themes.put(name, qualified.getInstance());
            }
        }

        requireState(themes.size() > 0, "No suitable instances of " + ResourceResolver.class.getName() + " were found.");
        currentTheme = themes.keySet().iterator().next();
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private String nameFor(@Nonnull Qualified<ResourceResolver> qualified) {
        Annotation qualifier = qualified.getQualifier();
        if (qualifier == null) {
            return null;
        }
        if (Named.class.isAssignableFrom(qualifier.getClass())) {
            Named named = (Named) qualifier;
            return named.value();
        }
        return null;
    }

    @Nonnull
    @Override
    public Set<String> getThemes() {
        return unmodifiableSet(themes.keySet());
    }

    @Nonnull
    @Override
    public String getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void setCurrentTheme(@Nonnull String theme) throws PropertyVetoException {
        requireNonBlank(theme, "Argument 'theme' must not be null");
        if (themes.containsKey(theme)) {
            fireVetoableChange(PROPERTY_CURRENT_THEME, this.currentTheme, theme);
            firePropertyChange(PROPERTY_CURRENT_THEME, this.currentTheme, this.currentTheme = theme);
        }
    }

    @Nonnull
    @Override
    public ResourceResolver getResourceResolver() {
        return themes.get(currentTheme);
    }
}
