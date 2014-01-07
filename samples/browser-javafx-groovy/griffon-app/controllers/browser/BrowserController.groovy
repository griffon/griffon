package browser

import griffon.core.artifact.GriffonController
import griffon.transform.Threading
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class BrowserController {
    def model
    def builder

    void mvcGroupInit(Map<String, Object> args) {
        runInsideUIAsync {
            builder.urlField.text = 'http://griffon-framework.org'
            openUrl()
        }
    }

    @Threading(Threading.Policy.SKIP)
    void back() {
        if (builder.browser.engine.history.entries.size() > 0) {
            builder.browser.engine.history.go(-1)
            builder.urlField.text = getUrlFromHistory()
        }
    }

    @Threading(Threading.Policy.SKIP)
    void forward() {
        if (builder.browser.engine.history.entries.size() > 0) {
            builder.browser.engine.history.go(1)
            builder.urlField.text = getUrlFromHistory()
        }
    }

    @Threading(Threading.Policy.SKIP)
    void reload() {
        builder.browser.engine.reload()
    }

    @Threading(Threading.Policy.SKIP)
    def openUrl = {
        String url = model.url
        if (url.indexOf('://') < 0) url = 'http://' + url
        if (builder.browser.engine.location == url) return
        builder.browser.engine.load(url)
    }

    private String getUrlFromHistory() {
        builder.browser.engine.history.entries[builder.browser.engine.history.currentIndex].url
    }
}
