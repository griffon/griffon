package \${package}

import griffon.annotations.javafx.FXObservable
import griffon.core.artifact.GriffonModel
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel
import org.kordamp.jipsy.annotations.ServiceProviderFor

@ServiceProviderFor(GriffonModel)
class _APPModel extends AbstractGriffonModel {
    @FXObservable String clickCount = '0'
}