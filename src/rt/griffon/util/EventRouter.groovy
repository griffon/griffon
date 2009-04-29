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
   private List listeners = Collections.synchronizedList([])
   private Map scriptBindings = [:]
   private Map closureListeners = Collections.synchronizedMap([:])

   public void publish( String eventName, List params = [] ) {
      if( !eventName ) return
      def publisher = {
         eventName = eventName[0].toUpperCase() + eventName[1..-1]
         def eventHandler = "on" + eventName
         def dispatchEvent = { listener ->
            try {
               fireEvent(listener, eventHandler, params)
            } catch( x ) {
               // TODO log exception
               x.printStackTrace()
            }
         }
         synchronized(listeners) {
            listeners.each{ dispatchEvent(it) }
         }
         synchronized(closureListeners) {
            closureListeners[eventName].each{ dispatchEvent(it) }
         }
      }
      if( SwingUtilities.isEventDispatchThread() ) {
         Thread.start { publisher() }
      } else {
         publisher()
      }
   }

   private void fireEvent( Script script, String eventHandler, List params ) {
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

   private void fireEvent( Map map, String eventHandler, List params ) {
      eventHandler = eventHandler[2..-1]
      def handler = map[eventHandler]
      if( handler && handler instanceof Closure ) {
         handler(*params)
      }
   }

   private void fireEvent( Closure closure, String eventHandler, List params ) {
      closure(*params)
   }

   private void fireEvent( Object instance, String eventHandler, List params ) {
      def mp = instance.metaClass.getMetaProperty(eventHandler)
      if( mp && mp.getProperty(instance) ) {
         mp.getProperty(instance)(*params)
         return
      }

      def mm = instance.metaClass.getMetaMethod(eventHandler,params)
      if( mm ) {
         mm.invoke(instance,*params)
      }
   }

   /**
    * Adds an ApplicationEvent listener.<br/>
    *
    * A listener may be<ul>
    * <li>a <tt>Script</tt></li>
    * <li>a <tt>Bean</tt></li>
    * <li>a <tt>Map</tt></li>
    * </ul>
    *
    * With the exception of Maps, the naming convention for an eventHandler is
    * "on" + eventName, Maps require handlers to be named as eventName only.<p>
    * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
    * Event names must follow the camelCase naming convention.
    */
   public void addApplicationEventListener( listener ) {
      if( !listener || listener instanceof Closure ) return
      synchronized(listeners) {
         if( listeners.find{ it == listener } ) return
         listeners.add(listener)
      }
   }

   public void removeApplicationEventListener( listener ) {
      if( !listener || listener instanceof Closure ) return
      synchronized(listeners) {
         listeners.remove(listener)
      }
   }

   /**
    * Adds a Closure as an ApplicationEvent listener.<br/>
    *
    * Event names must follow the camelCase naming convention.
    */
   public void addApplicationEventListener( String eventName, Closure listener ) {
      if( !eventName || !listener ) return
      eventName = eventName[0].toUpperCase() + eventName[1..-1]
      synchronized(closureListeners) {
         def list = closureListeners.get(eventName,[])
         if( list.find{ it == listener } ) return
         list.add(listener)
      }
   }

   public void removeApplicationEventListener( String eventName, Closure listener ) {
      if( !eventName || !listener ) return
      eventName = eventName[0].toUpperCase() + eventName[1..-1]
      synchronized(closureListeners) {
         def list = closureListeners[eventName]
         if( list ) list.remove(listener)
      }
   }
}