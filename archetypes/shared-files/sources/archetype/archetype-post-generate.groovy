if (binding.hasVariable('gradle') && (binding.getVariable('gradle') ?: false).toBoolean()) {
    new File(artifactId + File.separator + '.mvn').deleteDir()
    new File(artifactId + File.separator + 'pom.xml').delete()
    new File(artifactId + File.separator + 'mvnw').delete()
    new File(artifactId + File.separator + 'mvnw.cmd').delete()
    new File(artifactId + File.separator + 'gradlew').executable = true
} else {
    new File(artifactId + File.separator + 'gradle').deleteDir()
    new File(artifactId + File.separator + 'build.gradle').delete()
    new File(artifactId + File.separator + 'settings.gradle').delete()
    new File(artifactId + File.separator + 'gradle.properties').delete()
    new File(artifactId + File.separator + 'gradlew').delete()
    new File(artifactId + File.separator + 'gradlew.bat').delete()
    new File(artifactId + File.separator + 'mvnw').executable = true
}

String classNameUpper = className.capitalize()
String classNameLower = className.size() > 1?  className.substring(0, 1).toLowerCase(Locale.ENGLISH) + className.substring(1) : className.toLowerCase(Locale.ENGLISH)
String packagePath = binding.variables['package'].replace('.', File.separator)

['java', 'groovy'].each { lang ->
    def files = ['src/main/LANG/PACK/_APPMVCGroup.LANG',
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
        File src = new File(artifactId + File.separator + original)
        File dest = new File(artifactId + File.separator + target)

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