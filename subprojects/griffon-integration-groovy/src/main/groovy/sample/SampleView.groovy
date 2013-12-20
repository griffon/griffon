package sample

import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView

import javax.annotation.Nonnull
import javax.inject.Inject

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

class SampleView extends AbstractGriffonView {
    FactoryBuilderSupport builder

    @Inject
    SampleView(@Nonnull GriffonApplication application) {
        super(application)
    }

    @Override
    void initUI() {
        builder.application(title: 'Main', id: 'mainWindow', size: [320, 240],
            defaultCloseOperation: DO_NOTHING_ON_CLOSE) {
            gridLayout(cols: 2, rows: 2)
            button(clickAction)
            button(clickAction)
            button(clickAction)
            button(clickAction)
        }
    }
}
