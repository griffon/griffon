package sample

import griffon.core.artifact.GriffonController
import griffon.core.controller.ActionExecutionStatus
import org.codehaus.griffon.runtime.core.controller.AbstractActionInterceptor

import javax.annotation.Nonnull
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Named

@Named
class TracerActionInterceptor extends AbstractActionInterceptor {
    @Nonnull
    @Override
    Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        println("[START] ${qualifyActionName(controller, actionName)}")
        return args
    }

    @Override
    void after(@Nonnull ActionExecutionStatus status, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        println("[END] ${qualifyActionName(controller, actionName)}")
    }

    @PostConstruct
    void init() {
        println('<<< FOR GREAT JUSTICE! >>>')
    }

    @PreDestroy
    void destroy() {
        println('=== FUBAR ===')
    }
}
