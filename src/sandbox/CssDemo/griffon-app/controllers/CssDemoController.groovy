class CssDemoController {

    // these will be injected by Griffon
    def model
    def view

    def doNothing = {
        buildMVCGroup('NothingPanel')
        doOutside {
            nothingMethod()
            println nothingProp
            edt { println nothingWidget() }
        }
    }

}