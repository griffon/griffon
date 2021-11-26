/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.builder.core;

import griffon.builder.core.factory.MetaComponentFactory;
import griffon.core.threading.UIThreadManager;
import groovy.lang.Closure;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer;
import org.codehaus.groovy.runtime.MethodClosure;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.TypeUtils.castToBoolean;
import static java.util.Collections.singletonList;

/**
 * @author Andres Almiray
 */
@Named("core")
public class CoreBuilderCustomizer extends AbstractBuilderCustomizer {
    private static final String KEY_ROOT_NODE_NAME = "ROOT_NODE_NAME";
    private static final String MVC_ID = "mvcId";
    private static final String ROOT_NODE_IDENTIFIED_BY_ATTRIBUTE = "rootNodeIdentifiedByAttribute";

    @Inject
    private UIThreadManager uiThreadManager;

    public CoreBuilderCustomizer() {
        Map<String, Factory> factories = new LinkedHashMap<>();
        factories.put("metaComponent", new MetaComponentFactory());
        setFactories(factories);
    }

    @PostConstruct
    private void setup() {
        Map<String, Closure> methods = new LinkedHashMap<>();
        methods.put("runInsideUISync", new MethodClosure(uiThreadManager, "runInsideUISync"));
        methods.put("runInsideUIAsync", new MethodClosure(uiThreadManager, "runInsideUIAsync"));
        methods.put("runOutsideUI", new MethodClosure(uiThreadManager, "runOutsideUI"));
        methods.put("runOutsideUIAsync", new MethodClosure(uiThreadManager, "runOutsideUIAsync"));
        methods.put("runFuture", new MethodClosure(uiThreadManager, "runFuture"));
        methods.put("isUIThread", new MethodClosure(uiThreadManager, "isUIThread"));
        setMethods(methods);
    }

    @Nonnull
    @Override
    public List<Closure> getAttributeDelegates() {
        return singletonList(new MethodClosure(this, "rootAttributeDelegate"));
    }

    @Nonnull
    @Override
    public List<Closure> getPreInstantiateDelegates() {
        return singletonList(new MethodClosure(this, "rootNodePreInstantiateDelegate"));
    }

    @Nonnull
    @Override
    public List<Closure> getPostNodeCompletionDelegates() {
        return singletonList(new MethodClosure(this, "rootNodePostNodeCompletionDelegate"));
    }

    protected void rootAttributeDelegate(FactoryBuilderSupport builder, Object node, Map attributes) {
        Object isRootNode = attributes.remove("rootNode");
        if (isRootNode != null && castToBoolean(isRootNode)) {
            String mvcId = String.valueOf(builder.getVariable(MVC_ID));
            builder.getVariables().put(ROOT_NODE_IDENTIFIED_BY_ATTRIBUTE, true);
            builder.getVariables().put(mvcId + "-rootNode", node);
        }
    }

    protected void rootNodePreInstantiateDelegate(FactoryBuilderSupport builder, Map attributes, Object value) {
        String name = String.valueOf(builder.getContext().get(FactoryBuilderSupport.CURRENT_NAME));
        if (!builder.hasVariable(KEY_ROOT_NODE_NAME)) {
            builder.setVariable(KEY_ROOT_NODE_NAME, name);
        }
    }

    protected void rootNodePostNodeCompletionDelegate(FactoryBuilderSupport builder, Object parent, Object node) {
        Object hasRootNode = builder.getVariables().get(ROOT_NODE_IDENTIFIED_BY_ATTRIBUTE);
        if (hasRootNode != null && castToBoolean(hasRootNode)) {
            return;
        }

        String name = String.valueOf(builder.getContext().get(FactoryBuilderSupport.CURRENT_NAME));
        if (builder.getVariable(KEY_ROOT_NODE_NAME).equals(name) && builder.hasVariable(MVC_ID)) {
            String mvcId = String.valueOf(builder.getVariable(MVC_ID));
            builder.getVariables().put(mvcId + "-rootNode", node);
        }
    }
}
