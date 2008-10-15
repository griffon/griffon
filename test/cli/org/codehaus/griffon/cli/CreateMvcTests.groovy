package org.codehaus.griffon.cli

class CreateMvcTests extends AbstractCliTests {
    def appDir

    void testCreateController() {
        appDir = createTestApp()

        tryMvc('Book')
        tryMvc('org.example.Author')
        tryMvc('project-item', 'ProjectItem')
    }

	void testCreateControllerCreatesViewDirectory() {
        appDir = createTestApp()

		System.setProperty("griffon.cli.args", "Book")
		gantRun( ["-f", "scripts/CreateMvc.groovy"] as String[])

        def bookModelFile = "${appDir}/griffon-app/models/BookModel.groovy"
        assertTrue "${bookModelFile} does not exist", new File(bookModelFile).exists()
        def bookViewFile = "${appDir}/griffon-app/views/BookView.groovy"
        assertTrue "${bookViewFile} does not exist", new File(bookViewFile).exists()
        def bookControllerFile = "${appDir}/griffon-app/controllers/BookController.groovy"
        assertTrue "${bookControllerFile} does not exist", new File(bookControllerFile).exists()
	}

    void tryMvc(String className) {
        tryMvc(className, className)
    }

    void tryMvc(String scriptArg, String className) {
        // Run the create controller script with a single argument.
        System.setProperty("griffon.cli.args", scriptArg)
        gantRun( ["-f", "scripts/CreateMvc.groovy"] as String[])

        // Extract any package from the class name.
        def pkg = null
        def pos = className.lastIndexOf('.')
        if (pos != -1) {
            pkg = className[0..<pos]
            className = className[(pos + 1)..-1]
        }

        def pkgPath = ''
        if (pkg) {
            pkgPath = pkg.replace('.' as char, '/' as char) + '/'
        }

        // Check that the model has been created.
        def modelFile = new File("${appDir}/griffon-app/models/${pkgPath}${className}Model.groovy")
        assert modelFile.exists()
        assert modelFile.text =~ "^${pkg ? 'package ' + pkg : ''}\\s*import groovy.beans.Bindable\\s*class ${className}Model \\{"

        // Check that the view has been created.
        def viewFile = new File("${appDir}/griffon-app/views/${pkgPath}${className}View.groovy")
        assert viewFile.exists()
        assert viewFile.text =~ "^${pkg ? 'package ' + pkg : ''}\\s*application\\(title:'testapp'," //?? title always test app?

        // Check that the controller has been created.
        def controllerFile = new File("${appDir}/griffon-app/controllers/${pkgPath}${className}Controller.groovy")
        assert controllerFile.exists()
        assert controllerFile.text =~ "^${pkg ? 'package ' + pkg : ''}\\s*class ${className}Controller \\{"
    }
}
