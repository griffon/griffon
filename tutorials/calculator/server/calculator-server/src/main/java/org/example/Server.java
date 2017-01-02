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
package org.example;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.SessionScoped;
import org.example.calculator.Calculator;
import org.example.calculator.CalculatorImpl;
import org.example.http.HttpServer;
import org.example.pm.ServerDolphinProvider;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.install();

        Injector injector = Guice.createInjector(new ServletModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Calculator.class)
                    .to(CalculatorImpl.class)
                    .in(Singleton.class);

                bind(Codec.class)
                    .to(JsonCodec.class)
                    .in(Singleton.class);

                bind(ServerModelStore.class)
                    .in(SessionScoped.class);

                bind(ServerConnector.class)
                    .in(SessionScoped.class);

                bind(ServerDolphin.class)
                    .toProvider(ServerDolphinProvider.class)
                    .in(SessionScoped.class);
            }
        });

        final List<Channel> channels = new ArrayList<>();
        channels.add(setupHttpChannel(injector));

        final ExecutorService executorService = Executors.newFixedThreadPool(channels.size());

        for (final Channel channel : channels) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        channel.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                executorService.shutdownNow();
            }
        });
    }

    private static Channel setupHttpChannel(Injector injector) {
        return new HttpChannel(HttpServer.of(injector));
    }

    private static interface Channel {
        void start() throws Exception;

        void close() throws Exception;
    }

    private static class HttpChannel implements Channel {
        private final HttpServer server;

        private HttpChannel(HttpServer server) {
            this.server = server;
        }

        @Override
        public void start() throws Exception {
            server.start();
        }

        @Override
        public void close() throws Exception {
            server.close();
        }
    }
}
