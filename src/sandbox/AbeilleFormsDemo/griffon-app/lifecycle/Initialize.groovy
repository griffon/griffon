import griffon.util.GriffonPlatformHelper
import com.jeta.forms.components.panel.FormPanel

GriffonPlatformHelper.tweakForNativePlatform(app)

FormPanel.metaClass.getProperty = { name ->
   def metaProperty= FormPanel.metaClass.getMetaProperty(name) 
   metaProperty? metaProperty.getProperty(delegate): delegate.getComponentByName(name)
}
