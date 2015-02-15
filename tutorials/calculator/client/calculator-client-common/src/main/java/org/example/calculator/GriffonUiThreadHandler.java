package org.example.calculator;

import griffon.core.threading.UIThreadManager;
import org.opendolphin.core.client.comm.UiThreadHandler;

import javax.inject.Inject;

public class GriffonUiThreadHandler implements UiThreadHandler {
    @Inject
    private UIThreadManager uiThreadManager;

    @Override
    public void executeInsideUiThread(Runnable runnable) {
        uiThreadManager.runInsideUIAsync(runnable);
    }
}
