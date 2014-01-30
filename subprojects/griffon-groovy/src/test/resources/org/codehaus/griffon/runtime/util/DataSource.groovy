package org.codehaus.griffon.runtime.util

dataSource {
    driverClassName = 'org.h2.Driver'
    username = 'sa'
    password = ''
    pool {
        maxWait = 60000
        maxIdle = 5
        maxActive = 8
    }
}
environments {
    development {
        dataSource {
            dbCreate = 'create'
            url = 'jdbc:h2:mem:sample-dev'
        }
    }
    test {
        dataSource {
            dbCreate = 'create'
            url = 'jdbc:h2:mem:sample-test'
        }
    }
    production {
        dataSource {
            dbCreate = 'skip'
            url = 'jdbc:h2:mem:sample-prod'
        }
    }
}
