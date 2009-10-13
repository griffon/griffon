package org.codehaus.griffon.test

import junit.framework.TestSuite
import org.codehaus.griffon.util.BuildSettings
import griffon.util.IGriffonApplication

class GriffonIntegrationTestHelper extends DefaultGriffonTestHelper {
    IGriffonApplication app

    GriffonIntegrationTestHelper(
            BuildSettings settings,
            ClassLoader parentLoader,
            Closure resourceResolver,
            IGriffonApplication app) {
        super(settings, parentLoader, resourceResolver)
        this.app = app
    }

    TestSuite createTestSuite() {
        new GriffonTestSuite(this.app, this.testSuffix)
    }

    TestSuite createTestSuite(Class clazz) {
        new GriffonTestSuite(this.app, clazz, this.testSuffix)
    }
}
