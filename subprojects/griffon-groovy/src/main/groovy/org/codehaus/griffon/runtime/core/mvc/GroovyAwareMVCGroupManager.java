/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.mvc;

import griffon.core.ApplicationClassLoader;
import griffon.core.GriffonApplication;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import groovy.lang.Script;
import groovy.util.FactoryBuilderSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static org.codehaus.griffon.runtime.core.mvc.GroovyAwareMVCGroup.BUILDER;

/**
 * @author Andres Almiray
 */
public class GroovyAwareMVCGroupManager extends DefaultMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager.class);

    @Inject
    public GroovyAwareMVCGroupManager(@Nonnull GriffonApplication application, @Nonnull ApplicationClassLoader applicationClassLoader) {
        super(application, applicationClassLoader);
    }

    @Nonnull
    @Override
    public MVCGroup newMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members) {
        return new GroovyAwareMVCGroup(this, configuration, mvcId, members);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> instantiateMembers(@Nonnull Map<String, ClassHolder> classMap, @Nonnull Map<String, Object> args) {
        Map<String, Object> map = super.instantiateMembers(classMap, args);
        FactoryBuilderSupport builder = createBuilder(getApplication());
        map.put(BUILDER, builder);

        for (Object member : map.values()) {
            // all scripts get the builder as their binding
            if (member instanceof Script) {
                builder.getVariables().putAll(((Script) member).getBinding().getVariables());
                ((Script) member).setBinding(builder);
            }
        }

        return map;
    }

    private FactoryBuilderSupport createBuilder(@Nonnull GriffonApplication application) {
        return new FactoryBuilderSupport() {
        };
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void adjustMvcArguments(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        super.adjustMvcArguments(group, args);
        FactoryBuilderSupport builder = (FactoryBuilderSupport) group.getMember(BUILDER);
        for (Map.Entry<String, Object> variable : args.entrySet()) {
            builder.setVariable(variable.getKey(), variable.getValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void fillNonArtifactMemberProperties(@Nonnull String type, @Nonnull Object member, @Nonnull Map<String, Object> args) {
        if (member instanceof Script) {
            ((Script) member).getBinding().getVariables().putAll(args);
        }
    }

    @Override
    protected void initializeNonArtifactMember(@Nonnull String type, @Nonnull Object member, @Nonnull Map<String, Object> args) {

    }

    @Override
    protected void destroyMembers(@Nonnull MVCGroup group, boolean fireDestructionEvents) {
        super.destroyMembers(group, fireDestructionEvents);

        try {
            FactoryBuilderSupport builder = (FactoryBuilderSupport) group.getMember(BUILDER);
            if (builder != null) {
                builder.dispose();
                builder.getVariables().clear();
            }
        } catch (Exception e) {
            // TODO find out why this call breaks applet mode on shutdown
            LOG.error("Application encountered an error while destroying group '" + group.getMvcId() + "'", sanitize(e));
        }
    }
}
