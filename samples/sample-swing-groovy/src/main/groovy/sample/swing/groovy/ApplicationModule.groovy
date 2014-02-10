package sample.swing.groovy

import griffon.core.LifecycleHandler
import griffon.core.env.Lifecycle
import griffon.core.injection.Module
import griffon.inject.DependsOn
import griffon.swing.SwingWindowDisplayHandler
import org.codehaus.griffon.runtime.core.LifecycleHandlerProvider
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.codehaus.griffon.runtime.util.ResourceBundleProvider
import org.kordamp.jipsy.ServiceProviderFor

import static griffon.util.AnnotationUtils.named

@DependsOn('swing')
@ServiceProviderFor(Module.class)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named('defaultWindowDisplayHandler'))
            .to(CenteringWindowDisplayHandler.class)
            .asSingleton()

        bind(ResourceBundle.class)
            .withClassifier(named('applicationResourceBundle'))
            .toProvider(new ResourceBundleProvider('sample.swing.groovy.Config'))
            .asSingleton()

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider('sample.swing.groovy.' + lifecycle.getName()))
                .asSingleton()
        }
    }
}
