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

import griffon.core.GriffonServiceClass;
import griffon.util.GriffonUtil;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.stmt.Statement;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 * @author Andres Almiray
 * @since 1.5.0
 */
public class GriffonServiceTestInjector extends AbstractArtifactTestInjector {
    public String getVariableName() {
        return GriffonServiceClass.TYPE;
    }

    public String getArtifactType() {
        return GriffonServiceClass.TRAILING;
    }

    public Statement getInitializingStatement(ClassExpression classExpr) {
        return stmnt(assign(prop(THIS, getVariableName()),
            call(
                prop(prop(THIS, APP), constx("serviceManager")),
                "findService",
                args(constx(GriffonUtil.getLogicalPropertyName(classExpr.getType().getName(), getArtifactType())))
            )));
    }

}