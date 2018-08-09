package ${project_package};

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ControllerAction;
import griffon.annotations.inject.MVCMember;
import org.kordamp.jipsy.ServiceProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.application.threading.Threading;
import griffon.annotations.core.Nonnull;

@ServiceProviderFor(GriffonController.class)
public class ${project_class_name}Controller extends AbstractGriffonController {
    private ${project_class_name}Model model;

    @MVCMember
    public void setModel(@Nonnull ${project_class_name}Model model) {
        this.model = model;
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void click() {
        int count = Integer.parseInt(model.getClickCount());
        model.setClickCount(String.valueOf(count + 1));
    }
}