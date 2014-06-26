import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler

import javax.annotation.Nonnull
import javax.inject.Inject

import static griffon.util.GriffonApplicationUtils.isMacOSX
import static groovy.swing.SwingBuilder.lookAndFeel

class Initialize extends AbstractLifecycleHandler {
    @Inject
    Initialize(@Nonnull GriffonApplication application) {
        super(application)
    }

    @Override
    void execute() {
        lookAndFeel((isMacOSX ? 'system' : 'nimbus'), 'gtk', ['metal', [boldFonts: false]])
    }
}