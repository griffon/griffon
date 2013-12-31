package sample;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionExecutionStatus;
import org.codehaus.griffon.runtime.core.controller.AbstractActionInterceptor;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

@Named
public class TracerActionInterceptor extends AbstractActionInterceptor {
    @Nonnull
    @Override
    public Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        System.out.println("[START] " + qualifyActionName(controller, actionName));
        return args;
    }

    @Override
    public void after(@Nonnull ActionExecutionStatus status, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        System.out.println("[END] " + qualifyActionName(controller, actionName));
    }

    @PostConstruct
    public void init() {
        System.out.println("<<< FOR GREAT JUSTICE! >>>");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("=== FUBAR ===");
    }
}
