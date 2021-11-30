if (binding.hasVariable('gradle') && (binding.getVariable('gradle') ?: false).toBoolean()) {
    new File(request.outputDirectory, request.artifactId + '/.mvn').deleteDir()
    new File(request.outputDirectory, request.artifactId + '/pom.xml').delete()
    new File(request.outputDirectory, request.artifactId + '/mvnw').delete()
    new File(request.outputDirectory, request.artifactId + '/mvnw.cmd').delete()
    new File(request.outputDirectory, request.artifactId + '/gradlew').executable = true
} else {
    new File(request.outputDirectory, request.artifactId + '/gradle').deleteDir()
    new File(request.outputDirectory, request.artifactId + '/build.gradle').delete()
    new File(request.outputDirectory, request.artifactId + '/settings.gradle').delete()
    new File(request.outputDirectory, request.artifactId + '/gradle.properties').delete()
    new File(request.outputDirectory, request.artifactId + '/gradlew').delete()
    new File(request.outputDirectory, request.artifactId + '/gradlew.bat').delete()
    new File(request.outputDirectory, request.artifactId + '/mvnw').executable = true
}

String classNameUpper = className.capitalize()
String classNameLower = className.size() > 1?  className.substring(0, 1).toLowerCase(Locale.ENGLISH) + className.substring(1) : className.toLowerCase(Locale.ENGLISH)
String packagePath = binding.variables['package'].replace('.', '/')

['java', 'groovy'].each { lang ->
    ['src/main/LANG/PACK/_APPMVCGroup.LANG',
     'griffon-app/conf/Config.LANG',
     'griffon-app/controllers/PACK/_APPController.LANG',
     'griffon-app/models/PACK/_APPModel.LANG',
     'griffon-app/mvcs/PACK/_APPMVCGroup.LANG',
     'griffon-app/views/PACK/_APPView.LANG',
     'griffon-app/resources/PACK/_app.fxml',
     'src/test/LANG/PACK/_APPControllerTest.LANG'].each { filename ->
        String original = filename.replace('PACK', packagePath)
            .replace('LANG', lang)
        String target = filename.replace('PACK', packagePath)
            .replace('LANG', lang)
            .replace('_APP', classNameUpper)
            .replace('_app', classNameLower)
        File src = new File(request.outputDirectory, request.artifactId + File.separator + original)
        File dest = new File(request.outputDirectory, request.artifactId + File.separator + target)

        if (src.exists()) {
            //rename
            src.renameTo(dest)

            // update refs
            String text = dest.text.replace('_APP', classNameUpper)
                .replace('_app', classNameLower)

            // update text
            dest.text = text
        }
    }
}