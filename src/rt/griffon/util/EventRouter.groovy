/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util

import javax.swing.SwingUtilities

class EventRouter {
   private List listeners = []
   private Map scriptBindings = [:]

   public void publish( String eventName, List params = [] ) {
      if( !eventName ) return
      def publisher = {
         def eventHandler = "on" + eventName[0].toUpperCase() + eventName[1..-1]
         listeners.each { listener ->
            try {
               if( listener instanceof Script ) {
                  fireEventOnScript(listener, eventHandler, params)
               } else {
                  fireEventOnClass(listener, eventHandler, params)
               }
            } catch( x ) {
               // TODO log exception
               println x
            }
         }
      }
      if( SwingUtilities.isEventDispatchThread() ) {
         Thread.start { publisher() }
      } else {
         publisher()
      }
   }

   private void fireEventOnScript( script, String eventHandler, List params ) {
      def binding = scriptBindings[script]
      if( !binding ) {
         binding = new Binding()
         script.binding = binding
         script.run()
         scriptBindings[script] = binding
      }

      for( variable in script.binding.variables ) {
         def m = variable.key =~ /$eventHandler/
         if( m.matches() ) {
            variable.value(*params)
            return
         }
      }
   }

   private void fireEventOnClass( instance, String eventHandler, List params ) {
      def mp = instance.metaClass.getMetaProperty(eventHandler)
      if( mp ) {
         mp.getProperty(instance)(*params)
      }
   }

   public void addApplicationEventListener( listener ) {
      if( !listener ) return
      if( listeners.find{ it == listener } ) return
      listeners << listener
   }

   public void removeApplicationEventListener( listener ) {
      if( !listener ) return
      listeners -= listener
   }
}