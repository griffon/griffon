/*
 * Copyright 2009-2011 the original author or authors.
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

import org.codehaus.groovy.runtime.MetaClassHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * An event handling helper.<p>
 * Listeners may be of type<ul>
 * <li>a <tt>Script</tt></li>
 * <li>a <tt>Map</tt></li>
 * <li>a <tt>Closure</tt></li>
 * <li>a <tt>Object</tt> (a Java bean)</li>
 * </ul>
 *
 * With the exception of Maps and Closures, the naming convention for an eventHandler is
 * "on" + eventName, Maps and Closures require handlers to be named as eventName only.<p>
 * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
 * Event names must follow the camelCase naming convention.<p>
 *
 * @author Andres Almiray
 */
class EventRouter {
   private List listeners = Collections.synchronizedList([])
   private Map scriptBindings = [:]
   private Map closureListeners = Collections.synchronizedMap([:])
   private static final Logger LOG = LoggerFactory.getLogger(EventRouter)

   /**
    * Publishes an event with optional arguments.</p>
    * Event listeners will be notified in the same thread
    * that orifinated the event.
    *
    * @param eventName the name of the event
    * @param params the event's argumnents
    *
    */
   public void publish(String eventName, List params = []) {
       if(!eventName) return
       buildPublisher(eventName, params)('synchronously')  
   }

   /**
    * Publishes an event with optional arguments.</p>
    * Event listeners are guaranteed to be notified
    * outside of the UI thread always.
    *
    * @param eventName the name of the event
    * @param params the event's argumnents
    *
    */
   public void publishAsync(String eventName, List params = []) {
       if(!eventName) return
       UIThreadHelper.instance.executeOutside(buildPublisher(eventName, params).curry('asynchronously'))  
   }
   
   private Closure buildPublisher(String eventName, List params) {
      return { mode ->
         if(LOG.traceEnabled) LOG.trace("Triggering event '$eventName' $mode")
         eventName = eventName[0].toUpperCase() + eventName[1..-1]
         def eventHandler = "on" + eventName
         def dispatchEvent = { listener ->
             // any exceptions that might get thrown should be caught
             // by GriffonExceptionHandler
             fireEvent(listener, eventHandler, params ?: [])
         }

         // defensive copying to avoid CME during event dispatching
         // GRIFFON-224
         List listenersCopy = []
         synchronized(listeners) {
            listenersCopy.addAll(listeners)
         }
         synchronized(closureListeners) {
            closureListeners[eventName].each{ listenersCopy << it }
         }

         listenersCopy.each{ dispatchEvent(it) }
      }
   }

   private void fireEvent(Script script, String eventHandler, List params) {
      def binding = scriptBindings[script]
      if(!binding) {
         binding = new Binding()
         script.binding = binding
         script.run()
         scriptBindings[script] = binding
      }

      for(variable in script.binding.variables) {
         def m = variable.key =~ /$eventHandler/
         if(m.matches()) {
            variable.value(*params)
            return
         }
      }
   }

   private void fireEvent(Map map, String eventHandler, List params) {
      eventHandler = eventHandler[2..-1]
      def handler = map[eventHandler]
      if(handler && handler instanceof Closure) {
         handler(*params)
      }
   }

   private void fireEvent(Closure closure, String eventHandler, List params) {
      closure(*params)
   }

   private void fireEvent(Object instance, String eventHandler, List params) {
      def mp = instance.metaClass.getMetaProperty(eventHandler)
      if(mp && mp.getProperty(instance)) {
         mp.getProperty(instance)(*params)
         return
      }

      Class[] argTypes = MetaClassHelper.convertToTypeArray(params as Object[])
      def mm = instance.metaClass.pickMethod(eventHandler,argTypes)
      if(mm) {
         mm.invoke(instance,*params)
      }
   }

   /**
    * Adds an event listener.<p>
    *
    * A listener may be a<ul>
    * <li>a <tt>Script</tt></li>
    * <li>a <tt>Map</tt></li>
    * <li>a <tt>Object</tt> (a Java bean)</li>
    * </ul>
    *
    * With the exception of Maps, the naming convention for an eventHandler is
    * "on" + eventName, Maps require handlers to be named as eventName only.<p>
    * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
    * Event names must follow the camelCase naming convention.<p>
    *
    * @param listener and event listener of type Script, Map or Object
    */
   public void addEventListener(listener) {
      if(!listener || listener instanceof Closure) return
      synchronized(listeners) {
         if(listeners.find{ it == listener }) return
         listeners.add(listener)
      }
   }

   /**
    * Removes an event listener.<p>
    *
    * A listener may be a<ul>
    * <li>a <tt>Script</tt></li>
    * <li>a <tt>Map</tt></li>
    * <li>a <tt>Object</tt> (a Java bean)</li>
    * </ul>
    *
    * With the exception of Maps, the naming convention for an eventHandler is
    * "on" + eventName, Maps require handlers to be named as eventName only.<p>
    * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
    * Event names must follow the camelCase naming convention.<p>
    *
    * @param listener and event listener of type Script, Map or Object
    */
   public void removeEventListener(listener) {
      if(!listener || listener instanceof Closure) return
      synchronized(listeners) {
         listeners.remove(listener)
      }
   }

   /**
    * Adds a Closure as an event listener.<p>
    * Event names must follow the camelCase naming convention.
    *
    * @param eventName the name of the event
    * @param listener the event listener
    */
   public void addEventListener(String eventName, Closure listener) {
      if(!eventName || !listener) return
      eventName = eventName[0].toUpperCase() + eventName[1..-1]
      synchronized(closureListeners) {
         def list = closureListeners.get(eventName,[])
         if(list.find{ it == listener }) return
         list.add(listener)
      }
   }

   /**
    * Removes a Closure as an event listener.<p>
    * Event names must follow the camelCase naming convention.
    *
    * @param eventName the name of the event
    * @param listener the event listener
    */
   public void removeEventListener(String eventName, Closure listener) {
      if(!eventName || !listener) return
      eventName = eventName[0].toUpperCase() + eventName[1..-1]
      synchronized(closureListeners) {
         def list = closureListeners[eventName]
         if(list) list.remove(listener)
      }
   }
}
