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
 * limitations under the License.
 */
package groovy.swing

import groovy.util.GroovySwingTestCase
import groovy.swing.factory.SwingWorkerFactory

class SwingWorkerTest extends GroovySwingTestCase {
   public void testWorker_answerDirectly() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         work {
            Thread.sleep 300
            42
         }

         onDone {
            theUltimateAnswer = get() 
         }
      }

      assert theUltimateAnswer == 0
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
   }

   public void testWorker_answerWithLocalClosure() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         def compute = { 42 }

         work {
            Thread.sleep 300
            compute()
         }

         onDone {
            theUltimateAnswer = get() 
         }
      }

      assert theUltimateAnswer == 0
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
   }

   public void testWorker_answerWithGlobalClosure() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def compute = { 42 }
      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         work {
            Thread.sleep 300
            compute()
         }

         onDone {
            theUltimateAnswer = get() 
         }
      }

      assert theUltimateAnswer == 0
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
   }

   public void testWorker_answerWithLocalVar() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         def answer = 42

         work {
            Thread.sleep 300
            answer
         }

         onDone {
            theUltimateAnswer = get() 
         }
      }

      assert theUltimateAnswer == 0
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
   }

   public void testWorker_answerWithGlobalVar() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def answer = 42
      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         work {
            Thread.sleep 300
            return answer
         }

         onDone {
            theUltimateAnswer = get() 
         }
      }

      assert theUltimateAnswer == 0
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
   }

   public void testWorker_answerWithBuilderVars() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      builder.answer = 42
      def theUltimateAnswer = 0
      def deepThought = builder.withWorker {
         work {
            Thread.sleep 300
            answer
         }

         onDone {
            theUltimateAnswer = get() 
            theUltimateQuestion = "unknown"
         }
      }

      assert theUltimateAnswer == 0
      shouldFail( MissingPropertyException ){
         assert builder.theUltimateQuestion == null
      }
      deepThought.execute()
      Thread.sleep 500
      assert theUltimateAnswer == 42
      assert builder.theUltimateQuestion == "unknown"
   }

   public void testWorker_publishAndProcess() {
      if( isHeadless() ) return

      SampleBuilder builder = new SampleBuilder()
      builder.registerFactory( "withWorker", new SwingWorkerFactory() )

      def processed = []
      def deepThought = builder.withWorker {
         work {
            (0..9).inject([]) { l, v -> 
               Thread.sleep 100
               publish( v )
               l << v
            }
         }

         onUpdate { List<Object> chunks ->
            chunks.each { processed << it }
         }

         onDone {
            answer = get() 
         }
      }

      deepThought.execute()
      Thread.sleep 100 * 13
      assert builder.answer == [0,1,2,3,4,5,6,7,8,9]
      assert processed == [0,1,2,3,4,5,6,7,8,9]
   }
}

class SampleBuilder extends FactoryBuilderSupport {
}
