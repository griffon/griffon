@artifact.package@defaultTarget("The description of the script goes here!") {
    depends(@artifact.key@)
}

includeTargets << griffonScript("Init")

target('@artifact.key@': "The description of the script goes here!") {
	// TODO: Implement script here
}
