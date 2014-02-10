package sample.swing.java;

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
                .e("title", "Swing + Java")
                .e("startupGroups", asList("sample"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", CollectionUtils.map()
                .e("sample", CollectionUtils.map()
                    .e("model", "sample.swing.java.SampleModel")
                    .e("view", "sample.swing.java.SampleView")
                    .e("controller", "sample.swing.java.SampleController")
                )
            );
    }
}
