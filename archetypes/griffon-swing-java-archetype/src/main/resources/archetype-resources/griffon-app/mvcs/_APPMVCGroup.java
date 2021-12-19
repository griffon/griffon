package ${package};

import javax.inject.Named;
import griffon.core.mvc.MVCGroup;
import org.codehaus.griffon.runtime.core.mvc.AbstractTypedMVCGroup;
import griffon.annotations.core.Nonnull;

@Named("_app")
public class _APPMVCGroup extends AbstractTypedMVCGroup<_APPModel, _APPView, _APPController> {
    public _APPMVCGroup(@Nonnull MVCGroup delegate) {
        super(delegate);
    }
}