package org.codehaus.griffon.ant.taskdefs

class FileMergeTaskTests extends GroovyTestCase {
    void testSkip() {
        File f1 = createTempFile('skip')
        File f2 = createTempFile('skip')

        f1.text = 'foo'
        f2.text = 'bar'

        new FileMergeTask.Skip().merge(f1, f2)

        assert f1.text == 'foo'
    }

    void testReplace() {
        File f1 = createTempFile('replace')
        File f2 = createTempFile('replace')

        f1.text = 'foo'
        f2.text = 'bar'

        new FileMergeTask.Replace().merge(f1, f2)

        assert f1.text == 'bar'
    }

    void testAppend() {
        File f1 = createTempFile('append')
        File f2 = createTempFile('append')

        f1.text = 'foo\n'
        f2.text = 'bar'

        new FileMergeTask.Append().merge(f1, f2)

        assert f1.text.trim() == 'foo\nbar'.trim()
    }

    void testMerge() {
        File f1 = createTempFile('merge')
        File f2 = createTempFile('merge')

        f1.text = '1\n2\n3\n4'
        f2.text = '2\n4\n6\n8'

        new FileMergeTask.Merge().merge(f1, f2)

        assert f1.text.trim() == '1\n2\n3\n4\n6\n8'.trim()
    }

    void testMergeProperties() {
        File f1 = createTempFile('mergeprops')
        File f2 = createTempFile('mergeprops')

        f1.text = text('''
        foo = foo
        bar = bar
        ''')
        f2.text = text('''
        baz = baz
        bar = xxx
        ''')

        new FileMergeTask.MergeProperties().merge(f1, f2)

        Properties p = new Properties()
        f1.withInputStream {p.load(it)}
        assert p.foo == 'foo'
        assert p.bar == 'xxx'
        assert p.baz == 'baz'
    }

    void testMergeGriffonArtifacts() {
        File f1 = createTempFile('mergearts')
        File f2 = createTempFile('mergearts')

        f1.text = text('''
        controller = 'foo'
        model = 'foo'
        view = 'foo'
        ''')
        f2.text = text('''
        controller = 'bar'
        model = 'bar'
        view = 'bar'
        service = 'bar'
        ''')

        new FileMergeTask.MergeGriffonArtifacts().merge(f1, f2)

        Properties p = new Properties()
        f1.withInputStream {p.load(it)}
        assert p.controller == "'foo,bar'"
        assert p.model == "'foo,bar'"
        assert p.view == "'foo,bar'"
        assert p.service == "'bar'"
    }

    private File createTempFile(String name) {
        File f = File.createTempFile(name, '.txt')
        f.deleteOnExit()
        f
    }

    private String text(int indent = 8, String content) {
        content.stripIndent(indent).trim()
    }
}
