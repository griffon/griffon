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
package sample.pivot.groovy

import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.codehaus.griffon.runtime.util.ResourceBundleProvider
import org.kordamp.jipsy.ServiceProviderFor

import static griffon.util.AnnotationUtils.named

@ServiceProviderFor(Module.class)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ResourceBundle.class)
            .withClassifier(named('applicationResourceBundle'))
            .toProvider(new ResourceBundleProvider('sample.pivot.groovy.Config'))
            .asSingleton()
    }
}
