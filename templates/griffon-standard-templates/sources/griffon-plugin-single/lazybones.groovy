import uk.co.cacoethes.util.NameType

Map props = [:]
File projectDir = targetDir instanceof File ? targetDir : new File(String.valueOf(targetDir))
props.project_name = transformText(projectDir.name, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)
props.project_property_name = transformText(props.project_name, from: NameType.HYPHENATED, to: NameType.PROPERTY)
props.project_capitalized_name = props.project_property_name.capitalize()
props.project_group = ask("Define value for 'group' [org.example]: ", "org.example", "group")
props.project_version = ask("Define value for 'version' [0.1.0-SNAPSHOT]: ", "0.1.0-SNAPSHOT", "version")
props.project_package = ask("Define value for 'package' ["+ props.project_group +"]: ", props.project_group, "package")
props.griffon_version = ask("Define value for 'griffonVersion' [@griffon.version@]: ", "@griffon.version@", "griffonVersion")

processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props
processTemplates '*.xml', props
