package ${project_package};

import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

@Named("${plugin_name}")
@ServiceProviderFor(Module.class)
public class ${project_class_name}Module extends AbstractModule {
    @Override
    protected void doConfigure() {
    }
}
