import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import java.util.Map;

import static griffon.util.CollectionUtils.map;
import static java.util.Arrays.asList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                    .e("title", "JavaFX Views")
                    .e("startupGroups", asList("app"))
                    .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                    .e("app", map()
                            .e("view", "org.example.AppView")
                    )
                    .e("tab1", map()
                            .e("model", "org.example.SampleModel")
                            .e("view", "org.example.Tab1View")
                            .e("controller", "org.example.SampleController")
                    )
                    .e("tab2", map()
                            .e("model", "org.example.SampleModel")
                            .e("view", "org.example.Tab2View")
                            .e("controller", "org.example.SampleController")
                    )
                    .e("tab3", map()
                            .e("model", "org.example.SampleModel")
                            .e("view", "org.example.Tab3View")
                            .e("controller", "org.example.SampleController")
                    )
                    .e("tab4", map()
                            .e("model", "org.example.SampleModel")
                            .e("view", "org.example.Tab4View")
                            .e("controller", "org.example.SampleController")
                    )
            );
    }
}