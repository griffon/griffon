import griffon.util.AbstractMapResourceBundle;
import griffon.util.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.Arrays.asList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        CollectionUtils.map(entries)
            .e("application", CollectionUtils.map()
                .e("title", "${project_name}")
                .e("startupGroups", asList("${project_property_name}"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", CollectionUtils.map()
                .e("${project_property_name}", CollectionUtils.map()
                    .e("model", "${project_package}.${project_class_name}Model")
                    .e("view", "${project_package}.${project_class_name}View")
                    .e("controller", "${project_package}.${project_class_name}Controller")
                )
            );
    }
}