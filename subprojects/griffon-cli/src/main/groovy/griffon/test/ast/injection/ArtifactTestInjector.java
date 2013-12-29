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

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.stmt.Statement;

/**
 * @author Andres Almiray
 * @since 1.5.0
 */
public interface ArtifactTestInjector {
    String APP = "app";

    String METHOD_CREATE_MOCK_APPLICATION = "createMockApplication";

    String METHOD_CONFIG_MOCK_APPLICATION = "configMockApplication";

    String METHOD_SETUP_MOCK_APPLICATION = "setupMockApplication";

    String METHOD_CLEANUP_MOCK_APPLICATION = "cleanupMockApplication";

    String METHOD_SETUP_CLASS_UNDER_TEST = "setupClassUnderTest";

    String METHOD_CLEANUP_CLASS_UNDER_TEST = "cleanupClassUnderTest";

    String getVariableName();

    String getArtifactType();

    Statement getInitializingStatement(ClassExpression classExpr);

    boolean matches(ClassExpression classExpr);

    void inject(ClassNode classNode, ClassExpression classExpr);
}
