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