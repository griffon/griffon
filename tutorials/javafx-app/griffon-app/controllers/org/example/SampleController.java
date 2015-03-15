package org.example;

import griffon.core.artifact.GriffonController;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.inject.Inject;

@ArtifactProviderFor(GriffonController.class)
public class SampleController extends AbstractGriffonController {
    private SampleModel model;

    @Inject
    private SampleService sampleService;

    public void setModel(SampleModel model) {
        this.model = model;
    }

    public void sayHello() {
        final String result = sampleService.sayHello(model.getInput());
        runInsideUIAsync(() -> model.setOutput(result));
    }
}