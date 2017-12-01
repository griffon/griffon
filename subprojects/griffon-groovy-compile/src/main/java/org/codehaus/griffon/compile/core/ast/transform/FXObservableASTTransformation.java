/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.transform;

import griffon.transform.FXObservable;
import griffon.util.GriffonNameUtils;
import groovyjarjarasm.asm.Opcodes;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.codehaus.groovy.ast.ClassHelper.LIST_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.MAP_TYPE;


/**
 * Handles generation of code for the {@code @FXObservable}
 * <p>
 * Generally, it adds (if needed) a javafx.beans.property.Property type
 * <p>
 * It also generates the setter and getter and wires the them through the
 * javafx.beans.property.Property.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(FXObservable.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class FXObservableASTTransformation extends AbstractASTTransformation implements AnnotationHandler {
    private static final ClassNode FXOBSERVABLE_CNODE = makeClassSafe(FXObservable.class);

    private static final ClassNode OBJECT_PROPERTY_CNODE = makeClassSafe(ObjectProperty.class);
    private static final ClassNode BOOLEAN_PROPERTY_CNODE = makeClassSafe(BooleanProperty.class);
    private static final ClassNode DOUBLE_PROPERTY_CNODE = makeClassSafe(DoubleProperty.class);
    private static final ClassNode FLOAT_PROPERTY_CNODE = makeClassSafe(FloatProperty.class);
    private static final ClassNode INT_PROPERTY_CNODE = makeClassSafe(IntegerProperty.class);
    private static final ClassNode LONG_PROPERTY_CNODE = makeClassSafe(LongProperty.class);
    private static final ClassNode STRING_PROPERTY_CNODE = makeClassSafe(StringProperty.class);
    private static final ClassNode LIST_PROPERTY_CNODE = makeClassSafe(ListProperty.class);
    private static final ClassNode MAP_PROPERTY_CNODE = makeClassSafe(MapProperty.class);
    private static final ClassNode SET_PROPERTY_CNODE = makeClassSafe(SetProperty.class);

    private static final ClassNode SIMPLE_BOOLEAN_PROPERTY_CNODE = makeClassSafe(SimpleBooleanProperty.class);
    private static final ClassNode SIMPLE_DOUBLE_PROPERTY_CNODE = makeClassSafe(SimpleDoubleProperty.class);
    private static final ClassNode SIMPLE_FLOAT_PROPERTY_CNODE = makeClassSafe(SimpleFloatProperty.class);
    private static final ClassNode SIMPLE_INT_PROPERTY_CNODE = makeClassSafe(SimpleIntegerProperty.class);
    private static final ClassNode SIMPLE_LONG_PROPERTY_CNODE = makeClassSafe(SimpleLongProperty.class);
    private static final ClassNode SIMPLE_STRING_PROPERTY_CNODE = makeClassSafe(SimpleStringProperty.class);
    private static final ClassNode SIMPLE_LIST_PROPERTY_CNODE = makeClassSafe(SimpleListProperty.class);
    private static final ClassNode SIMPLE_MAP_PROPERTY_CNODE = makeClassSafe(SimpleMapProperty.class);
    private static final ClassNode SIMPLE_SET_PROPERTY_CNODE = makeClassSafe(SimpleSetProperty.class);
    private static final ClassNode SIMPLE_OBJECT_PROPERTY_CNODE = makeClassSafe(SimpleObjectProperty.class);

    private static final ClassNode OBSERVABLE_LIST_CNODE = makeClassSafe(ObservableList.class);
    private static final ClassNode OBSERVABLE_MAP_CNODE = makeClassSafe(ObservableMap.class);
    private static final ClassNode OBSERVABLE_SET_CNODE = makeClassSafe(ObservableSet.class);
    private static final ClassNode FXCOLLECTIONS_CNODE = makeClassSafe(FXCollections.class);
    private static final ClassNode SET_TYPE = makeClassSafe(Set.class);

    private static final Map<ClassNode, ClassNode> PROPERTY_TYPE_MAP = new HashMap<>();

    static {
        PROPERTY_TYPE_MAP.put(ClassHelper.STRING_TYPE, STRING_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.boolean_TYPE, BOOLEAN_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Boolean_TYPE, BOOLEAN_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.double_TYPE, DOUBLE_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Double_TYPE, DOUBLE_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.float_TYPE, FLOAT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Float_TYPE, FLOAT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.int_TYPE, INT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Integer_TYPE, INT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.long_TYPE, LONG_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Long_TYPE, LONG_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.short_TYPE, INT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Short_TYPE, INT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.byte_TYPE, INT_PROPERTY_CNODE);
        PROPERTY_TYPE_MAP.put(ClassHelper.Byte_TYPE, INT_PROPERTY_CNODE);
        //PROPERTY_TYPE_MAP.put(ClassHelper.char_TYPE, INT_PROPERTY_CNODE);
        //PROPERTY_TYPE_MAP.put(ClassHelper.Character_TYPE, INT_PROPERTY_CNODE);
    }

    private static final Map<ClassNode, ClassNode> PROPERTY_IMPL_MAP = new HashMap<ClassNode, ClassNode>();

    static {
        PROPERTY_IMPL_MAP.put(BOOLEAN_PROPERTY_CNODE, SIMPLE_BOOLEAN_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(DOUBLE_PROPERTY_CNODE, SIMPLE_DOUBLE_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(FLOAT_PROPERTY_CNODE, SIMPLE_FLOAT_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(INT_PROPERTY_CNODE, SIMPLE_INT_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(LONG_PROPERTY_CNODE, SIMPLE_LONG_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(STRING_PROPERTY_CNODE, SIMPLE_STRING_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(LIST_PROPERTY_CNODE, SIMPLE_LIST_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(MAP_PROPERTY_CNODE, SIMPLE_MAP_PROPERTY_CNODE);
        PROPERTY_IMPL_MAP.put(SET_PROPERTY_CNODE, SIMPLE_SET_PROPERTY_CNODE);
        //PROPERTY_IMPL_MAP.put(OBJECT_PROPERTY_CNODE, SIMPLE_OBJECT_PROPERTY_CNODE);
    }

    /**
     * Convenience method to see if an annotated node is {@code @FXObservable}.
     *
     * @param node the node to check
     *
     * @return true if the node is observable
     */
    public static boolean hasFXObservableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (FXOBSERVABLE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This ASTTransformation method is called when the compiler encounters our annotation.
     *
     * @param nodes      An array of nodes.  Index 0 is the annotation that triggered the call, index 1
     *                   is the annotated node.
     * @param sourceUnit The SourceUnit describing the source code in which the annotation was placed.
     */
    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new IllegalArgumentException("Internal error: wrong types: "
                + nodes[0].getClass().getName() + " / " + nodes[1].getClass().getName());
        }

        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];
        ClassNode declaringClass = parent.getDeclaringClass();

        if (parent instanceof FieldNode) {
            int modifiers = ((FieldNode) parent).getModifiers();
            if ((modifiers & Modifier.FINAL) != 0) {
                String msg = "@griffon.transform.FXObservable cannot annotate a final property.";
                generateSyntaxErrorMessage(sourceUnit, node, msg);
            }
            addJavaFXProperty(sourceUnit, node, declaringClass, (FieldNode) parent);
        } else {
            addJavaFXPropertyToClass(sourceUnit, node, (ClassNode) parent);
        }
    }

    /**
     * Adds a JavaFX property to the class in place of the original Groovy property.  A pair of synthetic
     * getter/setter methods is generated to provide pseudo-access to the original property.
     *
     * @param source         The SourceUnit in which the annotation was found
     * @param node           The node that was annotated
     * @param declaringClass The class in which the annotation was found
     * @param field          The field upon which the annotation was placed
     */
    private void addJavaFXProperty(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field) {
        String fieldName = field.getName();
        for (PropertyNode propertyNode : declaringClass.getProperties()) {
            if (propertyNode.getName().equals(fieldName)) {
                if (field.isStatic()) {
                    String message = "@griffon.transform.FXObservable cannot annotate a static property.";
                    generateSyntaxErrorMessage(source, node, message);
                } else {
                    createPropertyGetterSetter(declaringClass, propertyNode);
                }
                return;
            }
        }

        String message = "@griffon.transform.FXObservable must be on a property, not a field. Try removing the private, " +
            "protected, or public modifier.";
        generateSyntaxErrorMessage(source, node, message);
    }

    /**
     * Iterate through the properties of the class and convert each eligible property to a JavaFX property.
     *
     * @param source    The SourceUnit
     * @param node      The AnnotationNode
     * @param classNode The declaring class
     */
    private void addJavaFXPropertyToClass(SourceUnit source, AnnotationNode node, ClassNode classNode) {
        for (PropertyNode propertyNode : classNode.getProperties()) {
            FieldNode field = propertyNode.getField();
            // look to see if per-field handlers will catch this one...
            if (hasFXObservableAnnotation(field)
                || ((field.getModifiers() & Modifier.FINAL) != 0)
                || field.isStatic()) {
                // explicitly labeled properties are already handled,
                // don't transform final properties
                // don't transform static properties
                continue;
            }
            createPropertyGetterSetter(classNode, propertyNode);
        }
    }

    /**
     * Creates the JavaFX property and three methods for accessing the property and a pair of
     * getter/setter methods for accessing the original (now synthetic) Groovy property.  For
     * example, if the original property was "String firstName" then these three methods would
     * be generated:
     * <p>
     * public String getFirstName()
     * public void setFirstName(String value)
     * public StringProperty firstNameProperty()
     *
     * @param classNode    The declaring class in which the property will appear
     * @param originalProp The original Groovy property
     */
    private void createPropertyGetterSetter(ClassNode classNode, PropertyNode originalProp) {
        Expression initExp = originalProp.getInitialExpression();
        originalProp.getField().setInitialValueExpression(null);

        PropertyNode fxProperty = createFXProperty(originalProp);

        List<AnnotationNode> methodAnnotations = new ArrayList<>();
        List<AnnotationNode> fieldAnnotations = new ArrayList<>();
        for (AnnotationNode annotation : originalProp.getField().getAnnotations()) {
            if (FXOBSERVABLE_CNODE.equals(annotation.getClassNode())) { continue; }
            Class annotationClass = annotation.getClassNode().getTypeClass();
            Target target = (Target) annotationClass.getAnnotation(Target.class);
            if (isTargetAllowed(target, ElementType.METHOD)) {
                methodAnnotations.add(annotation);
            } else if (isTargetAllowed(target, ElementType.FIELD)) {
                fieldAnnotations.add(annotation);
            }
        }

        String getterName = "get" + MetaClassHelper.capitalize(originalProp.getName());
        if (classNode.getMethods(getterName).isEmpty()) {
            Statement getterBlock = createGetterStatement(createFXProperty(originalProp));
            createGetterMethod(classNode, originalProp, getterName, getterBlock, methodAnnotations);
            methodAnnotations = null;
        } else {
            wrapGetterMethod(classNode, originalProp.getName(), methodAnnotations);
            methodAnnotations = null;
        }

        String setterName = "set" + MetaClassHelper.capitalize(originalProp.getName());
        if (classNode.getMethods(setterName).isEmpty()) {
            Statement setterBlock = createSetterStatement(createFXProperty(originalProp));
            createSetterMethod(classNode, originalProp, setterName, setterBlock, methodAnnotations);
        } else {
            wrapSetterMethod(classNode, originalProp.getName(), methodAnnotations);
        }

        // We want the actual name of the field to be different from the getter (Prop vs Property) so
        // that the getter takes precedence when we say this.somethingProperty.
        FieldNode fxFieldShortName = createFieldNodeCopy(originalProp.getName() + "Prop", null, fxProperty.getField());
        createPropertyAccessor(classNode, createFXProperty(originalProp), fxFieldShortName, initExp);

        classNode.removeField(originalProp.getName());
        classNode.addField(fxFieldShortName);
        fxFieldShortName.addAnnotations(fieldAnnotations);
    }

    private boolean isTargetAllowed(Target target, ElementType elementType) {
        if (target == null) {
            return false;
        }
        for (ElementType et : target.value()) {
            if (et == elementType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new PropertyNode for the JavaFX property based on the original property.  The new property
     * will have "Property" appended to its name and its type will be one of the *Property types in JavaFX.
     *
     * @param orig The original property
     *
     * @return A new PropertyNode for the JavaFX property
     */
    private PropertyNode createFXProperty(PropertyNode orig) {
        ClassNode origType = orig.getType();
        ClassNode newType = PROPERTY_TYPE_MAP.get(origType);

        // For the ObjectProperty, we need to add the generic type to it.
        if (newType == null) {
            if (isTypeCompatible(ClassHelper.LIST_TYPE, origType) || isTypeCompatible(OBSERVABLE_LIST_CNODE, origType)) {
                newType = makeClassSafe(SIMPLE_LIST_PROPERTY_CNODE);
                GenericsType[] genericTypes = origType.getGenericsTypes();
                newType.setGenericsTypes(genericTypes);
            } else if (isTypeCompatible(ClassHelper.MAP_TYPE, origType) || isTypeCompatible(OBSERVABLE_MAP_CNODE, origType)) {
                newType = makeClassSafe(SIMPLE_MAP_PROPERTY_CNODE);
                GenericsType[] genericTypes = origType.getGenericsTypes();
                newType.setGenericsTypes(genericTypes);
            } else if (isTypeCompatible(SET_TYPE, origType) || isTypeCompatible(OBSERVABLE_SET_CNODE, origType)) {
                newType = makeClassSafe(SIMPLE_SET_PROPERTY_CNODE);
                GenericsType[] genericTypes = origType.getGenericsTypes();
                newType.setGenericsTypes(genericTypes);
            } else { // Object Type
                newType = makeClassSafe(OBJECT_PROPERTY_CNODE);
                ClassNode genericType = origType;
                if (genericType.isPrimaryClassNode()) {
                    genericType = ClassHelper.getWrapper(genericType);
                }
                newType.setGenericsTypes(new GenericsType[]{new GenericsType(genericType)});
            }
        }

        FieldNode fieldNode = createFieldNodeCopy(orig.getName() + "Property", newType, orig.getField());
        return new PropertyNode(fieldNode, orig.getModifiers(), orig.getGetterBlock(), orig.getSetterBlock());
    }

    private boolean isTypeCompatible(ClassNode base, ClassNode target) {
        return target.equals(base) || target.implementsInterface(base) || target.declaresInterface(base);
    }

    /**
     * Creates a setter method and adds it to the declaring class.  The setter has the form:
     * <p>
     * void <setter>(<type> fieldName)
     *
     * @param declaringClass The class to which the method is added
     * @param propertyNode   The property node being accessed by this setter
     * @param setterName     The name of the setter method
     * @param setterBlock    The code body of the method
     */
    protected void createSetterMethod(ClassNode declaringClass, PropertyNode propertyNode, String setterName,
                                      Statement setterBlock, List<AnnotationNode> annotations) {
        Parameter[] setterParameterTypes = {new Parameter(propertyNode.getType(), "value")};
        int mod = propertyNode.getModifiers() | Opcodes.ACC_FINAL;

        MethodNode setter = new MethodNode(setterName, mod, ClassHelper.VOID_TYPE, setterParameterTypes,
            ClassNode.EMPTY_ARRAY, setterBlock);
        if (annotations != null) { setter.addAnnotations(annotations); }
        setter.setSynthetic(true);
        declaringClass.addMethod(setter);
    }


    /**
     * If the setter already exists, this method should wrap it with our code and then a call to the original
     * setter.
     * <p>
     * TODO: Not implemented yet
     *
     * @param classNode    The declaring class to which the method will be added
     * @param propertyName The name of the original Groovy property
     */
    private void wrapSetterMethod(ClassNode classNode, String propertyName, List<AnnotationNode> annotations) {
        System.out.println(
            String.format("wrapSetterMethod for '%s', property '%s' not yet implemented",
                classNode.getName(), propertyName));
    }

    /**
     * Creates a getter method and adds it to the declaring class.
     *
     * @param declaringClass The class to which the method is added
     * @param propertyNode   The property node being accessed by this getter
     * @param getterName     The name of the getter method
     * @param getterBlock    The code body of the method
     */
    protected void createGetterMethod(ClassNode declaringClass, PropertyNode propertyNode, String getterName,
                                      Statement getterBlock, List<AnnotationNode> annotations) {
        int mod = propertyNode.getModifiers() | Opcodes.ACC_FINAL;
        MethodNode getter = new MethodNode(getterName, mod, propertyNode.getType(), Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY, getterBlock);
        if (annotations != null) { getter.addAnnotations(annotations); }
        getter.setSynthetic(true);
        declaringClass.addMethod(getter);
    }

    /**
     * If the getter already exists, this method should wrap it with our code.
     * <p>
     * TODO: Not implemented yet -- what to do with the returned value from the original getter?
     *
     * @param classNode    The declaring class to which the method will be added
     * @param propertyName The name of the original Groovy property
     */
    private void wrapGetterMethod(ClassNode classNode, String propertyName, List<AnnotationNode> annotations) {
        System.out.println(
            String.format("wrapGetterMethod for '%s', property '%s' not yet implemented",
                classNode.getName(), propertyName));
    }


    /**
     * Creates the body of a property access method that returns the JavaFX *Property instance.  If
     * the original property was "String firstName" then the generated code would be:
     * <p>
     * if (firstNameProperty == null) {
     * firstNameProperty = new javafx.beans.property.StringProperty()
     * }
     * return firstNameProperty
     *
     * @param classNode        The declaring class to which the JavaFX property will be added
     * @param fxProperty       The new JavaFX property
     * @param fxFieldShortName
     * @param initExp          The initializer expression from the original Groovy property declaration
     */
    private void createPropertyAccessor(ClassNode classNode, PropertyNode fxProperty, FieldNode fxFieldShortName,
                                        Expression initExp) {
        FieldExpression fieldExpression = new FieldExpression(fxFieldShortName);

        ArgumentListExpression ctorArgs = initExp == null ?
            ArgumentListExpression.EMPTY_ARGUMENTS :
            new ArgumentListExpression(initExp);

        BlockStatement block = new BlockStatement();
        ClassNode fxType = fxProperty.getType();
        ClassNode implNode = PROPERTY_IMPL_MAP.get(fxType);
        if (implNode == null) {
            if (fxType.getTypeClass() == SIMPLE_LIST_PROPERTY_CNODE.getTypeClass()) {
                if (initExp != null && initExp instanceof ListExpression ||
                    (initExp instanceof CastExpression &&
                        (((CastExpression) initExp).getType().equals(LIST_TYPE) ||
                            ((CastExpression) initExp).getType().declaresInterface(LIST_TYPE))) ||
                    (initExp instanceof ConstructorCallExpression &&
                        (((ConstructorCallExpression) initExp).getType().equals(LIST_TYPE) ||
                            ((ConstructorCallExpression) initExp).getType().declaresInterface(LIST_TYPE)))
                    ) {
                    ctorArgs = new ArgumentListExpression(
                        new MethodCallExpression(
                            new ClassExpression(FXCOLLECTIONS_CNODE),
                            "observableList",
                            ctorArgs)
                    );
                }
                implNode = fxType;
            } else if (fxType.getTypeClass() == SIMPLE_MAP_PROPERTY_CNODE.getTypeClass()) {
                if (initExp != null && initExp instanceof MapExpression ||
                    (initExp instanceof CastExpression &&
                        (((CastExpression) initExp).getType().equals(MAP_TYPE) ||
                            ((CastExpression) initExp).getType().declaresInterface(MAP_TYPE))) ||
                    (initExp instanceof ConstructorCallExpression &&
                        (((ConstructorCallExpression) initExp).getType().equals(MAP_TYPE) ||
                            ((ConstructorCallExpression) initExp).getType().declaresInterface(MAP_TYPE)))
                    ) {
                    ctorArgs = new ArgumentListExpression(
                        new MethodCallExpression(
                            new ClassExpression(FXCOLLECTIONS_CNODE),
                            "observableMap",
                            ctorArgs)
                    );
                }
                implNode = fxType;
            } else if (fxType.getTypeClass() == SIMPLE_SET_PROPERTY_CNODE.getTypeClass()) {
                if (initExp != null && (initExp instanceof CastExpression &&
                    (((CastExpression) initExp).getType().equals(SET_TYPE) ||
                        ((CastExpression) initExp).getType().declaresInterface(SET_TYPE))) ||
                    (initExp instanceof ConstructorCallExpression &&
                        (((ConstructorCallExpression) initExp).getType().equals(SET_TYPE) ||
                            ((ConstructorCallExpression) initExp).getType().declaresInterface(SET_TYPE)))
                    ) {
                    ctorArgs = new ArgumentListExpression(
                        new MethodCallExpression(
                            new ClassExpression(FXCOLLECTIONS_CNODE),
                            "observableSet",
                            ctorArgs)
                    );
                }
                implNode = fxType;
            } else {
                implNode = makeClassSafe(SIMPLE_OBJECT_PROPERTY_CNODE);
                GenericsType[] origGenerics = fxProperty.getType().getGenericsTypes();
                //List<GenericsType> copyGenericTypes = new ArrayList<GenericsType>();
                //for()
                implNode.setGenericsTypes(origGenerics);
            }
        }
        Expression initExpression = new ConstructorCallExpression(implNode, ctorArgs);

        IfStatement ifStmt = new IfStatement(
            new BooleanExpression(
                new BinaryExpression(
                    fieldExpression,
                    Token.newSymbol(Types.COMPARE_EQUAL, 0, 0),
                    ConstantExpression.NULL
                )
            ),
            new ExpressionStatement(
                new BinaryExpression(
                    fieldExpression,
                    Token.newSymbol(Types.EQUAL, 0, 0),
                    initExpression
                )
            ),
            EmptyStatement.INSTANCE
        );
        block.addStatement(ifStmt);
        block.addStatement(new ReturnStatement(fieldExpression));

        String getterName = getFXPropertyGetterName(fxProperty);
        MethodNode accessor = new MethodNode(getterName, fxProperty.getModifiers(), fxProperty.getType(),
            Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, block);
        accessor.setSynthetic(true);
        classNode.addMethod(accessor);

        // Create the xxxxProperty() method that merely calls getXxxxProperty()
        block = new BlockStatement();

        VariableExpression thisExpression = VariableExpression.THIS_EXPRESSION;
        ArgumentListExpression emptyArguments = ArgumentListExpression.EMPTY_ARGUMENTS;

        MethodCallExpression getProperty = new MethodCallExpression(thisExpression, getterName, emptyArguments);
        block.addStatement(new ReturnStatement(getProperty));

        String javaFXPropertyFunction = fxProperty.getName();

        accessor = new MethodNode(javaFXPropertyFunction, fxProperty.getModifiers(), fxProperty.getType(),
            Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, block);
        accessor.setSynthetic(true);
        classNode.addMethod(accessor);

        // Create the xxxx() method that merely calls getXxxxProperty()
        block = new BlockStatement();

        thisExpression = VariableExpression.THIS_EXPRESSION;
        emptyArguments = ArgumentListExpression.EMPTY_ARGUMENTS;

        getProperty = new MethodCallExpression(thisExpression, getterName, emptyArguments);
        block.addStatement(new ReturnStatement(getProperty));
        javaFXPropertyFunction = fxProperty.getName().replace("Property", "");

        accessor = new MethodNode(javaFXPropertyFunction, fxProperty.getModifiers(), fxProperty.getType(),
            Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, block);
        accessor.setSynthetic(true);
        classNode.addMethod(accessor);
    }

    /**
     * Creates the body of a setter method for the original property that is actually backed by a
     * JavaFX *Property instance:
     * <p>
     * Object $property = this.someProperty()
     * $property.setValue(value)
     *
     * @param fxProperty The original Groovy property that we're creating a setter for.
     *
     * @return A Statement that is the body of the new setter.
     */
    protected Statement createSetterStatement(PropertyNode fxProperty) {
        String fxPropertyGetter = getFXPropertyGetterName(fxProperty);
        VariableExpression thisExpression = VariableExpression.THIS_EXPRESSION;
        ArgumentListExpression emptyArgs = ArgumentListExpression.EMPTY_ARGUMENTS;

        MethodCallExpression getProperty = new MethodCallExpression(thisExpression, fxPropertyGetter, emptyArgs);

        ArgumentListExpression valueArg = new ArgumentListExpression(new Expression[]{new VariableExpression("value")});
        MethodCallExpression setValue = new MethodCallExpression(getProperty, "setValue", valueArg);

        return new ExpressionStatement(setValue);
    }

    /**
     * Creates the body of a getter method for the original property that is actually backed by a
     * JavaFX *Property instance:
     * <p>
     * Object $property = this.someProperty()
     * return $property.getValue()
     *
     * @param fxProperty The new JavaFX property.
     *
     * @return A Statement that is the body of the new getter.
     */
    protected Statement createGetterStatement(PropertyNode fxProperty) {
        String fxPropertyGetter = getFXPropertyGetterName(fxProperty);
        VariableExpression thisExpression = VariableExpression.THIS_EXPRESSION;
        ArgumentListExpression emptyArguments = ArgumentListExpression.EMPTY_ARGUMENTS;

        // We're relying on the *Property() method to provide the return value - is this still needed??
        //        Expression defaultReturn = defaultReturnMap.get(originalProperty.getType());
        //        if (defaultReturn == null)
        //            defaultReturn = ConstantExpression.NULL;

        MethodCallExpression getProperty = new MethodCallExpression(thisExpression, fxPropertyGetter, emptyArguments);
        MethodCallExpression getValue = new MethodCallExpression(getProperty, "getValue", emptyArguments);

        return new ReturnStatement(new ExpressionStatement(getValue));
    }

    /**
     * Generates a SyntaxErrorMessage based on the current SourceUnit, AnnotationNode, and a specified
     * error message.
     *
     * @param sourceUnit The SourceUnit
     * @param node       The node that was annotated
     * @param msg        The error message to display
     */
    private void generateSyntaxErrorMessage(SourceUnit sourceUnit, AnnotationNode node, String msg) {
        SyntaxException error = new SyntaxException(msg, node.getLineNumber(), node.getColumnNumber());
        sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(error, sourceUnit));
    }

    /**
     * Creates a copy of a FieldNode with a new name and, optionally, a new type.
     *
     * @param newName The name for the new field node.
     * @param newType The new type of the field.  If null, the old FieldNode's type will be used.
     * @param f       The FieldNode to copy.
     *
     * @return The new FieldNode.
     */
    private FieldNode createFieldNodeCopy(String newName, ClassNode newType, FieldNode f) {
        if (newType == null) { newType = f.getType(); }
        newType = newType.getPlainNodeReference();

        return new FieldNode(newName, f.getModifiers(), newType, f.getOwner(), f.getInitialValueExpression());
    }

    /**
     * Generates the correct getter method name for a JavaFX property.
     *
     * @param fxProperty The property for which the getter should be generated.
     *
     * @return The getter name as a String.
     */
    private String getFXPropertyGetterName(PropertyNode fxProperty) {
        return GriffonNameUtils.getGetterName(fxProperty.getName());
    }
}
