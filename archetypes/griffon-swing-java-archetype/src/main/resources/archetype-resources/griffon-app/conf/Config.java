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
                .e("title", "_app")
                .e("startupGroups", asList("_app"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("_app", map()
                    .e("model", "${package}._APPModel")
                    .e("view", "${package}._APPView")
                    .e("controller", "${package}._APPController")
                )
            );
    }
}