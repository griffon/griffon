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
package console

import griffon.core.injection.Module
import griffon.inject.DependsOn
import griffon.swing.SwingWindowDisplayHandler
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

import static griffon.util.AnnotationUtils.named

@DependsOn('swing')                                                        //<3>
@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(Evaluator)                                                    //<1>
            .to(GroovyShellEvaluator)
            .asSingleton()

        bind(SwingWindowDisplayHandler)                                    //<2>
            .withClassifier(named('defaultWindowDisplayHandler'))
            .to(CenteringWindowDisplayHandler)
            .asSingleton()
    }
}
