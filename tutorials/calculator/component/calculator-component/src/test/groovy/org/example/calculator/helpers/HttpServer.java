/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.calculator.helpers;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
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
import java.util.List;

public class HttpServer implements Closeable {
    private static Injector injector;
    private final Server server;

    public static HttpServer of(Class<?> resource, List<Module> modules) {
        return of(new ResourceConfig() {{
            register(resource);
        }}, modules);
    }

    public static HttpServer of(ResourceConfig resource, List<Module> modules) {
        modules.add(new ServletModule());
        ServiceLocator locator = BootstrapUtils.newServiceLocator();
        injector = BootstrapUtils.newInjector(locator, modules);
        BootstrapUtils.install(locator);

        Server server = new Server(8080);
        ResourceConfig config = ResourceConfig.forApplication(resource);
        ServletContainer servletContainer = new ServletContainer(config);
        ServletHolder sh = new ServletHolder(servletContainer);
        ServletContextHandler context = new ServletContextHandler(
            ServletContextHandler.SESSIONS);
        context.setContextPath("/openmdm");
        FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
        context.addFilter(filterHolder, "/*",
            EnumSet.allOf(DispatcherType.class));
        context.addServlet(sh, "/*");
        server.setHandler(context);

        return new HttpServer(server);
    }

    public static Injector getInjector() {
        return injector;
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
        } finally {
            BootstrapUtils.reset();
        }
    }

    public void start() throws IOException {
        try {
            server.start();
        } catch (Exception err) {
            throw new IOException(err);
        }
    }
}