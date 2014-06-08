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

dataSources {
    internal {
        driverClassName = 'org.h2.Driver'
        username = 'sa'
        password = ''
        schema = false
        url = 'jdbc:h2:mem:@application.name@-internal'
    }
    people {
        driverClassName = 'org.h2.Driver'
        username = 'sa'
        password = ''
        dbCreate = 'create'
        url = 'jdbc:h2:mem:@application.name@-people'
    }
}

environments {
    development {
        dataSource {
            dbCreate = 'create' // one of ['create', 'skip']
            url = 'jdbc:h2:mem:@application.name@-dev'
        }
    }
    test {
        dataSource {
            dbCreate = 'create'
            url = 'jdbc:h2:mem:@application.name@-test'
        }
    }
    production {
        dataSource {
            dbCreate = 'skip'
            url = 'jdbc:h2:mem:@application.name@-prod'
        }
    }
}
