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
                .e("title", "Griffon Integration")
                .e("startupGroups", asList("sample"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", CollectionUtils.map()
                .e("sample", CollectionUtils.map()
                    .e("model", "sample.SampleModel")
                    .e("view", "sample.SampleView")
                    .e("controller", "sample.SampleController")
                )
            );
    }
}
