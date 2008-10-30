package org.codehaus.griffon.console

prefsPane = dialog(owner:consoleFrame, title:'Editor Preferences', pack:true, locationByPlatform:true) {

  vbox {
    autoIndentCB = checkBox('Auto Indent', selected: bind { model.autoIndentEnabled } )
    bracketMatchingCB = checkBox('Bracket Matching', selected: bind { model.bracketMatchingEnabled } )
    emptyLineCB = checkBox('Clear Empty Lines', selected: bind { model.clearWhitespaceLinesEnabled} )
    hilightLineCB = checkBox('Highlight Current Line', selected: bind { model.currentLineHighlightEnabled} )
    fadeCB = checkBox('Fade Highlight', selected: bind { model.fadeCurrentLineHighlight} )
    lineWrapCB = checkBox('Line Wrap', selected: bind { model.lineWrap} )
    edgeMarginCB = checkBox('Show Edge Margin', selected: bind { model.marginLineEnabled} )
    softTabsCB = checkBox('Soft Tabs', selected: bind { model.tabsEmulated } )
    whitespaceCB = checkBox('Show Whitespace', selected: bind { model.whitespaceVisible} )

    //@Bindable def background // java.awt.Color or java.awt.Image
    //@Bindable Color currentLineHighlightColor
    //@Bindable boolean fractionalFontMetricsEnabled
    //@Bindable Font font
    //@Bindable Color marginLineColor
    //@Bindable int marginLinePosition
    //@Bindable Color matchedBracketBGColor
    //@Bindable Color matchedBracketBorderColor
    ////@Bindable SyntaxHighlightingColorScheme syntaxHighlightingColorScheme
    //@Bindable int tabSize
    //@Bindable File templateDirectory
    //@Bindable boolean templatesEnabled
    //@Bindable whitespaceVisible

    vstrut(12)
    hbox {
      hglue()
      button('OK', actionPerformed: controller.&okButtonPressed)
      hstrut(6)
      button('Cancel', actionPerformed: controller.&cancelButtomPressed)
      hstrut(6)
      button('Apply', actionPerformed: controller.&applyButtonPressed)
      hstrut(6)
    }
    vstrut(6)
  }
    
}


//TODO bidirectional bind
bean (model,
    autoIndentEnabled: bind { autoIndentCB.selected },
    bracketMatchingEnabled: bind { bracketMatchingCB.selected },
    clearWhitespaceLinesEnabled: bind { emptyLineCB.selected },
    currentLineHighlightEnabled: bind { hilightLineCB.selected },
    fadeCurrentLineHighlight: bind { fadeCB.selected },
    lineWrap: bind { lineWrapCB.selected },
    marginLineEnabled : bind { edgeMarginCB.selected },
    tabsEmulated : bind {softTabsCB.selected},
    whitespaceVisible : bind { whitespaceCB.selected }
)