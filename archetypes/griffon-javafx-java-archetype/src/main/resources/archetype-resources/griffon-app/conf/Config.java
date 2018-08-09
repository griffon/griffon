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
                .e("title", "App")
                .e("startupGroups", asList("app"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("app", map()
                    .e("model", "${groupId}.AppModel")
                    .e("view", "${groupId}.AppView")
                    .e("controller", "${groupId}.AppController")
                )
            );
    }
}