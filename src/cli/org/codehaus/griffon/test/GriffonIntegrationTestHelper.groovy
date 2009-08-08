package org.codehaus.groovy.griffon.test

import junit.framework.TestSuite
// import org.codehaus.groovy.griffon.commons.spring.GriffonWebApplicationContext
import org.codehaus.groovy.griffon.test.GriffonTestSuite
import org.codehaus.groovy.griffon.test.DefaultGriffonTestHelper
import org.springframework.context.ApplicationContext
import org.codehaus.griffon.util.BuildSettings

class GriffonIntegrationTestHelper extends DefaultGriffonTestHelper {
    ApplicationContext applicationContext

    GriffonIntegrationTestHelper(
            BuildSettings settings,
            ClassLoader parentLoader,
            Closure resourceResolver,
            ApplicationContext appContext) {
        super(settings, parentLoader, resourceResolver)
        this.applicationContext = appContext
    }

    TestSuite createTestSuite() {
        new GriffonTestSuite(this.applicationContext, this.testSuffix)
    }

    TestSuite createTestSuite(Class clazz) {
        new GriffonTestSuite(this.applicationContext, clazz, this.testSuffix)
    }
}
