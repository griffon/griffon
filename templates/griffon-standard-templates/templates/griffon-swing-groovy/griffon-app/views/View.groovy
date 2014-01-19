package ${project_package}

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class ${project_capitalized_name}View {
    FactoryBuilderSupport builder
    ${project_capitalized_name}Model model

    void initUI() {
        builder.with {
            application(title: application.applicationConfiguration['application.title'],
                id: 'mainWindow', size: [320, 160],
                iconImage:   imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                             imageIcon('/griffon-icon-32x32.png').image,
                             imageIcon('/griffon-icon-16x16.png').image]) {
                gridLayout(rows: 4, cols: 1)
                label(application.messageSource.getMessage('name.label'))
                textField(text: bind(target: model, 'input'))
                button(sayHelloAction)
                label(text: bind { model.output })
            }
        }
    }
}