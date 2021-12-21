package ${package};

import griffon.annotations.controller.ControllerAction;
import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonController;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

@ServiceProviderFor(GriffonController.class)
public class _APPController extends AbstractGriffonController {
    private _APPModel model;

    @MVCMember
    public void setModel(@Nonnull _APPModel model) {
        this.model = model;
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void click() {
        model.setClickCount(model.getClickCount() + 1);
    }
}