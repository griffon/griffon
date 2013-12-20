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
import griffon.core.artifact.GriffonArtifact;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.util.BuilderCustomizer;
import groovy.lang.Script;
import groovy.util.FactoryBuilderSupport;
import griffon.util.CompositeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonClassUtils.getDependsOn;
import static griffon.util.GriffonClassUtils.mapInstancesByName;
import static org.codehaus.griffon.runtime.core.mvc.GroovyAwareMVCGroup.BUILDER;

/**
 * @author Andres Almiray
 */
public class GroovyAwareMVCGroupManager extends DefaultMVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMVCGroupManager.class);
    private static final String BUILDER_CUSTOMIZER = "BuilderCustomizer";

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

    @Nonnull
    protected FactoryBuilderSupport createBuilder(@Nonnull GriffonApplication application) {
        Collection<BuilderCustomizer> customizers = resolveBuilderCustomizers(application);
        return new CompositeBuilder(customizers.toArray(new BuilderCustomizer[customizers.size()]));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void adjustMvcArguments(@Nonnull MVCGroup group, @Nonnull Map<String, Object> args) {
        super.adjustMvcArguments(group, args);
        FactoryBuilderSupport builder = (FactoryBuilderSupport) group.getMember(BUILDER);
        args.put(BUILDER, builder);
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
    protected void initializeArtifactMember(@Nonnull MVCGroup group, @Nonnull String type, @Nonnull GriffonArtifact member, @Nonnull Map<String, Object> args) {
        if (member instanceof Script) {
            ((GroovyAwareMVCGroup) group).buildScriptMember(type);
        } else {
            super.initializeArtifactMember(group, type, member, args);
        }
    }

    @Override
    protected void initializeNonArtifactMember(@Nonnull MVCGroup group, @Nonnull String type, @Nonnull Object member, @Nonnull Map<String, Object> args) {
        if (member instanceof Script) {
            ((GroovyAwareMVCGroup) group).buildScriptMember(type);
        } else {
            super.initializeNonArtifactMember(group, type, member, args);
        }
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

    @Nonnull
    protected Collection<BuilderCustomizer> resolveBuilderCustomizers(@Nonnull GriffonApplication application) {
        Collection<BuilderCustomizer> customizerInstances = getApplication().getInjector().getInstances(BuilderCustomizer.class);
        Map<String, BuilderCustomizer> customizers = mapInstancesByName(customizerInstances, BUILDER_CUSTOMIZER);

        Map<String, BuilderCustomizer> map = new LinkedHashMap<>();
        map.putAll(customizers);

        List<BuilderCustomizer> sortedCustomizers = new ArrayList<>();
        Set<String> addedDeps = new LinkedHashSet<>();

        while (!map.isEmpty()) {
            int processed = 0;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Current customizer order is " + customizers.keySet());
            }

            for (Iterator<Map.Entry<String, BuilderCustomizer>> iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, BuilderCustomizer> entry = iter.next();
                String customizerName = entry.getKey();
                String[] dependsOn = getDependsOn(entry.getValue());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processing customizer '" + customizerName + "'");
                    LOG.debug("    depends on '" + Arrays.toString(dependsOn) + "'");
                }

                if (dependsOn.length != 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("  Checking customizer '" + customizerName + "' dependencies (" + dependsOn.length + ")");
                    }

                    boolean failedDep = false;
                    for (String dep : dependsOn) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("  Checking customizer '" + customizerName + "' dependencies: " + dep);
                        }
                        if (!addedDeps.contains(dep)) {
                            // dep not in the list yet, we need to skip adding this to the list for now
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("  Skipped customizer '" + customizerName + "', since dependency '" + dep + "' not yet added");
                            }
                            failedDep = true;
                            break;
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("  Customizer '" + customizerName + "' dependency '" + dep + "' already added");
                            }
                        }
                    }

                    if (failedDep) {
                        // move on to next dependency
                        continue;
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("  Adding customizer '" + customizerName + "', since all dependencies have been added");
                }
                sortedCustomizers.add(entry.getValue());
                addedDeps.add(customizerName);
                iter.remove();
                processed++;
            }

            if (processed == 0) {
                // we have a cyclical dependency, warn the user and load in the order they appeared originally
                if (LOG.isWarnEnabled()) {
                    LOG.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    LOG.warn(":: Unresolved builder customizer dependencies detected ::");
                    LOG.warn(":: Continuing with original customizer order           ::");
                    LOG.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                }
                for (Map.Entry<String, BuilderCustomizer> entry : map.entrySet()) {
                    String customizerName = entry.getKey();
                    String[] dependsOn = getDependsOn(entry.getValue());

                    // display this as a cyclical dep
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("::   Customizer " + customizerName);
                    }
                    if (dependsOn.length != 0) {
                        for (String dep : dependsOn) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("::     depends on " + dep);
                            }
                        }
                    } else {
                        // we should only have items left in the list with deps, so this should never happen
                        // but a wise man once said...check for true, false and otherwise...just in case
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(":: Problem while resolving dependencies.");
                            LOG.warn(":: Unable to resolve dependency hierarchy.");
                        }
                    }
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    }
                }
                break;
                // if we have processed all the customizers, we are done
            } else if (sortedCustomizers.size() == customizers.size()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Customizer dependency ordering complete");
                }
                break;
            }
        }

        customizers = mapInstancesByName(customizerInstances, BUILDER_CUSTOMIZER);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Computed builder customizer order is " + customizers.keySet());
        }

        return customizers.values();
    }
}
