package org.codehaus.griffon.runtime.swing

import com.google.inject.AbstractModule
import griffon.core.ExecutorServiceManager
import griffon.core.threading.UIThreadManager
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.jukito.JukitoRunner
import org.jukito.UseModules
import org.junit.runner.RunWith

import javax.application.threading.ThreadingHandler
import javax.application.threading.ThreadingHandlerTest
import javax.inject.Inject
import javax.inject.Singleton
import javax.swing.SwingUtilities
import java.util.concurrent.ExecutorService

import static griffon.util.AnnotationUtils.named

@RunWith(JukitoRunner)
@UseModules(TestModule)
class SwingUIThreadManagerTest extends ThreadingHandlerTest {
    @Inject private UIThreadManager uiThreadManager

    @Override
    protected ThreadingHandler resolveThreadingHandler() {
        return uiThreadManager
    }

    @Override
    protected boolean isUIThread() {
        return SwingUtilities.isEventDispatchThread()
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ExecutorServiceManager)
                .to(DefaultExecutorServiceManager)
                .in(Singleton)

            bind(ExecutorService)
                .annotatedWith(named('defaultExecutorService'))
                .toProvider(DefaultExecutorServiceProvider)
                .in(Singleton)

            bind(UIThreadManager)
                .to(SwingUIThreadManager)
                .in(Singleton)
        }
    }
}
