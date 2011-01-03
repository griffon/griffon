/*
 * Copyright 2010-2011 the original author or authors.
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

import groovy.lang.GroovySystem;
import groovy.lang.ExpandoMetaClass;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

import griffon.core.GriffonApplication;
import griffon.core.GriffonClass;
import griffon.util.UIThreadHelper;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.codehaus.griffon.runtime.core.AbstractGriffonArtifact;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
public class GriffonArtifactASTInjector implements ASTInjector {
    private static final ClassNode GRIFFON_APPLICATION_CLASS = ClassHelper.makeWithoutCaching(GriffonApplication.class);
    private static final ClassNode GRIFFON_CLASS_CLASS = ClassHelper.makeWithoutCaching(GriffonClass.class);
    private static final ClassNode GAH_CLASS = ClassHelper.makeWithoutCaching(GriffonApplicationHelper.class);
    private static final ClassNode CALLABLE_CLASS = ClassHelper.makeWithoutCaching(Callable.class);
    private static final ClassNode FUTURE_CLASS = ClassHelper.makeWithoutCaching(Future.class);
    private static final ClassNode EXECUTOR_SERVICE_CLASS = ClassHelper.makeWithoutCaching(ExecutorService.class);
    private static final ClassNode UITH_CLASS = ClassHelper.makeWithoutCaching(UIThreadHelper.class);
    private static final ClassNode RUNNABLE_CLASS = ClassHelper.makeWithoutCaching(Runnable.class);
    private static final ClassNode LOGGER_CLASS = ClassHelper.makeWithoutCaching(Logger.class);
    private static final ClassNode LOGGER_FACTORY_CLASS = ClassHelper.makeWithoutCaching(LoggerFactory.class);
    private static final ClassNode GROOVY_SYSTEM_CLASS = ClassHelper.makeWithoutCaching(GroovySystem.class);
    private static final ClassNode ABSTRACT_GRIFFON_ARTIFACT_CLASS = ClassHelper.makeWithoutCaching(AbstractGriffonArtifact.class);
    private static final ClassNode EXPANDO_METACLASS_CLASS = ClassHelper.makeWithoutCaching(ExpandoMetaClass.class);
    public static final String APP = "app";
    
    public void inject(ClassNode classNode, String artifactType) {
        // GriffonApplication getApp()
        // void setApp(GriffonApplication app)
        classNode.addProperty(APP, ACC_PUBLIC, GRIFFON_APPLICATION_CLASS, null, null, null);

        FieldNode _metaClass = classNode.addField(
            "_metaClass",
            ACC_PRIVATE | ACC_SYNTHETIC,
            ClassHelper.METACLASS_TYPE,
            ConstantExpression.NULL);

        // MetaClass getMetaClass()
        classNode.addMethod(new MethodNode(
            "getMetaClass",
            ACC_PUBLIC,
            ClassHelper.METACLASS_TYPE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            
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
                decls(var("mc", ClassHelper.METACLASS_TYPE), ConstantExpression.NULL),
                ifs_no_return(
                    iof(THIS, EXPANDO_METACLASS_CLASS),
                    assigns(field(_metaClass), var("mc")),
                    assigns(field(_metaClass), call(ABSTRACT_GRIFFON_ARTIFACT_CLASS, "metaClassOf", args(THIS)))
                ),
                returns(field(_metaClass))
            )
        ));
        
        // void setMetaClass(MetaClass mc)
        classNode.addMethod(new MethodNode(
            "setMetaClass",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            params(param(ClassHelper.METACLASS_TYPE, "mc")),
            ClassNode.EMPTY_ARRAY,
            block(
                assigns(field(_metaClass), var("mc")),
                stmnt(call(
                    call(GROOVY_SYSTEM_CLASS, "getMetaClassRegistry", NO_ARGS),
                    "setMetaClass",
                    args(call(THIS, "getClass", NO_ARGS), var("mc"))
                ))
            )
        ));
        
        // GriffonClass getGriffonClass()
        classNode.addMethod(new MethodNode(
            "getGriffonClass",
            ACC_PUBLIC,
            GRIFFON_CLASS_CLASS,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returns(call(
                call(
                    call(THIS, "getApp", NO_ARGS),
                    "getArtifactManager",
                    NO_ARGS),
                "findGriffonClass",
                args(classx(classNode))))
        ));
    
        // Object newInstance()
        classNode.addMethod(new MethodNode(
            "newInstance",
            ACC_PUBLIC,
            ClassHelper.OBJECT_TYPE,
            params(
                param(ClassHelper.CLASS_Type, "clazz"),
                param(ClassHelper.STRING_TYPE, "type")),
            ClassNode.EMPTY_ARRAY,
            returns(call(
                GAH_CLASS,
                "newInstance",
                vars(APP, "clazz", "type")))
        ));
    
        // boolean isUIThread()
        classNode.addMethod(new MethodNode(
            "isUIThread",
            ACC_PUBLIC,
            ClassHelper.boolean_TYPE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returns(call(
                uiThreadHelperInstance(),
                "isUIThread",
                NO_ARGS))
        ));

        // void execAsync(Runnable)
        classNode.addMethod(new MethodNode(
            "execAsync",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            params(param(RUNNABLE_CLASS, "runnable")),
            ClassNode.EMPTY_ARRAY,
            stmnt(call(
                uiThreadHelperInstance(),
                "executeAsync",
                vars("runnable")))
        ));
    
        // void execSync(Runnable)
        classNode.addMethod(new MethodNode(
            "execSync",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            params(param(RUNNABLE_CLASS, "runnable")),
            ClassNode.EMPTY_ARRAY,
            stmnt(call(
                uiThreadHelperInstance(),
                "executeSync",
                vars("runnable")))
        ));
    
        // void execOutside(Runnable)
        classNode.addMethod(new MethodNode(
            "execOutside",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            params(param(RUNNABLE_CLASS, "runnable")),
            ClassNode.EMPTY_ARRAY,
            stmnt(call(
                uiThreadHelperInstance(),
                "executeOutside",
                vars("runnable")))
        ));
    
        // Future execFuture(Runnable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            params(param(ClassHelper.CLOSURE_TYPE, "closure")),
            ClassNode.EMPTY_ARRAY,
            returns(call(
                uiThreadHelperInstance(),
                "executeFuture",
                vars("closure")))
        ));
    
        // Future execFuture(ExecutorService, Closure)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            params(
                param(EXECUTOR_SERVICE_CLASS, "executorService"),
                param(ClassHelper.CLOSURE_TYPE, "closure")),
            ClassNode.EMPTY_ARRAY,
            returns(call(
                uiThreadHelperInstance(),
                "executeFuture",
                vars("executorService", "closure")))
        ));
    
        // Future execFuture(Callable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            params(param(CALLABLE_CLASS, "callable")),
            ClassNode.EMPTY_ARRAY,
            returns(call(
                uiThreadHelperInstance(),
                "executeFuture",
                vars("callable")))
        ));
    
        // Future execFuture(ExecutorService, Callable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            params(
                param(EXECUTOR_SERVICE_CLASS, "executorService"),
                param(CALLABLE_CLASS, "callable")),
            ClassNode.EMPTY_ARRAY,
            returns(call(
                uiThreadHelperInstance(),
                "executeFuture",
                vars("executorService", "callable")))
        ));
        
        String loggerCategory = "griffon.app." + artifactType +"."+ classNode.getName();
        FieldNode loggerField = classNode.addField(
            "this$logger",
            ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
            LOGGER_CLASS,
            call(
                LOGGER_FACTORY_CLASS,
                "getLogger",
                args(constx(loggerCategory)))
        );
        
        // Logger getLog()
        classNode.addMethod(new MethodNode(
            "getLog",
            ACC_PUBLIC,
            LOGGER_CLASS,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returns(field(loggerField))
        ));
    }

    private Expression uiThreadHelperInstance() {
        return new StaticMethodCallExpression(
                   UITH_CLASS,
                   "getInstance",
                   NO_ARGS);
    }
}