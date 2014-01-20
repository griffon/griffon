Basic Griffon Swing/Groovy project
----------------------------------

You have just created a basic Griffon application with Swing as UI toolkit
and Groovy as main language. The project has the following file structure

    .
    ├── build.gradle
    ├── pom.xml
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
        └── main
            ├── groovy
            └── resources

Simply add your source files to `src/main/groovy`, your test cases to
`src/test/groovy` and then you will be able to build your project with

    gradle build
    gradle test
    gradle run

Or if you prefer Maven you may issue the following commands

    mvn compile
    mvn test
    mvn exec:java

Don't forget to add any extra JAR dependencies to `build.gradle` or `pom.xml`!
