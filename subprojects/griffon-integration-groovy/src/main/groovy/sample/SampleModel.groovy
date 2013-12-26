package sample

import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonModel
import griffon.core.resources.InjectedResource
import org.codehaus.griffon.core.compile.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

import javax.annotation.Nonnull
import javax.inject.Inject
import java.awt.Color

@ArtifactProviderFor(GriffonModel)
class SampleModel extends AbstractGriffonModel {
    @InjectedResource(defaultValue = '#0000FF')
    Color color

    @InjectedResource
    Color color2

    String input

    @Inject
    SampleModel(@Nonnull GriffonApplication application) {
        super(application)
    }
}
