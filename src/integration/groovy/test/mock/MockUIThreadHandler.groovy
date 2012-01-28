package griffon.test.mock

import griffon.util.UIThreadHandler

class MockUIThreadHandler implements UIThreadHandler {
    boolean isUIThread() {
        false
    }

    void executeAsync(Runnable runnable) {
        runnable.run()
    }

    void executeSync(Runnable runnable) {
        runnable.run()
    }

    void executeOutside(Runnable runnable) {
        runnable.run()
    }
}
