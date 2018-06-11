Basic Griffon Swing/Groovy project
----------------------------------

You have just created a basic Griffon application with Swing as UI toolkit
and Groovy as main language. The project has the following file structure

    .
    ├── griffon-app
    │   ├── conf
    │   ├── controllers
    │   ├── i18n
    │   ├── lifecycle
    │   ├── models
    │   ├── resources
    │   ├── services
    │   └── views
    ├── pom.xml
    └── src
        ├── main
        │   ├── groovy
        │   └── resources
        └── test
            ├── groovy
            └── resources

Simply add your source files to `src/main/groovy`, your test cases to
`src/test/groovy` and then you will be able to build your project with

    mvn compile
    mvn test
    mvn -Prun

Don't forget to add any extra JAR dependencies to `pom.xml`!
