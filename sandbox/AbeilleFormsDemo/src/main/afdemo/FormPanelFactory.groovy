package afdemo

import com.jeta.forms.components.panel.FormPanel

class FormPanelFactory extends AbstractFactory {
   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
      if( value instanceof FormPanel ) return value
      if( value instanceof String ) {
         return new FormPanel(value)
      }
      def form = attributes.remove("form")
      if( form instanceof String ) return new FormPanel(form)
      threw new IllegalArgumentException("!!!")
   }

   public boolean isLeaf() { true }
}
