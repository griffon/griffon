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
package org.example.calculator;

import griffon.core.event.EventHandler;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.ServiceProviderFor;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.JsonCodec;

@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(EventHandler.class)
            .to(ApplicationEventHandler.class)
            .asSingleton();

        bind(ClientDolphin.class)
            .asSingleton();

        bind(UiThreadHandler.class)
            .to(GriffonUiThreadHandler.class)
            .asSingleton();

        bind(Codec.class)
            .to(JsonCodec.class)
            .asSingleton();

        bind(ClientModelStore.class)
            .toProvider(ClientModelStoreProvider.class)
            .asSingleton();

        bind(ClientConnector.class)
            .toProvider(ClientConnectorProvider.class)
            .asSingleton();
    }
}