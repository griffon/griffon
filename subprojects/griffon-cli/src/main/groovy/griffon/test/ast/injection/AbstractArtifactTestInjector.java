/*
 * Copyright 2007-2013 the original author or authors.
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

import griffon.core.ArtifactInfo;
import griffon.core.ArtifactManager;
import griffon.core.GriffonApplication;
import griffon.core.factories.ArtifactManagerFactory;
import griffon.test.mock.MockGriffonApplication;
import org.codehaus.griffon.runtime.core.DefaultArtifactManager;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import static java.lang.reflect.Modifier.*;
import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * @author Andres Almiray
 * @since 1.5.0
 */
public abstract class AbstractArtifactTestInjector implements ArtifactTestInjector {
    private static final ClassNode ARTIFACT_MANAGER_FACTORY_TYPE = new ClassNode(ArtifactManagerFactory.class);
    private static final ClassNode ARTIFACT_MANAGER_TYPE = new ClassNode(ArtifactManager.class);
    private static final ClassNode DEFAULT_ARTIFACT_MANAGER_TYPE = new ClassNode(DefaultArtifactManager.class);
    private static final ClassNode ARTIFACT_INFO_TYPE = new ClassNode(ArtifactInfo.class);
    private static final ClassNode GRIFFON_APPLICATION_TYPE = new ClassNode(GriffonApplication.class);
    private static final ClassNode MOCK_GRIFFON_APPLICATION_TYPE = new ClassNode(MockGriffonApplication.class);

    public boolean matches(ClassExpression classExpr) {
        return classExpr.getType().getName().endsWith(getArtifactType());
    }

    public Statement getInitializingStatement(ClassExpression classExpr) {
        return stmnt(assign(prop(THIS, getVariableName()),
            call(
                prop(THIS, APP),
                "newInstance",
                args(classExpr, constx(getVariableName()))
            )));
    }

    public void inject(ClassNode classNode, ClassExpression classExpr) {
        String variableName = getVariableName();
        classNode.addProperty(
            variableName,
            PUBLIC,
            classExpr.getType(),
            null,
            null,
            null
        );
        classNode.addProperty(
            APP,
            PUBLIC,
            GRIFFON_APPLICATION_TYPE,
            null,
            null,
            null
        );

        String artifactManagerClassName = classNode.getName() + "$ArtifactManager";
        InnerClassNode artifactManagerClassNode = new InnerClassNode(
            classNode,
            artifactManagerClassName,
            PUBLIC | STATIC,
            DEFAULT_ARTIFACT_MANAGER_TYPE
        );
        classNode.getModule().addClass(artifactManagerClassNode);

        artifactManagerClassNode.addConstructor(
            PUBLIC,
            params(param(GRIFFON_APPLICATION_TYPE, APP)),
            NO_EXCEPTIONS,
            block(
                stmnt(ctor(ClassNode.SUPER, vars(APP)))
            )
        );

        MethodNode loadMethod = artifactManagerClassNode.getMethod("doLoadArtifactMetadata", NO_PARAMS);
        artifactManagerClassNode.addMethod(
            loadMethod.getName(),
            loadMethod.getModifiers(),
            loadMethod.getReturnType(),
            loadMethod.getParameters(),
            loadMethod.getExceptions(),
            returns(
                mapx(mapEntryx(
                    constx(variableName),
                    listx(ctor(ARTIFACT_INFO_TYPE,
                        args(classExpr, constx(variableName))))
                ))
            )
        );

        String artifactManagerFactoryClassName = classNode.getName() + "$ArtifactManagerFactory";
        InnerClassNode artifactManagerFactoryClassNode = new InnerClassNode(
            classNode,
            artifactManagerFactoryClassName,
            PUBLIC | STATIC,
            GROOVY_OBJECT_SUPPORT_TYPE
        );
        classNode.getModule().addClass(artifactManagerFactoryClassNode);
        artifactManagerFactoryClassNode.addInterface(ARTIFACT_MANAGER_FACTORY_TYPE);
        artifactManagerFactoryClassNode.addMethod(
            "create",
            PUBLIC,
            ARTIFACT_MANAGER_TYPE,
            params(param(GRIFFON_APPLICATION_TYPE, APP)),
            NO_EXCEPTIONS,
            returns(
                ctor(artifactManagerClassNode, args(var(APP)))
            )
        );

        String configClassName = classNode.getName() + "$Config";
        InnerClassNode configClassNode = new InnerClassNode(
            classNode,
            configClassName,
            PUBLIC | STATIC,
            SCRIPT_TYPE
        );
        classNode.getModule().addClass(configClassNode);
        configClassNode.addMethod(
            "run",
            PUBLIC,
            OBJECT_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            block(
                stmnt(
                    assign(
                        prop(prop(prop(THIS, APP), constx("artifactManager")), "factory"),
                        constx(artifactManagerFactoryClassName)
                    ))
            )
        );

        if (null == classNode.getMethod(METHOD_CREATE_MOCK_APPLICATION, NO_PARAMS)) {
            classNode.addMethod(
                METHOD_CREATE_MOCK_APPLICATION,
                PROTECTED,
                GRIFFON_APPLICATION_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(
                    ctor(MOCK_GRIFFON_APPLICATION_TYPE, NO_ARGS)
                )
            );
        }

        if (null == classNode.getMethod(METHOD_CONFIG_MOCK_APPLICATION, NO_PARAMS)) {
            classNode.addMethod(
                METHOD_CONFIG_MOCK_APPLICATION,
                PROTECTED,
                VOID_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                new EmptyStatement()
            );
        }

        if (null == classNode.getMethod(METHOD_SETUP_CLASS_UNDER_TEST, NO_PARAMS)) {
            classNode.addMethod(
                METHOD_SETUP_CLASS_UNDER_TEST,
                PROTECTED,
                VOID_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                new EmptyStatement()
            );
        }

        if (null == classNode.getMethod(METHOD_CLEANUP_CLASS_UNDER_TEST, NO_PARAMS)) {
            classNode.addMethod(
                METHOD_CLEANUP_CLASS_UNDER_TEST,
                PROTECTED,
                VOID_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                new EmptyStatement()
            );
        }

        classNode.addMethod(
            METHOD_SETUP_MOCK_APPLICATION,
            PRIVATE,
            VOID_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            block(
                stmnt(assign(prop(THIS, APP), call(THIS, METHOD_CREATE_MOCK_APPLICATION, NO_ARGS))),
                stmnt(call(THIS, METHOD_CONFIG_MOCK_APPLICATION, NO_ARGS)),
                stmnt(call(prop(THIS, APP), "setConfigClass", args(classx(configClassNode)))),
                stmnt(call(prop(THIS, APP), "initialize", NO_ARGS)),
                getInitializingStatement(classExpr)
            )
        );

        classNode.addMethod(
            METHOD_CLEANUP_MOCK_APPLICATION,
            PRIVATE,
            VOID_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            stmnt(call(prop(THIS, APP), "shutdown", NO_ARGS))
        );
    }
}
