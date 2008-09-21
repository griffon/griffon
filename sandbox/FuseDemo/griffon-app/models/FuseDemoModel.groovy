import groovy.beans.Bindable

class FuseDemoModel {
   static final String BLACK_THEME = "black"
   static final String RED_THEME = "red"

   @Bindable String currentThemeId = RED_THEME
}