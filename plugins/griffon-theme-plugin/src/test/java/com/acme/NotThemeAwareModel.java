package com.acme;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.core.resources.InjectedResource;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonModel.class)
public class NotThemeAwareModel extends AbstractGriffonModel {
    @InjectedResource
    private String string;

    @Inject
    public NotThemeAwareModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        firePropertyChange("string", this.string, this.string = string);
    }
}
