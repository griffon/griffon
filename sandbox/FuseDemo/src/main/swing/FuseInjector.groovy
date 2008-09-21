package swing

import org.jdesktop.fuse.SwingHive

class FuseInjector {
   private static List targets = []
   private static SwingHive swingHive = new SwingHive()

   static addInjectTarget( target ) {
      targets << target
   }

   static injectResources() {
      targets.each { swingHive.inject(it) }
   }

   static changeTheme( String themeId ) {
      swingHive.load("/swing/${themeId}.uitheme")
   }
}
