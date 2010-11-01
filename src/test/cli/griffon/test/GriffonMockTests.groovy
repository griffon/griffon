/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.test

import junit.framework.AssertionFailedError

/**
 * Test case for {@link GriffonMock}.
 */
class GriffonMockTests extends GroovyTestCase {
    def savedMetaClass

    void setUp() {
        super.setUp()
        this.savedMetaClass = GriffonMockCollaborator.metaClass

        // Create a new EMC for the class and attach it.
        def emc = new ExpandoMetaClass(GriffonMockCollaborator, true, true)
        emc.initialize()
        GroovySystem.metaClassRegistry.setMetaClass(GriffonMockCollaborator, emc)
    }

    void tearDown() {
        super.tearDown()

        // Restore the saved meta class.
        GroovySystem.metaClassRegistry.setMetaClass(GriffonMockCollaborator, this.savedMetaClass)
    }

    void testMethod() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.save(1..1) {-> return false }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        assertEquals false, testClass.testMethod()

        mockControl.verify()
    }

    void testVerifyFails() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.save(2..2) {-> return false }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        assertEquals false, testClass.testMethod()

        shouldFail(AssertionFailedError) {
            mockControl.verify()
        }
    }

    void testTooManyCalls() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.save(1..1) {->
            return false
        }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        shouldFail(AssertionFailedError) {
            testClass.testMethod2()
        }
    }

    void testMissingMethod() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.merge(1..1) {->
            return false
        }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        shouldFail(AssertionFailedError) {
            testClass.testDynamicMethod()
        }
    }

    void testOverridingMetaClassMethod() {
        GriffonMockCollaborator.metaClass.update = {-> return "Failed!"}

        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.update() {-> return "Success!"}

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        assertEquals "Success!", testClass.testDynamicMethod()
    }

    void testStaticMethod() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.get(1..1) { assert it == 5; return "Success!" }

        def testClass = new GriffonMockTestClass()
        assertEquals "Success!", testClass.testStaticMethod()

        mockControl.verify()
    }

    void testStaticVerifyFails() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.get(2..2) { assert it == 5; return "Success!" }

        def testClass = new GriffonMockTestClass()
        assertEquals "Success!", testClass.testStaticMethod()

        shouldFail(AssertionFailedError) {
            mockControl.verify()
        }
    }

    void testStaticTooManyCalls() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.get(1..1) { assert it == 5; return "Success!" }

        def testClass = new GriffonMockTestClass()
        shouldFail(AssertionFailedError) {
            testClass.testStaticMethod2()
        }
    }

    void testStaticMissingMethod() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.find(1..1) { assert it == 5; return "Success!" }

        def testClass = new GriffonMockTestClass()
        shouldFail(MissingMethodException) {
            testClass.testStaticMethod()
        }
    }

    void testOverridingStaticMetaClassMethod() {
        GriffonMockCollaborator.metaClass.static.findByNothing = {-> return "Failed!"}

        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.findByNothing() {-> return "Success!"}


        def testClass = new GriffonMockTestClass()
        assertEquals "Success!", testClass.testDynamicStaticMethod()
    }

    void testStrictOrdering() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.static.get(1..1) { "OK" }
        mockControl.demand.update(1..1) { "OK" }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()
        testClass.testCorrectOrder()
        mockControl.verify()

        mockControl.demand.static.get(1..1) { "OK" }
        mockControl.demand.update(1..1) { "OK" }

        shouldFail(AssertionFailedError) {
            testClass.testWrongOrder()
        }
    }

    void testLooseOrdering() {
        def mockControl = new GriffonMock(GriffonMockCollaborator, true)
        mockControl.demand.static.get(1..1) { "OK" }
        mockControl.demand.update(1..1) { "OK" }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()
        testClass.testCorrectOrder()
        mockControl.verify()

        mockControl.demand.static.get(1..1) { "OK" }
        mockControl.demand.update(1..1) { "OK" }

        testClass.testWrongOrder()
        mockControl.verify()
    }

    /**
     * Tests that the argument matching for demanded methods works
     * properly.
     */
    void testArgumentMatching() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.multiMethod(1..1) {-> return "dynamic" }
        mockControl.demand.multiMethod(1..1) { String str ->
            assertEquals "Test string", str
            return "dynamic"
        }
        mockControl.demand.multiMethod(1..1) { String str, Map map ->
            assertEquals "Test string", str
            assertNotNull map
            assertEquals 1, map["arg1"]
            assertEquals 2, map["arg2"]
            return "dynamic"
        }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()
        
        def retval = testClass.testMultiMethod()

        // Check that the dynamic methods were called rather than the
        // ones that are statically defined on the collaborator.
        assertTrue retval.every { it == "dynamic" }
    }

    /**
     * GRAILS-3508
     *
     * Tests that the mock works OK if the mocked method is called with
     * any <ocde>null</code> arguments.
     */
    void testNullArguments() {
        def mockControl = new GriffonMock(GriffonMockCollaborator)
        mockControl.demand.multiMethod(1..1) { String str ->
            assertNull str
            return "dynamic"
        }

        def testClass = new GriffonMockTestClass()
        testClass.collaborator = mockControl.createMock()

        assertEquals "dynamic", testClass.testNullArgument()
    }

	void testEmptyArrayArguments() {
		def mockControl = new GriffonMock(GriffonMockCollaborator)
		mockControl.demand.testEmptyArrayArguments(1..1) { String str1, Object[] args, String str2 ->
			return "dynamic"
		}

		def testClass = new GriffonMockTestClass()
		testClass.collaborator = mockControl.createMock()

		assertEquals "dynamic", testClass.testEmptyArrayArguments()
	}

    /**
     * Tests that mocking an interface works.
     */
    void testInterface() {
        def mockControl = new GriffonMock(GriffonMockInterface)
        mockControl.demand.testMethod(1..1) { String name, int qty ->
            assert name == "brussels"
            assert qty == 5
            return "brussels-5"
        }

        def testClass = new GriffonMockTestClass()
        testClass.gmi = mockControl.createMock()

        assertEquals "brussels-5", testClass.testInterfaceCollaborator()
    }
}

class GriffonMockTestClass {
    GriffonMockCollaborator collaborator
    GriffonMockInterface gmi

    boolean testMethod() {
        return this.collaborator.save()
    }

    String testDynamicMethod() {
        return this.collaborator.update()
    }

    boolean testMethod2() {
        this.testMethod()
        return this.testMethod()
    }

    String testStaticMethod() {
        return GriffonMockCollaborator.get(5)
    }

    String testStaticMethod2() {
        testStaticMethod()
        return testStaticMethod()
    }

    String testDynamicStaticMethod() {
        return GriffonMockCollaborator.findByNothing()
    }

    void testCorrectOrder() {
        GriffonMockCollaborator.get(5)
        this.collaborator.update()
    }

    void testWrongOrder() {
        this.collaborator.update()
        GriffonMockCollaborator.get(5)
    }

    String testNullArgument() {
        return this.collaborator.multiMethod(null)
    }

	String testEmptyArrayArguments() {
		return this.collaborator.testEmptyArrayArguments('abc', [] as Object[], 'def')
	}

    String testInterfaceCollaborator() {
        return this.gmi.testMethod("brussels", 5)
    }

    List testMultiMethod() {
        List methodReturns = []
        methodReturns << this.collaborator.multiMethod()
        methodReturns << this.collaborator.multiMethod("Test string")
        methodReturns << this.collaborator.multiMethod("Test string", [arg1: 1, arg2: 2])
        return methodReturns
    }
}

class GriffonMockCollaborator {
    def save() {
        return true
    }

    String multiMethod() {
        return "static"
    }

    String multiMethod(String str) {
        return "static"
    }

    String multiMethod(String str, Map map) {
        return "static"
    }

	String someMethod(String str1, Object[] args, String str2) {
		return 'static'
	}
}

interface GriffonMockInterface {
    String testMethod(String name, int quantity)
}

class GriffonMockImpl implements GriffonMockInterface {
    String testMethod(String name, int quantity) {
        return name * quantity
    }
}
