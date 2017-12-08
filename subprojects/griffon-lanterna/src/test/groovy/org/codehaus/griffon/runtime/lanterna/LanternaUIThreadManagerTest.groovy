package org.codehaus.griffon.runtime.lanterna

import com.google.inject.AbstractModule
import com.googlecode.lanterna.gui.GUIScreen
import griffon.core.ExecutorServiceManager
import griffon.core.threading.UIThreadManager
import org.codehaus.griffon.runtime.core.DefaultExecutorServiceManager
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider
import org.jukito.JukitoRunner
import org.jukito.UseModules
import org.junit.Ignore
import org.junit.runner.RunWith

import javax.application.threading.ThreadingHandler
import javax.application.threading.ThreadingHandlerTest
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ExecutorService

import static griffon.util.AnnotationUtils.named

@RunWith(JukitoRunner)
@UseModules(TestModule)
@Ignore('The test Thread is also the UI thread')
class LanternaUIThreadManagerTest extends ThreadingHandlerTest {
    @Inject private UIThreadManager uiThreadManager
    @Inject private GUIScreen screen

    @Override
    protected ThreadingHandler resolveThreadingHandler() {
        return uiThreadManager
    }

    @Override
    protected boolean isUIThread() {
        return screen.isInEventThread()
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
                .to(LanternaUIThreadManager)
                .in(Singleton)

            bind(GUIScreen)
                .toProvider(GUIScreenProvider)
                .in(Singleton)
        }
    }
}
