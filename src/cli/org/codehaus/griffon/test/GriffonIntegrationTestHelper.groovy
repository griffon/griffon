package org.codehaus.griffon.test

import junit.framework.TestSuite
import org.codehaus.griffon.util.BuildSettings
import griffon.core.GriffonApplication

class GriffonIntegrationTestHelper extends DefaultGriffonTestHelper {
    GriffonApplication app

    GriffonIntegrationTestHelper(
            BuildSettings settings,
            ClassLoader parentLoader,
            Closure resourceResolver,
            GriffonApplication app) {
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
