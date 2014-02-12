/*
 * Copyright 2007-2014 the original author or authors.
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

package griffon.test.ast.injection;

import griffon.core.GriffonControllerClass;
import griffon.core.controller.GriffonControllerActionManager;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;

import static java.lang.reflect.Modifier.PROTECTED;
import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 * @author Andres Almiray
 * @since 1.5.0
 */
public class GriffonControllerTestInjector extends AbstractArtifactTestInjector {
    private static final String METHOD_INVOKE_ACTION = "invokeAction";
    private static final ClassNode GRIFFON_CONTROLLER_ACTION_MANAGER_TYPE = ClassHelper.make(GriffonControllerActionManager.class);

    public String getVariableName() {
        return GriffonControllerClass.TYPE;
    }

    public String getArtifactType() {
        return GriffonControllerClass.TRAILING;
    }

    @Override
    public void inject(ClassNode classNode, ClassExpression classExpr) {
        super.inject(classNode, classExpr);

        MethodNode invokeActionMethod = null;
        for (MethodNode methodNode : GRIFFON_CONTROLLER_ACTION_MANAGER_TYPE.getAbstractMethods()) {
            if (methodNode.getName().equals(METHOD_INVOKE_ACTION)) {
                invokeActionMethod = methodNode;
                break;
            }
        }
        Parameter[] parameters = invokeActionMethod.getParameters();
        classNode.addMethod(
            invokeActionMethod.getName(),
            PROTECTED,
            invokeActionMethod.getReturnType(),
            params(parameters[1], parameters[2]),
            invokeActionMethod.getExceptions(),
            stmnt(call(
                call(prop(THIS, APP), "getActionManager", NO_ARGS),
                METHOD_INVOKE_ACTION,
                args(prop(THIS, getVariableName()), var(parameters[1].getName()), var(parameters[2].getName())))
            )
        );
    }
}