@artifact.package@class @artifact.name@ {
    @Bindable String status

    void mvcGroupInit(Map args) {
        status = "Welcome to ${GriffonNameUtils.capitalize(app.getMessage('application.title'))}"
    }
}
