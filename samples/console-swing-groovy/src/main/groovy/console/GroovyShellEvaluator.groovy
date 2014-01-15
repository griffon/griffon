package console

class GroovyShellEvaluator implements Evaluator {
    private GroovyShell shell = new GroovyShell()

    @Override
    Object evaluate(String input) {
        shell.evaluate(input)
    }
}
