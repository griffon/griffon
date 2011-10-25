package org.codehaus.griffon.compiler

import org.codehaus.groovy.control.MultipleCompilationErrorsException

class AstInjectionTests extends AbstractCompilerTestCase {
    AstInjectionTests() {
        super('sample')
    }

    void testProjectCompilesOK() {
        compile()
        //TODO: assert injections on artifacs
    }

    void testProjectCompilesOKWithoutInjections() {
        System.setProperty(GriffonCompilerContext.DISABLE_AST_INJECTION, 'true')
        System.setProperty(GriffonCompilerContext.DISABLE_LOGGING_INJECTION, 'true')
        System.setProperty(GriffonCompilerContext.DISABLE_THREADING_INJECTION, 'true')

        try {
            compile()
        } finally {
            System.clearProperty(GriffonCompilerContext.DISABLE_AST_INJECTION)
            System.clearProperty(GriffonCompilerContext.DISABLE_LOGGING_INJECTION)
            System.clearProperty(GriffonCompilerContext.DISABLE_THREADING_INJECTION)
        }
    }

    void testProjectFailsCompilationDueToUnresolvedImports() {
        System.setProperty(GriffonCompilerContext.DISABLE_AUTO_IMPORTS, 'true')

        try {
            shouldFail(MultipleCompilationErrorsException) {
                compile()
            }
        } finally {
            System.clearProperty(GriffonCompilerContext.DISABLE_AUTO_IMPORTS)
        }
    }
}
