//import org.jdesktop.fuse.SwingHive

import swing.FuseInjector

class FuseDemoController {
   //SwingHive swingHive = new SwingHive()
   def model
   def builder

   def changeTheme( String themeId ) {
      if( model.currentThemeId != themeId ) {
         //swingHive.load("/swing/${themeId}.uitheme")
         FuseInjector.changeTheme( themeId )
         model.currentThemeId = themeId
      }
   }

   /*
   def injectResources( List targets ) {
      targets.each { swingHive.inject(builder."$it") }
   }
   */
}