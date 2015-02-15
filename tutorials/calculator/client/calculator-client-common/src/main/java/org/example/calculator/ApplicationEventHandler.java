package org.example.calculator;

import griffon.core.GriffonApplication;
import griffon.core.event.EventHandler;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.ClientConnector;

import javax.inject.Inject;

public class ApplicationEventHandler implements EventHandler {
    @Inject
    private ClientDolphin clientDolphin;

    @Inject
    private ClientModelStore clientModelStore;

    @Inject
    private ClientConnector clientConnector;

    public void onBootstrapStart(GriffonApplication application) {

    }
}