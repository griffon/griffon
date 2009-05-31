package griffon.util

import org.codehaus.groovy.control.CompilationFailedException

class EventPublisherTest extends GroovyTestCase {
    void testHappyPath() {
        GroovyShell shell = new GroovyShell()
        shell.evaluate("""
            import griffon.util.EventPublisher

            @EventPublisher
            class Publisher {
                void doit(String name) {
                   publishEvent("arg",[name])
                }

                void doit() {
                   publishEvent("empty")
                }
            }

            class Consumer {
               String value

               void onArg(String arg) { value = "arg = "+arg }
               void onEmpty() { value = "empty" }
            }

            p = new Publisher()
            c = new Consumer()
            p.addEventListener(c)
            assert !c.value
            p.doit()
            assert c.value == "empty"
            p.doit("Groovy")
            assert c.value == "arg = Groovy"
        """)
    }

    void testBadInheritance() {
        shouldFail(CompilationFailedException) {
            GroovyShell shell = new GroovyShell()
            shell.evaluate("""
                import griffon.util.EventPublisher

                @EventPublisher
                class EventPublisherTestBean2  {
                    void addEventListener(listener) {}
                }
                new EventPublisherTestBean2()
            """)
        }
    }
}