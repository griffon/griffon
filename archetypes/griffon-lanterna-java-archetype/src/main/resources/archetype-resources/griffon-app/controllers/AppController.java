package ${groupId};

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ControllerAction;
import griffon.annotations.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.application.threading.Threading;
import griffon.annotations.core.Nonnull;

@ArtifactProviderFor(GriffonController.class)
public class AppController extends AbstractGriffonController {
    private AppModel model;

    @MVCMember
    public void setModel(@Nonnull AppModel model) {
        this.model = model;
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void click() {
        model.setClickCount(model.getClickCount() + 1);
    }
}