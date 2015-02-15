package org.example.calculator;

import griffon.core.artifact.GriffonController;
import griffon.core.mvc.MVCGroup;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import java.util.Map;

@ArtifactProviderFor(GriffonController.class)
public class AppController extends AbstractGriffonController {
    private AppView view;

    public void setView(AppView view) {
        this.view = view;
    }

    @Override
    public void mvcGroupInit(Map<String, Object> args) {
        final MVCGroup calculator = createMVCGroup("calculator");
        runInsideUIAsync(new Runnable() {
            @Override
            public void run() {
                CalculatorView calculatorView = (CalculatorView) calculator.getView();
                view.getContainer().setCenter(calculatorView.getCalculatorNode());
            }
        });
    }
}