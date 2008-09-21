import com.u2d.css4swing.style.ComponentStyle

class CssDemoBuilderAddon {
   static attributeDelegates = [
      { builder, node, attrs ->
         def cssClass = attrs.remove('cssClass')
         if( cssClass ) ComponentStyle.addClass( node, cssClass )
      }
   ]
}
