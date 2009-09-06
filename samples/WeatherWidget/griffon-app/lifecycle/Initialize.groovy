import groovy.swing.SwingBuilder
import griffon.util.GriffonPlatformHelper

GriffonPlatformHelper.tweakForNativePlatform(app)
SwingBuilder.lookAndFeel('mac', 'nimbus', 'gtk', ['metal', [boldFonts: false]])