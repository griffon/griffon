package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel::class)
class ${project_class_name}Model : AbstractGriffonModel() {
    private var _clickCount: StringProperty = SimpleStringProperty(this, "clickCount", "0")

    var clickCount: String
    get() = _clickCount.get()
    set(s) = _clickCount.set(s)

    fun clickCountProperty() = _clickCount
}