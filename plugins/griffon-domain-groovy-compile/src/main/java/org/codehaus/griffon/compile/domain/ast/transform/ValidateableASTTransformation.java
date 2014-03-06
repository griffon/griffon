/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.compile.domain.ast.transform;

import griffon.plugins.validation.Errors;
import griffon.plugins.validation.Validateable;
import griffon.plugins.validation.constraints.ConstraintUtils;
import griffon.plugins.validation.constraints.ConstraintsEvaluator;
import griffon.plugins.validation.constraints.ConstraintsValidator;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.runtime.validation.DefaultErrors;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.LIST_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;

/**
 * @author Andres Almiray
 */
@AnnotationHandlerFor(griffon.transform.Validateable.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ValidateableASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateableASTTransformation.class);
    private static final ClassNode VALIDATEABLE_TYPE = makeClassSafe(Validateable.class);
    private static final ClassNode VALIDATEABLE_ANNOTATION = makeClassSafe(griffon.transform.Validateable.class);
    private static final ClassNode ERRORS_TYPE = makeClassSafe(Errors.class);
    private static final ClassNode DEFAULT_ERRORS_TYPE = makeClassSafe(DefaultErrors.class);
    private static final ClassNode CONSTRAINTS_VALIDATOR_TYPE = makeClassSafe(ConstraintsValidator.class);
    private static final ClassNode CONSTRAINTS_EVALUATOR_TYPE = makeClassSafe(ConstraintsEvaluator.class);
    private static final ClassNode CONSTRAINT_UTILS_TYPE = makeClassSafe(ConstraintUtils.class);

    private static final String VALIDATE = "validate";
    private static final String GET_ERRORS = "getErrors";
    private static final String GET_CLASS = "getClass";
    private static final String PROPERTIES = "properties";
    private static final String EVALUATE = "evaluate";
    private static final String CONSTRAINED_PROPERTIES = "constrainedProperties";

    /**
     * Convenience method to see if an annotated node is {@code @Validateable}.
     *
     * @param node the node to check
     * @return true if the node is annotated with @Validateable
     */
    public static boolean hasValidateableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (VALIDATEABLE_ANNOTATION.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addValidateableToClass(source, (ClassNode) nodes[1]);
    }

    public static void addValidateableToClass(SourceUnit source, ClassNode classNode) {
        if (needsValidateable(classNode, source)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + Validateable.class.getName() + " into " + classNode.getName());
            }
            addValidateable(classNode, source);
        }
    }

    /**
     * Snoops through the declaring class and all parents looking for methods<ul>
     * <li>boolean validate()</li>
     * <li>Errors getErrors()</li>
     * </ul>If any are defined all
     * must be defined or a compilation error results.
     *
     * @param declaringClass the class to search
     * @param sourceUnit     the source unit, for error reporting. {@code @NotNull}.
     * @return true if property change support should be added
     */
    protected static boolean needsValidateable(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean foundValidate = false, foundGetErrors = false, foundConstrainedProperties = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                foundValidate = foundValidate || method.getName().equals(VALIDATE) && method.getParameters().length == 0;
                foundGetErrors = foundGetErrors || method.getName().equals(GET_ERRORS) && method.getParameters().length == 0;
                foundConstrainedProperties = foundConstrainedProperties || method.getName().equals(CONSTRAINED_PROPERTIES) && method.getParameters().length == 0;
                if (foundValidate && foundGetErrors && foundConstrainedProperties) {
                    return false;
                }
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (foundValidate || foundGetErrors || foundConstrainedProperties) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                new SimpleMessage("@Validateable cannot be processed on "
                    + declaringClass.getName()
                    + " because some but not all of validate, getErrors and constrainedProperties were declared in the current class or super classes.",
                    sourceUnit)
            );
            return false;
        }
        return true;
    }

    public static void addValidateable(ClassNode declaringClass, SourceUnit source) {
        injectInterface(declaringClass, VALIDATEABLE_TYPE);
        addValidatableBehavior(declaringClass, source);
    }

    public static void addValidatableBehavior(ClassNode declaringClass, SourceUnit source) {
        // add field:
        // protected final Errors this$errors = new org.codehaus.griffon.runtime.validation.DefaultErrors(getClass())
        FieldNode errorsField = declaringClass.addField(
            "this$errors",
            FINAL | PROTECTED,
            ERRORS_TYPE,
            ctor(DEFAULT_ERRORS_TYPE, call(THIS, GET_CLASS, NO_ARGS)));

        // add method:
        // boolean validate(String... properties) {
        //      return ConstraintsValidator.evaluate(this, properties);
        // }
        injectMethod(declaringClass, new MethodNode(
            VALIDATE,
            PUBLIC,
            ClassHelper.boolean_TYPE,
            params(param(makeClassSafe(STRING_TYPE).makeArray(), PROPERTIES)),
            ClassNode.EMPTY_ARRAY,
            returns(call(CONSTRAINTS_VALIDATOR_TYPE, EVALUATE, args(THIS, var(PROPERTIES))))
        ));

        // add method:
        // boolean validate(List<String> properties) {
        //      return ConstraintsValidator.evaluate(this, properties);
        // }
        injectMethod(declaringClass, new MethodNode(
            VALIDATE,
            PUBLIC,
            ClassHelper.boolean_TYPE,
            params(param(makeClassSafe(LIST_TYPE), PROPERTIES)),
            ClassNode.EMPTY_ARRAY,
            returns(call(CONSTRAINTS_VALIDATOR_TYPE, EVALUATE, args(THIS, var(PROPERTIES))))
        ));

        // add method:
        // Errors getErrors() {
        //      return $this$errors;
        // }
        injectMethod(declaringClass, new MethodNode(
            GET_ERRORS,
            PUBLIC,
            ERRORS_TYPE,
            params(),
            ClassNode.EMPTY_ARRAY,
            returns(field(errorsField))
        ));

        // add field:
        // protected final Map<String, ConstrainedProperty> this$constrainedProperties = new LinkedHashMap()
        FieldNode constrainedPropertiesField = declaringClass.addField(
            "this$constrainedProperties",
            FINAL | PROTECTED,
            makeClassSafe(ClassHelper.MAP_TYPE),
            ctor(makeClassSafe(LinkedHashMap.class), NO_ARGS));

        // add method:
        // Map<String, ConstrainedProperty> constrainedProperties() {
        //      return this$constrainedProperties;
        // }
        injectMethod(declaringClass, new MethodNode(
            CONSTRAINED_PROPERTIES,
            PUBLIC,
            makeClassSafe(ClassHelper.MAP_TYPE),
            params(),
            ClassNode.EMPTY_ARRAY,
            returns(field(constrainedPropertiesField))
        ));



        //ConstraintsEvaluator constraintsEvaluator = application.getInjector().getInstance(ConstraintsEvaluator.class);
        //ConstraintsEvaluator constraintsEvaluator = ConstraintUtils.getConstraintsEvaluator(ApplicationHolder.getApplication());
        declaringClass.addObjectInitializerStatements(decls(
            var("constraintsEvaluator", CONSTRAINTS_EVALUATOR_TYPE),
            call(CONSTRAINT_UTILS_TYPE, "getConstraintsEvaluator", args(null))
        ));
        //constrainedProperties.putAll(constraintsEvaluator.evaluate(getClass()));
        declaringClass.addObjectInitializerStatements(stmnt(
            call(
                field(constrainedPropertiesField),
                "putAll",
                args(call(
                    var("constraintsEvaluator"),
                    EVALUATE,
                    args(call(THIS, GET_CLASS, NO_ARGS)))))
        ));
    }
}
