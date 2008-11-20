package griffon.util

/**
 * Created by IntelliJ IDEA.
 * User: Danno.Ferrin
 * Date: Aug 31, 2008
 * Time: 6:47:22 PM
 * To change this template use File | Settings | File Templates.
 */

class GriffonPlatformHelper {

    static void tweakForNativePlatform(IGriffonApplication app) {
        if (GriffonApplicationUtils.isMacOSX) {
            tweakForMacOSX(app)
        }
    }

    static macOSXHandler = null

    static void tweakForMacOSX(IGriffonApplication app) {
        // look and feel
        // don't do, let user decide
        //UIManager.setLookAndFeel('apple.laf.AquaLookAndFeel')

        // use unified menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true")

        // set menu bar title
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", app.getConfig().application?.title ?: 'Griffon')
        // we may want to have a more specific option like application.shortTitle, that is used first

        // exit handler
        if (!macOSXHandler) {
            try {
                Binding bindings = new Binding()
                bindings.app = app

                GroovyShell shell = new GroovyShell(GriffonPlatformHelper.getClass().getClassLoader(), bindings)
                macOSXHandler = shell.evaluate("""
                    package griffon.util

                    import com.apple.mrj.*

                    class GriffonMacOsSupport implements MRJQuitHandler, MRJAboutHandler {
                        def app
                    
                        public GriffonMacOsSupport(def app) {
                            this.app = app
                        }
                    
                        public void handleAbout() {
                            return // FIXME we can do better
                        }

                        public void handleQuit() {
                            app.shutdown()
                        }

                    }

                    def handler = new GriffonMacOsSupport(app)
                    MRJApplicationUtils.registerAboutHandler(handler)
                    MRJApplicationUtils.registerQuitHandler(handler)

                    return handler
                """)
            } catch (Throwable t) {
                t.printStackTrace(System.out)
            }
        }
    }
}
