package com.acme;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.core.resources.InjectedResource;
import griffon.metadata.ArtifactProviderFor;
import griffon.plugins.theme.ThemeAware;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ThemeAware
@ArtifactProviderFor(GriffonModel.class)
public class ThemeAwareModel extends AbstractGriffonModel {
    @InjectedResource
    private String string;

    @InjectedResource
    private String nonThemed;

    @Inject
    public ThemeAwareModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        firePropertyChange("string", this.string, this.string = string);
    }

    public String getNonThemed() {
        return nonThemed;
    }

    public void setNonThemed(String nonThemed) {
        firePropertyChange("nonThemed", this.nonThemed, this.nonThemed = nonThemed);
    }
}
