/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.swing.factory

import groovy.swing.impl.Jdk5SwingWorker

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class SwingWorkerFactory extends AbstractFactory {
   public Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map properties ) throws InstantiationException, IllegalAccessException {
      new Jdk5SwingWorker( builder )
   }

   public boolean isHandlesNodeChildren() {
      return true
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ) {
      builder.context["start"] = attributes.remove("start")
      return true
   }

   public boolean onNodeChildren( FactoryBuilderSupport builder, Object node, Closure childContent ) {
      node.delegate = childContent.delegate
      childContent.delegate = node
      childContent()
      return false
   }

   public void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
      node.init()
      if( builder.context.start ) node.execute()
   }
}