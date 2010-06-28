@artifact.package@class @artifact.name@ {
    // these will be injected by Griffon
    def model
    def view

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    /*
    def action = { evt = null ->
    }
    */
}