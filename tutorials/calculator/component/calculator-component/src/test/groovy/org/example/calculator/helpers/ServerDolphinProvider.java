package org.example.calculator.helpers;

import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class ServerDolphinProvider implements Provider<ServerDolphin> {
    @Inject
    private ServerModelStore serverModelStore;

    @Inject
    private ServerConnector serverConnector;

    @Inject
    private Codec codec;

    @Override
    public ServerDolphin get() {
        serverConnector.setCodec(codec);
        serverConnector.setServerModelStore(serverModelStore);
        ServerDolphin serverDolphin = new ServerDolphin(serverModelStore, serverConnector);
        serverDolphin.registerDefaultActions();
        return serverDolphin;
    }
}
