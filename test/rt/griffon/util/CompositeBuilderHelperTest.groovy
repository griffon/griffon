package griffon.util
import griffon.builder.UberBuilder

class CompositeBuilderHelperTest extends GroovyTestCase {
    void testSanityCheckOnSampleBuilder() {
        SampleBuilder builder = new SampleBuilder()
        Set groups = builder.getRegistrationGroups()
        Set collections = builder.getRegistrationGroupItems('Collections')
        assert collections.size() == 2 
        assert [] == (['list', 'map'] - collections)
        Set support = builder.getRegistrationGroupItems('Support')
        assert support.size() == 1
        assert [] == (['foo'] - support)
    }

    void testExplicitNodeName() {
        Bean1 bean1 = new Bean1()
        MetaClass mc = bean1.metaClass
        assert !mc.respondsTo(bean1, 'foo')
        
        ConfigObject builderConfig = new ConfigSlurper().parse('''
            root {
                'griffon.util.SampleBuilder' {
                    bean1 = ['foo']
                }
            }
        ''')

        Map targets = [bean1: bean1]
        invokeCompositeBuilderHelper(targets, builderConfig)

        mc = bean1.metaClass
        assert mc.respondsTo(bean1, 'foo')
        assert !mc.respondsTo(bean1, 'list')
        assert !mc.respondsTo(bean1, 'map')
        assert 'foo' == bean1.foo()
    }

    void testGroupName() {
        Bean2 bean2 = new Bean2()
        MetaClass mc = bean2.metaClass
        assert !mc.respondsTo(bean2, 'map')
        assert !mc.respondsTo(bean2, 'list')
        
        ConfigObject builderConfig = new ConfigSlurper().parse('''
            root {
                'griffon.util.SampleBuilder' {
                    bean2 = ['Collections']
                }
            }
        ''')

        Map targets = [bean2: bean2]
        invokeCompositeBuilderHelper(targets, builderConfig)

        mc = bean2.metaClass
        assert !mc.respondsTo(bean2, 'foo')
        assert mc.respondsTo(bean2, 'list')
        assert mc.respondsTo(bean2, 'map')
        assert [one: 1, two: 2] == bean2.map(one: 1, two:2)
        assert ['list', 'value'] == bean2.list('value')
    }

    void testStar() {
        Bean3 bean3 = new Bean3()
        MetaClass mc = bean3.metaClass
        assert !mc.respondsTo(bean3, 'foo')
        assert !mc.respondsTo(bean3, 'map')
        assert !mc.respondsTo(bean3, 'list')
        
        ConfigObject builderConfig = new ConfigSlurper().parse('''
            root {
                'griffon.util.SampleBuilder' {
                    bean3 = '*'
                }
            }
        ''')

        Map targets = [bean3: bean3]
        invokeCompositeBuilderHelper(targets, builderConfig)

        mc = bean3.metaClass
        assert mc.respondsTo(bean3, 'foo')
        assert mc.respondsTo(bean3, 'list')
        assert mc.respondsTo(bean3, 'map')
        assert 'foo' == bean3.foo()
        assert [one: 1, two: 2] == bean3.map(one: 1, two:2)
        assert ['list', 'value'] == bean3.list('value')
    }

    private invokeCompositeBuilderHelper(Map targets, builderConfig) {
        UberBuilder uberBuilder = new UberBuilder()

        for(node in builderConfig) {
            String nodeName = node.key
            if(nodeName == "root") nodeName = ""
            node.value.each { builder ->
                CompositeBuilderHelper.handleLocalBuilder(uberBuilder, targets, nodeName, builder)
            }
        }
    }
}

class SampleBuilder extends FactoryBuilderSupport {
    SampleBuilder(boolean init = true) {
        super(init)
    }

    void registerCollections() {
        registerFactory('map', new MapFactory())
        registerFactory('list', new ListFactory())
    }

    void registerSupport() {
        registerExplicitMethod('foo', this.&foo)
    }

    static foo() { 'foo' }
}

class MapFactory extends AbstractFactory {
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
        throws InstantiationException, IllegalAccessException {
        return attributes
    }
}

class ListFactory extends AbstractFactory {
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
        throws InstantiationException, IllegalAccessException {
        return [name, value]
    }
}

class Bean1{}
class Bean2{}
class Bean3{}
