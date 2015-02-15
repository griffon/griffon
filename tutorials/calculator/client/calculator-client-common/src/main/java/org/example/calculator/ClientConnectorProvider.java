package org.example.calculator;

import griffon.core.Configuration;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Codec;

import javax.inject.Inject;
import javax.inject.Provider;

public class ClientConnectorProvider implements Provider<ClientConnector> {
    @Inject
    private ClientDolphin clientDolphin;

    @Inject
    private Codec codec;

    @Inject
    private UiThreadHandler uiThreadHandler;

    @Inject
    private Configuration applicationConfiguration;

    @Override
    public ClientConnector get() {
        String url = applicationConfiguration.getAsString("dolphin.server.url", "http://localhost:8080/griffon/dolphin/v1");
        ClientConnector connector = new HttpClientConnector(clientDolphin, url);
        connector.setCodec(codec);
        connector.setUiThreadHandler(uiThreadHandler);
        clientDolphin.setClientConnector(connector);
        return connector;
    }
}
