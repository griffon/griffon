import com.jeta.forms.components.panel.FormPanel
import griffon.util.GriffonPlatformHelper

GriffonPlatformHelper.tweakForNativePlatform(app)

FormPanel.metaClass.getProperty = { name ->
   def metaProperty= FormPanel.metaClass.getMetaProperty(name) 
   metaProperty? metaProperty.getProperty(delegate): delegate.getComponentByName(name)
}
