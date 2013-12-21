import griffon.util.AbstractMapResourceBundle;
import griffon.util.CollectionUtils;
import sample.SampleController;
import sample.SampleModel;
import sample.SampleView;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.Arrays.asList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        CollectionUtils.map(entries)
            //.e("application.startupGroups", asList("sample"))
            .e("application", CollectionUtils.map()
                .e("title", "Griffon Integration JavaFX")
                .e("startupGroups", asList("sample"))
            )
            .e("mvcGroups", CollectionUtils.map()
                .e("sample", CollectionUtils.map()
                    .e("model", SampleModel.class.getName())
                    .e("view", SampleView.class.getName())
                    .e("controller", SampleController.class.getName())
                )
            );
    }
}
