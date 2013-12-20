package griffon.builder.core;

import griffon.builder.core.factory.MetaComponentFactory;
import griffon.builder.core.factory.RootFactory;
import groovy.util.Factory;
import org.codehaus.griffon.runtime.core.view.AbstractBuilderCustomizer;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@Named("core")
public class CoreBuilderCustomizer extends AbstractBuilderCustomizer {
    public CoreBuilderCustomizer() {
        Map<String, Factory> factories = new LinkedHashMap<>();
        factories.put("root", new RootFactory());
        factories.put("metaComponent", new MetaComponentFactory());
        setFactories(factories);
    }
}
