package org.example.http;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.squarespace.jersey2.guice.BootstrapUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.io.Closeable;
import java.io.IOException;
import java.util.EnumSet;

public class HttpServer implements Closeable {
    private final Server server;

    public static HttpServer of(Injector injector) {
        ServiceLocator locator = BootstrapUtils.newServiceLocator();
        BootstrapUtils.link(locator, injector);
        BootstrapUtils.install(locator);

        Server server = new Server(8080);
        ResourceConfig config = ResourceConfig.forApplication(new CalculatorApplication());
        ServletContainer servletContainer = new ServletContainer(config);
        ServletHolder sh = new ServletHolder(servletContainer);
        ServletContextHandler context = new ServletContextHandler(
            ServletContextHandler.SESSIONS);
        context.setContextPath("/griffon");
        FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
        context.addFilter(filterHolder, "/*",
            EnumSet.allOf(DispatcherType.class));
        context.addServlet(sh, "/*");
        server.setHandler(context);

        return new HttpServer(server);
    }

    private HttpServer(Server server) {
        this.server = server;
    }

    @Override
    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }
}