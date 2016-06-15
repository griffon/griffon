import griffon.util.AbstractMapResourceBundle

class Config : AbstractMapResourceBundle() {
    override fun initialize(entries: MutableMap<String, Any>) {
        entries.put("application", hashMapOf(
                "title" to "${project_name}",
                "startupGroups" to listOf("${project_property_name}"),
                "autoshutdown" to true
        ))
        entries.put("mvcGroups", hashMapOf(
                "${project_property_name}" to hashMapOf(
                        "model" to "${project_package}.${project_class_name}Model",
                        "view" to "${project_package}.${project_class_name}View",
                        "controller" to "${project_package}.${project_class_name}Controller"
                )
        ))
    }
}