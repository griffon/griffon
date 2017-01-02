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
package org.example.calculator

import com.google.inject.AbstractModule
import org.example.calculator.helpers.ClientConnectorProvider
import org.example.calculator.helpers.ClientModelStoreProvider
import org.example.calculator.helpers.HttpServer
import org.example.calculator.helpers.ServerDolphinProvider
import org.example.calculator.helpers.TestClientDolphin
import org.example.calculator.pm.CalculatorAction
import org.example.calculator.pm.CalculatorPMResource
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.RunLaterUiThreadHandler
import org.opendolphin.core.client.comm.UiThreadHandler
import org.opendolphin.core.comm.Codec
import org.opendolphin.core.comm.JsonCodec
import org.opendolphin.core.server.ServerConnector
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerModelStore
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import javax.inject.Singleton

import static org.example.calculator.CalculatorPM.COMMAND_DIV
import static org.example.calculator.CalculatorPM.COMMAND_MUL
import static org.example.calculator.CalculatorPM.COMMAND_SUB
import static org.example.calculator.CalculatorPM.COMMAND_SUM
import static org.example.calculator.CalculatorPM.ATTR_ERROR
import static org.example.calculator.CalculatorPM.ATTR_OP1
import static org.example.calculator.CalculatorPM.ATTR_OP2
import static org.example.calculator.CalculatorPM.ATTR_RESULT
import static org.example.calculator.CalculatorPM.PM_CALCULATION

@Stepwise
class CalculatorPMResourceSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    private static final HttpServer SERVER = HttpServer.of(CalculatorPMResource, [
        new AbstractModule() {
            protected void configure() {
                bind(Calculator).to(CalculatorImpl).in(Singleton)

                bind(ClientDolphin).in(Singleton)
                bind(UiThreadHandler).to(RunLaterUiThreadHandler).in(Singleton)
                bind(ClientConnector).toProvider(ClientConnectorProvider).in(Singleton)
                bind(ClientModelStore).toProvider(ClientModelStoreProvider).in(Singleton)
                bind(TestClientDolphin).in(Singleton)

                bind(Codec).to(JsonCodec).in(Singleton)
                bind(ServerModelStore).in(Singleton)
                bind(ServerConnector).in(Singleton)
                bind(ServerDolphin).toProvider(ServerDolphinProvider).in(Singleton)
                bind(CalculatorAction).in(Singleton)
            }
        }
    ])

    void setupSpec() {
        SERVER.start()
        ServerConnector serverConnector = SERVER.injector.getInstance(ServerConnector)
        serverConnector.register(SERVER.injector.getInstance(CalculatorAction))
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        clientDolphin.presentationModel(PM_CALCULATION, [
            (ATTR_OP1)   : 0L,
            (ATTR_OP2)   : 0L,
            (ATTR_RESULT): 0L,
            (ATTR_ERROR) : ''
        ])
        clientDolphin.clientModelStore.withPresentationModel(PM_CALCULATION) { pm ->
            // wait for it
        }
    }

    void cleanupSpec() {
        SERVER.close()
    }

    @Unroll("The sum of #op1 and #op2 is equal to #result")
    void sum_operation() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_SUM) {
            assert result == pm[ATTR_RESULT].value.toLong()
        }

        where:
        op1 << (0..10).collect { it * 10 }
        op2 << (10..0).collect { it * 10 }
        result << (0..10).collect { 100 }
    }

    @Unroll("The sub of #op1 and #op2 is equal to #result")
    void sub_operation() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_SUB) {
            assert result == pm[ATTR_RESULT].value.toLong()
        }

        where:
        op1 << (10..0).collect { it * 10 }
        op2 << (0..10).collect { it * 10 }
        result << (0..10).collect { 100 - (it * 20) }
    }

    @Unroll("The mul of #op1 and #op2 is equal to #result")
    void mul_operation() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_MUL) {
            assert result == pm[ATTR_RESULT].value.toLong()
        }

        where:
        op1 << (0..10).collect { it * 10 }
        op2 << (0..10).collect { it * 10 }
        result << (0..10).collect { it * it * 100 }
    }

    @Unroll("The div of #op1 and #op2 is equal to #result")
    void div_operation() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_DIV) {
            assert result == pm[ATTR_RESULT].value.toLong()
        }

        where:
        op1 << (1..10).collect { 100 }
        op2 << (1..10).collect { it * 10 }
        result << (1..10).collect { (100 / (it * 10)) as int }
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with sum")
    void sum_constraints() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_SUM) {
            assert !pm[ATTR_RESULT].value
            assert pm[ATTR_ERROR].value
        }

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with sub")
    void sub_constraints() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_SUB) {
            assert !pm[ATTR_RESULT].value
            assert pm[ATTR_ERROR].value
        }

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with mul")
    void mul_constraints() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_MUL) {
            assert !pm[ATTR_RESULT].value
            assert pm[ATTR_ERROR].value
        }

        where:
        op1 | op2
        -1  | 0
        0   | -1
        101 | 0
        0   | 101
    }

    @Unroll("Values op1 = #op1 and op2 = #op2 yield an error with div")
    void div_constraints() {
        given:
        TestClientDolphin clientDolphin = SERVER.injector.getInstance(TestClientDolphin)
        PresentationModel pm = clientDolphin.clientModelStore.findPresentationModelById(PM_CALCULATION)

        when:
        pm[ATTR_OP1].setValue(op1)
        pm[ATTR_OP2].setValue(op2)

        then:
        clientDolphin.send(COMMAND_DIV) {
            assert !pm[ATTR_RESULT].value
            assert pm[ATTR_ERROR].value
        }

        where:
        op1 | op2
        -1  | 0
        0   | 0
        101 | 1
        0   | 101
    }
}
