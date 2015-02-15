package org.example.calculator;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class ClientModelStoreProvider implements Provider<ClientModelStore> {
    @Inject
    private ClientDolphin clientDolphin;

    @Override
    public ClientModelStore get() {
        ClientModelStore clientModelStore = new ClientModelStore(clientDolphin);
        clientDolphin.setClientModelStore(clientModelStore);
        return clientModelStore;
    }
}
