package griffon.plugins.datasource

import griffon.core.CallableWithArgs
import griffon.core.GriffonApplication
import griffon.core.test.GriffonUnitRule
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

        where:
        name       | _
        'default'  | _
        'internal' | _
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
    }

    void 'Bogus dataSource name results in error'() {
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
}
