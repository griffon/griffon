package org.codehaus.griffon.runtime.util;

import com.google.inject.*;

import javax.inject.Inject;
import java.util.Map;

import static com.google.inject.util.Providers.guicify;

public class Main {
    public static void main(String[] args) {
        final MyApplication application = new MyApplication();
        final MyXInjector xinjector = new MyXInjector();
        Module appModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Application.class).toInstance(application);
                bind(XInjector.class).toInstance(xinjector);
            }
        };

        Injector injector = Guice.createInjector(appModule, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Service.class).to(MyService.class).in(javax.inject.Singleton.class);
                bind(MyController.class).toProvider(guicify(new ArtifactProvider<>(MyController.class)));
            }
        });
        xinjector.setInjector(injector);

        for (Map.Entry<Key<?>, Binding<?>> entry : injector.getBindings().entrySet()) {
            System.out.println(entry);
        }

        Controller c1 = injector.getInstance(MyController.class);
        Controller c2 = injector.getInstance(MyController.class);
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c1.getService());
        System.out.println(c2.getService());
        System.out.println(c1.getService().getApplication());
        System.out.println(c2.getService().getApplication());
    }

    public static interface Application {
    }

    public static class MyApplication implements Application {
    }

    public static interface Service {
        Application getApplication();
    }

    public static class MyService implements Service {
        private final Application application;

        @Inject
        public MyService(Application application) {
            this.application = application;
        }

        public Application getApplication() {
            return application;
        }
    }

    public static interface Controller {
        Service getService();
    }

    public static class MyController implements Controller {
        private Service service;

        @Inject
        public void setService(Service service) {
            this.service = service;
        }

        public Service getService() {
            return service;
        }
    }

    public static class ControllerProvider implements javax.inject.Provider<Controller> {
        @Inject
        private XInjector injector;

        @Override
        public Controller get() {
            Controller c = new MyController();
            injector.injectMembers(c);
            return c;
        }
    }

    public static class ArtifactProvider<T> implements javax.inject.Provider<T> {
        private final Class<T> klass;

        @Inject
        private XInjector injector;

        public ArtifactProvider(Class<T> klass) {
            this.klass = klass;
        }

        @Override
        public T get() {
            try {
                T t = klass.newInstance();
                injector.injectMembers(t);
                return t;
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }
    }

    public static interface XInjector {
        void injectMembers(Object instance);
    }

    public static class MyXInjector implements XInjector {
        private Injector injector;

        public Injector getInjector() {
            return injector;
        }

        public void setInjector(Injector injector) {
            this.injector = injector;
        }

        @Override
        public void injectMembers(Object instance) {
            injector.injectMembers(instance);
        }
    }
}
