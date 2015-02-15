package org.example.calculator.helpers

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.WithPresentationModelHandler

import javax.inject.Inject
import java.util.concurrent.CountDownLatch

class TestClientDolphin {
    @Inject
    private ClientDolphin clientDolphin

    @Inject
    private ClientModelStore clientModelStore

    @Inject
    private ClientConnector clientConnector

    Object methodMissing(String name, Object args) {
        clientDolphin.invokeMethod(name, args)
    }

    Object propertyMissing(String name) {
        clientDolphin.getProperty(name)
    }

    void propertyMissing(String name, Object arg) {
        clientDolphin.setProperty(name, arg)
    }

    void send(String commandName, Closure onFinished) {
        CountDownLatch latch = new CountDownLatch(1)
        clientDolphin.send(commandName)
        clientDolphin.sync { latch.countDown() }
        latch.await()
        onFinished()
    }

    void withPresentationModel(String requestedPmId, WithPresentationModelHandler withPmHandler) {
        CountDownLatch latch = new CountDownLatch(1)
        clientModelStore.withPresentationModel(requestedPmId, withPmHandler)
        clientDolphin.sync { latch.countDown() }
        latch.await()
    }
}