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
package griffon.plugins.domain.datastore

import griffon.core.CallableWithArgs
import griffon.core.GriffonApplication
import griffon.core.test.GriffonUnitRule
import griffon.inject.BindTo
import org.codehaus.griffon.runtime.domain.datastore.DefaultDatastore
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.inject.Named

@Unroll
class DatastoreSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private DatastoreHandler datastoreHandler

    @Inject
    private GriffonApplication application

    void 'Open and close default datastore'() {
        given:
        List eventNames = [
            'DatastoreConnectStart',
            'DatastoreConnectEnd'
        ]
        List events = []
        eventNames.each { name ->
            application.eventRouter.addEventListener(name, { Object... args ->
                events << [name: name, args: args]
            } as CallableWithArgs)
        }

        when:
        datastoreHandler.withDatastore { String datastoreName, Datastore datastore ->
            true
        }

        then:
        events.size() == 2
        events.name == eventNames
    }

    void 'Connect to default datastore'() {
        expect:
        datastoreHandler.withDatastore { String datastoreName, Datastore datastore ->
            datastoreName == 'default' && datastore
        }
    }


    void 'Can connect to #name datastore'() {
        expect:
        datastoreHandler.withDatastore(name) { String datastoreName, Datastore datastore ->
            datastoreName == name && datastore
        }

        where:
        name       | _
        'default'  | _
        'internal' | _
    }

    void 'Bogus datastore name (#name) results in error'() {
        when:
        datastoreHandler.withDatastore(name) { String datastoreName, Datastore datastore ->
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

    /*
    void 'Execute statements on people datastore'() {
        when:
        List peopleIn = datastoreHandler.withDatastore('people') { String datastoreName, Datastore datastore ->
            Sql sql = new Sql(datastore)
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

        List peopleOut = datastoreHandler.withDatastore('people') { String datastoreName, Datastore datastore ->
            Sql sql = new Sql(datastore)
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
    */

    @javax.inject.Singleton
    @BindTo(Datastore)
    @Named('internal')
    private DefaultDatastore internal
}
