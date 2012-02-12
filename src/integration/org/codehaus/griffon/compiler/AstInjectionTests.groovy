package org.codehaus.griffon.compiler

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import static org.codehaus.griffon.cli.CommandLineConstants.*

class AstInjectionTests extends AbstractCompilerTestCase {
    AstInjectionTests() {
        super('sample')
    }

    void testProjectCompilesOK() {
        compile()
        //TODO: assert injections on artifacts
    }

    void testProjectCompilesOKWithoutInjections() {
        System.setProperty(KEY_DISABLE_AST_INJECTION, 'true')
        System.setProperty(KEY_DISABLE_LOGGING_INJECTION, 'true')
        System.setProperty(KEY_DISABLE_THREADING_INJECTION, 'true')

        try {
            compile()
        } finally {
            System.clearProperty(KEY_DISABLE_AST_INJECTION)
            System.clearProperty(KEY_DISABLE_LOGGING_INJECTION)
            System.clearProperty(KEY_DISABLE_THREADING_INJECTION)
        }
    }

    void testProjectFailsCompilationDueToUnresolvedImports() {
        System.setProperty(KEY_DISABLE_AUTO_IMPORTS, 'true')

        try {
            shouldFail(MultipleCompilationErrorsException) {
                compile()
            }
        } finally {
            System.clearProperty(KEY_DISABLE_AUTO_IMPORTS)
        }
    }
}
