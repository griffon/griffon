/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example.calculator.helpers;

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

    @Override
    public ClientConnector get() {
        String url = "http://localhost:8080/openmdm/dolphin/v1";
        ClientConnector connector = new HttpClientConnector(clientDolphin, url);
        connector.setCodec(codec);
        connector.setUiThreadHandler(uiThreadHandler);
        clientDolphin.setClientConnector(connector);
        return connector;
    }
}
