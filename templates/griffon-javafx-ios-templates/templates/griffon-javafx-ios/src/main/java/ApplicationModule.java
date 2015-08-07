package ${project_package};

import griffon.core.event.EventHandler;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.ResourceBundle;
import static griffon.util.AnnotationUtils.named;

@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ResourceBundle.class)
            .withClassifier(named("applicationResourceBundle"))
            .toProvider(new ResourceBundleProvider("${project_package}.Config"))
            .asSingleton();

        bind(EventHandler.class)
            .to(ApplicationEventHandler.class)
            .asSingleton();
    }
}