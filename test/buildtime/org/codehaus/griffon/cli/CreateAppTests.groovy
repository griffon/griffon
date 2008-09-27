package org.codehaus.griffon.cli;

class CreateAppTests extends AbstractCliTests {


	void testCreateApp() {

	    gantRun(['-f', 'scripts/CreateApp.groovy'] as String[])

	    // test basic structure
		assert new File("${appBase}/testapp").exists()
        assert new File("${appBase}/testapp/griffon-app/conf").exists()
		assert new File("${appBase}/testapp/griffon-app/conf/keys").exists()
		assert new File("${appBase}/testapp/griffon-app/conf/webstart").exists()

        assert new File("${appBase}/testapp/griffon-app/controllers").exists()
        assert new File("${appBase}/testapp/griffon-app/i18n").exists()
        assert new File("${appBase}/testapp/griffon-app/lifecycle").exists()
        assert new File("${appBase}/testapp/griffon-app/models").exists()
        assert new File("${appBase}/testapp/griffon-app/resources").exists()
        assert new File("${appBase}/testapp/griffon-app/views").exists()

        assert new File("${appBase}/testapp/lib").exists()
        assert new File("${appBase}/testapp/scripts").exists()
		assert new File("${appBase}/testapp/src").exists()
		assert new File("${appBase}/testapp/src/main").exists()

        assert new File("${appBase}/testapp/test").exists()
        assert new File("${appBase}/testapp/test/integration").exists()

		// test critical files
		assert new File("${appBase}/testapp/griffon-app/conf/Application.groovy").exists()
		assert new File("${appBase}/testapp/griffon-app/conf/Builder.groovy").exists()
		assert new File("${appBase}/testapp/griffon-app/conf/Config.groovy").exists()
		assert new File("${appBase}/testapp/griffon-app/i18n/messages.properties").exists()
		assert new File("${appBase}/testapp/griffon-app/models/TestappModel.groovy").exists()
		assert new File("${appBase}/testapp/griffon-app/views/TestappView.groovy").exists()
		assert new File("${appBase}/testapp/griffon-app/controllers/TestappController.groovy").exists()

	}
}
