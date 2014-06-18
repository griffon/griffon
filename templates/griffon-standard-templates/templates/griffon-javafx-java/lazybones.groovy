import uk.co.cacoethes.util.NameType

Map props = [:]
File projectDir = targetDir instanceof File ? targetDir : new File(String.valueOf(targetDir))
props.project_name = transformText(projectDir.name, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)
props.project_property_name = transformText(props.project_name, from: NameType.HYPHENATED, to: NameType.PROPERTY)
props.project_capitalized_name = props.project_property_name.capitalize()
props.project_group = ask("Define value for 'group' [org.example]: ", "org.example", "group")
props.project_version = ask("Define value for 'version' [0.1.0-SNAPSHOT]: ", "0.1.0-SNAPSHOT", "version")
props.project_package = ask("Define value for 'package' ["+ props.project_group +"]: ", props.project_group, "package")
props.griffon_version = ask("Define value for 'griffonVersion' [2.0.0.BETA]: ", "2.0.0.BETA", "griffonVersion")
String packagePath = props.project_package.replace('.' as char, '/' as char)

processTemplates 'pom.xml', props
processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props
processTemplates 'griffon-app/resources/*.fxml', props
processTemplates 'src/main/java/*.java', props
processTemplates 'src/test/java/*.java', props
processTemplates 'griffon-app/*/*.java', props

File mainSources = new File(projectDir, 'src/main/java')
File testSources = new File(projectDir, 'src/test/java')

File mainSourcesPath = new File(mainSources, packagePath)
mainSourcesPath.mkdirs()
File testSourcesPath = new File(testSources, packagePath)
testSourcesPath.mkdirs()

mainSources.eachFile { File file ->
   file.renameTo(mainSourcesPath.absolutePath + '/' + file.name)
}

testSources.eachFile { File file ->
   file.renameTo(testSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
}

['controllers', 'models', 'services', 'views'].each { String category ->
    File artifactDir = new File(projectDir, "griffon-app/$category")
    artifactDir.eachFile { File file ->
       File artifactSourcesPath = new File(projectDir, "griffon-app/$category/$packagePath")
       artifactSourcesPath.mkdirs()
       file.renameTo(artifactSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
    }
}

File fxml = new File(projectDir, "griffon-app/resources/app.fxml")
File fxmlPath = new File(projectDir, "griffon-app/resources/$packagePath")
fxmlPath.mkdirs()
fxml.renameTo(fxmlPath.absolutePath + '/' + props.project_property_name + '.fxml')
