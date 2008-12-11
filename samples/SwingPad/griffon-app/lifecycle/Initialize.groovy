import groovy.swing.SwingBuilder
import griffon.util.GriffonPlatformHelper

GriffonPlatformHelper.tweakForNativePlatform(app)
SwingBuilder.lookAndFeel('nimbus', 'mac', 'gtk', ['metal', [boldFonts: false]])
