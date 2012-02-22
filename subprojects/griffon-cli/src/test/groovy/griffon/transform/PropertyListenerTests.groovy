package griffon.transform

class PropertyListenerTests extends GroovyTestCase {
    void testSimpleValues() {
        GroovyShell shell = new GroovyShell()
        shell.evaluate("""
            import griffon.transform.PropertyListener
            import groovy.beans.Bindable

            class EventHolder {
                static event
            }
            
            @Bindable
            class Producer {
                @PropertyListener({EventHolder.event = it})
                String value1
                @PropertyListener(foo)
                String value2

                def foo = { EventHolder.event = it }
            }

            assert !EventHolder.event
            Producer p = new Producer(value1: 'Griffon')
            assert EventHolder.event
            assert EventHolder.event.propertyName == 'value1'
            assert EventHolder.event.newValue == 'Griffon'

            EventHolder.event = null
            p.value2 = 'Groovy'
            assert EventHolder.event
            assert EventHolder.event.propertyName == 'value2'
            assert EventHolder.event.newValue == 'Groovy'
        """)
    }

    void testListValues() {
        GroovyShell shell = new GroovyShell()
        shell.evaluate("""
            import griffon.transform.PropertyListener
            import groovy.beans.Bindable

            class EventHolder2 {
                static List events = []
            }

            @Bindable
            class Producer2 {
                @PropertyListener([{EventHolder2.events << it},
                           {EventHolder2.events << it}])
                String value1
                @PropertyListener([foo, bar])
                String value2

                def foo = { EventHolder2.events << it }
                def bar = { EventHolder2.events << it }
            }

            assert !EventHolder2.events
            def p = new Producer2(value1: 'Griffon')
            assert EventHolder2.events
            assert EventHolder2.events[0].propertyName == 'value1'
            assert EventHolder2.events[0].newValue == 'Griffon'
            assert EventHolder2.events[1].propertyName == 'value1'
            assert EventHolder2.events[1].newValue == 'Griffon'

            EventHolder2.events = []
            p.value2 = 'Groovy'
            assert EventHolder2.events
            assert EventHolder2.events[0].propertyName == 'value2'
            assert EventHolder2.events[0].newValue == 'Groovy'
            assert EventHolder2.events[1].propertyName == 'value2'
            assert EventHolder2.events[1].newValue == 'Groovy'
        """)
    }

    void testAnnotatedClass() {
        GroovyShell shell = new GroovyShell()
        shell.evaluate("""
            import griffon.transform.PropertyListener
            import groovy.beans.Bindable

            class EventHolder3 {
                static event
            }

            @Bindable
            @PropertyListener({EventHolder3.event = it})
            class Producer3 {
                String value1
                String value2
            }

            @Bindable
            @PropertyListener(foo)
            class Producer4 {
                String value1
                String value2
                def foo = { EventHolder3.event = it }
            }

            assert !EventHolder3.event
            def p = new Producer3(value1: 'Griffon')
            assert EventHolder3.event
            assert EventHolder3.event.propertyName == 'value1'
            assert EventHolder3.event.newValue == 'Griffon'
            EventHolder3.event = null
            p.value2 = 'Groovy'
            assert EventHolder3.event
            assert EventHolder3.event.propertyName == 'value2'
            assert EventHolder3.event.newValue == 'Groovy'

            EventHolder3.event = null
            p = new Producer4(value1: 'Griffon')
            assert EventHolder3.event
            assert EventHolder3.event.propertyName == 'value1'
            assert EventHolder3.event.newValue == 'Griffon'
            EventHolder3.event = null
            p.value2 = 'Groovy'
            assert EventHolder3.event
            assert EventHolder3.event.propertyName == 'value2'
            assert EventHolder3.event.newValue == 'Groovy'
        """)
    }
}
