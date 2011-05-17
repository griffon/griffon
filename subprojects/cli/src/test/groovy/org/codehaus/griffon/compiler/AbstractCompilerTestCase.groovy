package org.codehaus.griffon.compiler

abstract class AbstractCompilerTestCase extends GroovyTestCase {
    protected String projectName
    protected File baseDir
    protected File classesDir
    protected ClassLoader projectClassLoader
    private ClassLoader contextClassLoader
    private AntBuilder ant

    AbstractCompilerTestCase(String projectName) {
        this.projectName = projectName
        this.baseDir = new File("${System.getProperty('user.dir')}/build/classes/test/test-projects/${projectName}")
        this.classesDir = new File("${baseDir}/target/classes")
    }

   protected void compile() {
       try {
           doCompile()
       } catch(x) {
           throw x.cause ?: x
       }
   }

   private void doCompile() {
        String classpathId = 'griffon.compile.classpath'
        ant.taskdef(name: 'griffonc', classname : 'org.codehaus.griffon.compiler.GriffonCompiler')
        ant.path(id: classpathId) {
            AbstractCompilerTestCase.class.classLoader.getURLs().each { url ->
                 pathelement(location: "${url.toString().substring(5)}")
            }
            for(dir in new File("${baseDir}/griffon-app").listFiles()) {
                pathelement(location: "${dir.absolutePath}")
            }
            pathelement(location: "${classesDir.absolutePath}")
            pathelement(location: "${System.getProperty('user.dir')}/src/resources/cli/")
            pathelement(location: "${System.getProperty('user.dir')}/src/metainf/cli/")
        }

        def urls = AbstractCompilerTestCase.class.classLoader.URLs.collect([]){it}
        urls << classesDir.toURI().toURL()
        urls << new File("${System.getProperty('user.dir')}/src/resources/cli").toURI().toURL()
        urls << new File("${System.getProperty('user.dir')}/src/metainf/cli").toURI().toURL()
        projectClassLoader = new URLClassLoader(urls as URL[], null)
        Thread.currentThread().contextClassLoader = projectClassLoader

        ant.echo(message: "${this.class.name}.${name}")
        ant.griffonc(destdir: classesDir,
                projectName: projectName,
                basedir: baseDir.absolutePath,
                classpathref: classpathId,
                encoding: 'UTF-8') {
            def excludedPaths = ['resources', 'i18n', 'conf']
            for(dir in new File("${baseDir}/griffon-app").listFiles()) {
                if(!excludedPaths.contains(dir.name) && dir.directory) src(path: "$dir")
            }
            def srcMain = new File("${baseDir}/src/main")
            if(srcMain.exists()) src(path: srcMain)
            javac(classpathref: classpathId, encoding: 'UTF-8')
        }
    }

    protected void setUp() {
        contextClassLoader = Thread.currentThread().contextClassLoader
        ant = new AntBuilder()
        ant.delete(dir: classesDir) 
        ant.mkdir(dir: classesDir) 
    }

    protected void tearDown() {
        ant.delete(dir: classesDir, quiet: true) 
        Thread.currentThread().contextClassLoader = contextClassLoader
    }
}
