/*
 * Copyright 2012-2013 the original author or authors.
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

package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import lombok.javac.JavacNode;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;

/**
 * @author Andres Almiray
 */
public class JavacUtil {
    public static void addInterface(JavacNode node, String interfaceName) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) node.get();
        final ListBuffer<JCTree.JCExpression> implementing = ListBuffer.lb();
        implementing.appendList(classDecl.implementing);
        implementing.append(chainDotsString(node, interfaceName));
        classDecl.implementing = implementing.toList();
    }
}
