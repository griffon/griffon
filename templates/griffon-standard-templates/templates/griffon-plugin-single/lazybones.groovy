import uk.co.cacoethes.util.NameType

Map props = [:]
File projectDir = targetDir instanceof File ? targetDir : new File(String.valueOf(targetDir))
String projectName = projectDir.name - 'griffon-' - '-plugin'
if (projectName =~ /\-/) {
    props.project_class_name = transformText(projectName, from: NameType.HYPHENATED, to: NameType.CAMEL_CASE)
} else {
    props.project_class_name = transformText(projectName, from: NameType.PROPERTY, to: NameType.CAMEL_CASE)
}
props.project_property_name = transformText(props.project_class_name, from: NameType.CAMEL_CASE, to: NameType.PROPERTY)
props.project_hyphenated_name = transformText(props.project_class_name, from: NameType.PROPERTY, to: NameType.HYPHENATED)
props.project_name = transformText(props.project_class_name, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)
props.project_capitalized_name = props.project_property_name.capitalize()
props.project_group = ask("Define value for 'group' [org.example]: ", "org.example", "group")
props.project_version = ask("Define value for 'version' [0.1.0.SNAPSHOT]: ", "0.1.0.SNAPSHOT", "version")
props.project_package = ask("Define value for 'package' [" + props.project_group + "]: ", props.project_group, "package")
props.griffon_version = ask("Define value for 'griffonVersion' [2.0.0.BETA2]: ", "2.0.0.BETA2", "griffonVersion")
props.project_year = Calendar.instance.get(Calendar.YEAR)

String packagePath = props.project_package.replace('.' as char, '/' as char)

processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props
processTemplates 'src/main/java/*.java', props

File mainSources = new File(projectDir, 'src/main/java')

File mainSourcesPath = new File(mainSources, packagePath)
mainSourcesPath.mkdirs()

mainSources.eachFile { File file ->
    file.renameTo(mainSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
}