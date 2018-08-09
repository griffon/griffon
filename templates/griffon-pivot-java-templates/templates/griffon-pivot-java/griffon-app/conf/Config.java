import griffon.util.AbstractMapResourceBundle;

import griffon.annotations.core.Nonnull;
import java.util.Map;

import static java.util.Arrays.asList;
import static griffon.util.CollectionUtils.map;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                .e("title", "${project_name}")
                .e("startupGroups", asList("${project_property_name}"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("${project_property_name}", map()
                    .e("model", "${project_package}.${project_class_name}Model")
                    .e("view", "${project_package}.${project_class_name}View")
                    .e("controller", "${project_package}.${project_class_name}Controller")
                )
            );
    }
}