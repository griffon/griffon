/*
 * Copyright 2008-2015 the original author or authors.
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
import griffon.builder.core.factory.RootFactory;
import griffon.core.threading.UIThreadManager;
import groovy.lang.Closure;
import groovy.util.Factory;
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer;
import org.codehaus.groovy.runtime.MethodClosure;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@Named("core")
public class CoreBuilderCustomizer extends AbstractBuilderCustomizer {
    @Inject
    private UIThreadManager uiThreadManager;

    public CoreBuilderCustomizer() {
        Map<String, Factory> factories = new LinkedHashMap<>();
        factories.put("root", new RootFactory());
        factories.put("metaComponent", new MetaComponentFactory());
        setFactories(factories);

    }

    @PostConstruct
    private void setup() {
        Map<String, Closure> methods = new LinkedHashMap<>();
        methods.put("runInsideUISync", new MethodClosure(uiThreadManager, "runInsideUISync"));
        methods.put("runInsideUIAsync", new MethodClosure(uiThreadManager, "runInsideUIAsync"));
        methods.put("runOutsideUI", new MethodClosure(uiThreadManager, "runOutsideUI"));
        methods.put("runFuture", new MethodClosure(uiThreadManager, "runFuture"));
        methods.put("isUIThread", new MethodClosure(uiThreadManager, "isUIThread"));
        setMethods(methods);
    }
}
