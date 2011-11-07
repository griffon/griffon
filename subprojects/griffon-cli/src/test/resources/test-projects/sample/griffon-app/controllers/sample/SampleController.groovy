package sample

class SampleController {
    def model
    def view

    def actionClosure = { evt = null ->
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }

    private notAnActionClosure = { evt = null ->
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }

    void actionMethod() {
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }

    private void notAnActionMethod() {
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }

    def onEventHandlerClosure = {
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }

    void onEventHandlerMethod() {
        log.info 'simple statement'
        log.info "complex statement ${this.class.name}"
    }
}
