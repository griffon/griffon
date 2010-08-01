package org.codehaus.griffon.runtime.core

import griffon.core.GriffonClass
import griffon.core.GriffonApplication
import griffon.test.GriffonUnitTestCase
import griffon.test.mock.MockGriffonApplication

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

abstract class AbstractGriffonClassTestCase extends GriffonUnitTestCase {
    protected GriffonApplication app

    void setUp() {
        app = new MockGriffonApplication()
    }

    void testRespondsToMvcMethods() {
         GriffonClass griffonClass = getGriffonClassUnderTest()
         def subject = griffonClass.newInstance()
         MetaClass metaClass = subject.metaClass

         assert metaClass.respondsTo(subject, 'newInstance', [Class, String] as Class[])
         assert metaClass.respondsTo(subject, 'buildMVCGroup', [String] as Class[])
         assert metaClass.respondsTo(subject, 'buildMVCGroup', [String, String] as Class[])
         assert metaClass.respondsTo(subject, 'buildMVCGroup', [String, Map] as Class[])
         assert metaClass.respondsTo(subject, 'buildMVCGroup', [String, String, Map] as Class[])
         assert metaClass.respondsTo(subject, 'createMVCGroup', [String] as Class[])
         assert metaClass.respondsTo(subject, 'createMVCGroup', [String, String] as Class[])
         assert metaClass.respondsTo(subject, 'createMVCGroup', [String, Map] as Class[])
         assert metaClass.respondsTo(subject, 'createMVCGroup', [String, String, Map] as Class[])
         assert metaClass.respondsTo(subject, 'destroyMVCGroup', [String] as Class[])
         assert metaClass.respondsTo(subject, 'isUIThread', new Class[0])
         assert metaClass.respondsTo(subject, 'execAsync', [Closure] as Class[])
         assert metaClass.respondsTo(subject, 'execSync', [Closure] as Class[])
         assert metaClass.respondsTo(subject, 'execOutside', [Closure] as Class[])
         assert metaClass.respondsTo(subject, 'execFuture', [Closure] as Class[])
         assert metaClass.respondsTo(subject, 'execFuture', [Callable] as Class[])
         assert metaClass.respondsTo(subject, 'execFuture', [ExecutorService, Closure] as Class[])
         assert metaClass.respondsTo(subject, 'execFuture', [ExecutorService, Callable] as Class[])
    }

    protected abstract GriffonClass getGriffonClassUnderTest()
}
