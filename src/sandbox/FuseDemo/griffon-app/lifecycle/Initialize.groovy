import griffon.util.GriffonPlatformHelper
import groovy.swing.SwingBuilder

GriffonPlatformHelper.tweakForNativePlatform(app)
SwingBuilder.lookAndFeel('nimbus', 'mac', 'gtk', ['metal', [boldFonts: false]])

