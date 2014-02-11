package sample.javafx.groovy

import griffon.core.LifecycleHandler
import griffon.core.env.Lifecycle
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.LifecycleHandlerProvider
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.codehaus.griffon.runtime.util.ResourceBundleProvider
import org.kordamp.jipsy.ServiceProviderFor

import static griffon.util.AnnotationUtils.named

@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ResourceBundle)
            .withClassifier(named('applicationResourceBundle'))
            .toProvider(new ResourceBundleProvider('sample.javafx.groovy.Config'))
            .asSingleton()

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider('sample.javafx.groovy.' + lifecycle.getName()))
                .asSingleton()
        }
    }
}
