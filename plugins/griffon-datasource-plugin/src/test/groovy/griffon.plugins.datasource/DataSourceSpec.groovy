/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.datasource

import griffon.core.CallableWithArgs
import griffon.core.GriffonApplication
import griffon.core.test.GriffonUnitRule
import griffon.plugins.datasource.exceptions.RuntimeSQLException
import groovy.sql.Sql
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.sql.DataSource
import java.sql.Connection

@Unroll
class DataSourceSpec extends Specification {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private DataSourceHandler dataSourceHandler

    @Inject
    private GriffonApplication application

    void 'Open and close default dataSource'() {
        given:
        List eventNames = [
            'DataSourceConnectStart', 'DataSourceConnectEnd',
            'DataSourceDisconnectStart', 'DataSourceDisconnectEnd'
        ]
        List events = []
        eventNames.each { name ->
            application.eventRouter.addEventListener(name, { Object... args ->
                events << [name: name, args: args]
            } as CallableWithArgs)
        }

        when:
        dataSourceHandler.withDataSource { String dataSourceName, DataSource dataSource ->
            true
        }
        dataSourceHandler.closeDataSource()

        then:
        events.size() == 4
        events.name == eventNames
    }

    void 'Connect to default dataSource'() {
        expect:
        dataSourceHandler.withDataSource { String dataSourceName, DataSource dataSource ->
            dataSourceName == 'default' && dataSource
        }
    }

    void 'Open a connection to default dataSource'() {
        expect:
        dataSourceHandler.withConnection { String dataSourceName, DataSource dataSource, Connection connection ->
            dataSourceName == 'default' && dataSource && connection
        }
    }

    void 'Can connect to #name dataSource'() {
        expect:
        dataSourceHandler.withDataSource(name) { String dataSourceName, DataSource dataSource ->
            dataSourceName == name && dataSource
        }

        where:
        name       | _
        'default'  | _
        'internal' | _
        'people'   | _
    }

    void 'Can open a connection to #name dataSource'() {
        expect:
        dataSourceHandler.withConnection(name) { String dataSourceName, DataSource dataSource, Connection connection ->
            dataSourceName == name && dataSource && connection
        }

        where:
        name       | _
        'default'  | _
        'internal' | _
        'people'   | _
    }

    void 'Bogus dataSource name (#name) results in error'() {
        when:
        dataSourceHandler.withDataSource(name) { String dataSourceName, DataSource dataSource ->
            true
        }

        then:
        thrown(IllegalArgumentException)

        where:
        name    | _
        null    | _
        ''      | _
        'bogus' | _
    }

    void 'Execute statements on people dataSource'() {
        when:
        List peopleIn = dataSourceHandler.withDataSource('people') { String dataSourceName, DataSource dataSource ->
            Sql sql = new Sql(dataSource)
            def people = sql.dataSet('people')
            [[id: 1, name: 'Danno', lastname: 'Ferrin'],
                [id: 2, name: 'Andres', lastname: 'Almiray'],
                [id: 3, name: 'James', lastname: 'Williams'],
                [id: 4, name: 'Guillaume', lastname: 'Laforge'],
                [id: 5, name: 'Jim', lastname: 'Shingler'],
                [id: 6, name: 'Alexander', lastname: 'Klein'],
                [id: 7, name: 'Rene', lastname: 'Groeschke']].each { data ->
                people.add(data)
            }
        }

        List peopleOut = dataSourceHandler.withDataSource('people') { String dataSourceName, DataSource dataSource ->
            Sql sql = new Sql(dataSource)
            List list = []
            sql.eachRow('SELECT * FROM people') {
                list << [id: it.id,
                    name: it.name,
                    lastname: it.lastname]
            }
            list
        }

        then:
        peopleIn == peopleOut
    }

    void 'A runtime SQLException is thrown within dataSource handling'() {
        when:
        dataSourceHandler.withDataSource { String dataSourceName, DataSource dataSource ->
            Sql sql = new Sql(dataSource)
            sql.dataSet('people').add(id: 0)
        }

        then:
        thrown(RuntimeSQLException)
    }

    void 'A runtime SQLException is thrown within connection handling'() {
        when:
        dataSourceHandler.withConnection { String dataSourceName, DataSource dataSource, Connection connection ->
            Sql sql = new Sql(connection)
            sql.dataSet('people').add(id: 0)
        }

        then:
        thrown(RuntimeSQLException)
    }
}
