/*
 * Copyright 2008 the original author or authors.
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
 */

package groovy.swing.impl

import org.jdesktop.swingworker.SwingWorker

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class Jdk5SwingWorker extends SwingWorker<Object,Object> {
   def delegate

   private boolean initialized
   private initClosure
   private workClosure
   private updateClosure
   private doneClosure
   private final builder

   Jdk5SwingWorker( FactoryBuilderSupport builder ) {
      this.builder = builder
   }

   final void onInit( Closure callable ) {
      initClosure = callable
      initClosure.delegate = this
   }

   final void work( Closure callable ) {
      workClosure = callable
      workClosure.delegate = this
   }

   final void onUpdate( Closure callable ) {
      updateClosure = callable
      updateClosure.delegate = this
   }

   final void onDone( Closure callable ) {
      doneClosure = callable
      doneClosure.delegate = this
   }

   public synchronized void init() {
      if( initialized ) return
      if( initClosure ){
         initClosure()
      }
      if( !workClosure ){
         throw new IllegalStateException("This worker doesn't know what to do! Please define a work{ } block")
      }
      if( !doneClosure ){
         throw new IllegalStateException("This worker doesn't know what to do when it is done! Please define an onDone{ } block")
      }
      initialized = true
   }

   protected Object doInBackground() {
      workClosure()
   }

   protected final void process( List<Object> chunks ) {
      if( !updateClosure ) return
      updateClosure( chunks )
   }

   protected final void done() {
      doneClosure()
   }

   def methodMissing( String name, Object value ) {
      // try the builder first
      try {
         return builder."$name"(value)
      }catch( MissingMethodException mme ) {
         // try original delegate if != builder
         if( delegate && delegate != builder ){
            return delegate."$name"(value)
         }else{
            throw mme
         }
      }
   }

   def propertyMissing( String name ) {
      // try the builder first
      try {
         return builder."$name"
      }catch( MissingPropertyException mpe ) {
         // try original delegate if != builder
         if( delegate && delegate != builder ){
            return delegate."$name"
         }else{
            throw mpe
         }
      }
   }

   def propertyMissing( String name, Object value ) {
      // try the builder first
      try {
         builder."$name" = value
         return
      }catch( MissingMethodException mpe ) {
         // try original delegate if != builder
         if( delegate && delegate != builder ){
            delegate."$name" = value
            return
         }else{
            throw mpe
         }
      }
   }
}
