Basic Griffon JavaFX/Kotlin project
---------------------------------

You have just created a basic Griffon application with JavaFX as UI toolkit
and Kotlin as main language. The project has the following file structure

    .
    ├── build.gradle
    ├── griffon-app
    │   ├── conf
    │   ├── controllers
    │   ├── i18n
    │   ├── lifecycle
    │   ├── models
    │   ├── resources
    │   ├── services
    │   └── views
    └── src
        ├── functional-test
        │   └── kotlin
        ├── integration-test
        │   └── kotlin
        ├── main
        │   ├── kotlin
        │   └── resources
        └── test
            ├── kotlin
            └── resources

Simply add your source files to `src/main/kotlin`, your test cases to
`src/test/kotlin` and then you will be able to build your project with

    gradle build
    gradle test
    gradle run

Don't forget to add any extra JAR dependencies to `build.gradle`!

