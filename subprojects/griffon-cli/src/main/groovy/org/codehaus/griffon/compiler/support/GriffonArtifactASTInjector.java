/*
 * Copyright 2010-2014 the original author or authors.
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

package org.codehaus.griffon.compiler.support;

import griffon.core.GriffonApplication;
import griffon.core.GriffonClass;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovySystem;
import org.codehaus.griffon.ast.MVCAwareASTTransformation;
import org.codehaus.griffon.ast.ResourcesAwareASTTransformation;
import org.codehaus.griffon.ast.ThreadingAwareASTTransformation;
import org.codehaus.griffon.runtime.core.AbstractGriffonArtifact;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class GriffonArtifactASTInjector extends AbstractASTInjector {
    private static final ClassNode GRIFFON_APPLICATION_TYPE = makeClassSafe(GriffonApplication.class);
    private static final ClassNode GRIFFON_CLASS_TYPE = makeClassSafe(GriffonClass.class);
    private static final ClassNode GAH_TYPE = makeClassSafe(GriffonApplicationHelper.class);
    private static final ClassNode LOGGER_TYPE = makeClassSafe(Logger.class);
    private static final ClassNode LOGGER_FACTORY_TYPE = makeClassSafe(LoggerFactory.class);
    private static final ClassNode GROOVY_SYSTEM_TYPE = makeClassSafe(GroovySystem.class);
    private static final ClassNode ABSTRACT_GRIFFON_ARTIFACT_TYPE = makeClassSafe(AbstractGriffonArtifact.class);
    private static final ClassNode EXPANDO_METACLASS_TYPE = makeClassSafe(ExpandoMetaClass.class);
    public static final String APP = "app";

    public void inject(ClassNode classNode, String artifactType) {
        // GriffonApplication getApp()
        // void setApp(GriffonApplication app)
        injectProperty(classNode, APP, ACC_PUBLIC, GRIFFON_APPLICATION_TYPE);

        FieldNode _metaClass = injectField(classNode,
                "_metaClass",
                ACC_PRIVATE | ACC_SYNTHETIC,
                METACLASS_TYPE,
                ConstantExpression.NULL);

        // MetaClass getMetaClass()
        injectMethod(classNode, new MethodNode(
                "getMetaClass",
                ACC_PUBLIC,
                METACLASS_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,

                /*
                if(_metaClass != null) return _metaClass
                MetaClass mc = null
                if(this instanceof GroovyObject)  mc = super.getMetaClass()
                if(mc instanceof ExpandoMetaClass) _metaClass = mc
                else _metaClass = AbstractGriffonArtifact.metaClassOf(this)
                return _metaClass
                */
                block(
                        ifs(
                                ne(field(_metaClass), ConstantExpression.NULL),
                                field(_metaClass)
                        ),
                        decls(var("mc", METACLASS_TYPE), ConstantExpression.NULL),
                        ifs_no_return(
                                iof(THIS, EXPANDO_METACLASS_TYPE),
                                assigns(field(_metaClass), var("mc")),
                                assigns(field(_metaClass), call(ABSTRACT_GRIFFON_ARTIFACT_TYPE, "metaClassOf", args(THIS)))
                        ),
                        returns(field(_metaClass))
                )
        ));

        // void setMetaClass(MetaClass mc)
        injectMethod(classNode, new MethodNode(
                "setMetaClass",
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(METACLASS_TYPE, "mc")),
                NO_EXCEPTIONS,
                block(
                        assigns(field(_metaClass), var("mc")),
                        stmnt(call(
                                call(GROOVY_SYSTEM_TYPE, "getMetaClassRegistry", NO_ARGS),
                                "setMetaClass",
                                args(call(THIS, "getClass", NO_ARGS), var("mc"))
                        ))
                )
        ));

        // GriffonClass getGriffonClass()
        injectMethod(classNode, new MethodNode(
                "getGriffonClass",
                ACC_PUBLIC,
                GRIFFON_CLASS_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(call(
                        call(
                                call(THIS, "getApp", NO_ARGS),
                                "getArtifactManager",
                                NO_ARGS),
                        "findGriffonClass",
                        args(call(THIS, "getClass", NO_ARGS))))
        ));

        // Object newInstance()
        injectMethod(classNode, new MethodNode(
                "newInstance",
                ACC_PUBLIC,
                makeClassSafe(OBJECT_TYPE),
                params(
                        param(makeClassSafe(CLASS_Type), "clazz"),
                        param(STRING_TYPE, "type")),
                NO_EXCEPTIONS,
                returns(call(
                        GAH_TYPE,
                        "newInstance",
                        vars(APP, "clazz", "type")))
        ));

        String loggerCategory = "griffon.app." + artifactType + "." + classNode.getName();
        FieldNode loggerField = injectField(classNode,
                "this$logger",
                ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
                LOGGER_TYPE,
                call(
                        LOGGER_FACTORY_TYPE,
                        "getLogger",
                        args(constx(loggerCategory)))
        );

        // Logger getLog()
        injectMethod(classNode, new MethodNode(
                "getLog",
                ACC_PUBLIC,
                LOGGER_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(field(loggerField))
        ));

        ThreadingAwareASTTransformation.apply(classNode);
        MVCAwareASTTransformation.apply(classNode);
        ResourcesAwareASTTransformation.apply(classNode);
    }
}
