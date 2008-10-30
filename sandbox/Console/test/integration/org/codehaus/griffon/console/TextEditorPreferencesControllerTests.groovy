package org.codehaus.griffon.console

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants

class TextEditorPreferencesControllerTests extends GroovyTestCase {

    void testReadProperties() {
        RSyntaxTextArea rsta = new RSyntaxTextArea();
        rsta.restoreDefaultSyntaxHighlightingColorScheme()
        rsta.syntaxEditingStyle = SyntaxConstants.GROOVY_SYNTAX_STYLE

        TextEditorPreferencesModel tepm = new TextEditorPreferencesModel()

        assert tepm.font == null
        tepm.readFromObject rsta
        assert tepm.font != null
    }

    void testWriteProperties() {
        RSyntaxTextArea rsta = new RSyntaxTextArea();
        rsta.restoreDefaultSyntaxHighlightingColorScheme()
        rsta.syntaxEditingStyle = SyntaxConstants.GROOVY_SYNTAX_STYLE

        TextEditorPreferencesModel tepm = new TextEditorPreferencesModel()

        tepm.readFromObject rsta
        tepm.tabsEmulated = !tepm.tabsEmulated
        assert tepm.tabsEmulated != rsta.tabsEmulated
        tepm.applyToObject rsta
        assert tepm.tabsEmulated == rsta.tabsEmulated
    }

}
