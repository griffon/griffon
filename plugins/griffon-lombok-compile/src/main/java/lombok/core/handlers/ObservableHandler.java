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
package lombok.core.handlers;

import griffon.transform.Observable;
import griffon.transform.Vetoable;
import lombok.ast.*;
import lombok.core.AST;
import lombok.core.LombokNode;
import org.codehaus.griffon.compile.core.ObservableConstants;

import java.util.ArrayList;
import java.util.List;

import static lombok.ast.AST.*;
import static lombok.ast.AST.Number;
import static lombok.ast.AST.String;
import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndFieldOnly;
import static lombok.core.util.Names.camelCaseToConstant;
import static lombok.core.util.Names.capitalize;

/**
 * Contains code copied from BoundSetterHandler.java, original from Philipp Eichhorn (lombok-pg)
 *
 * @author Andres Almiray
 */
public abstract class ObservableHandler<TYPE_TYPE extends IType<?, FIELD_TYPE, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, LOMBOK_NODE_TYPE, ?>, SOURCE_TYPE> extends AbstractHandler<TYPE_TYPE> implements ObservableConstants {
    protected static final String OLD_VALUE_VARIABLE_NAME = "$old";

    protected final LOMBOK_NODE_TYPE annotationNode;
    protected final SOURCE_TYPE ast;

    public ObservableHandler(LOMBOK_NODE_TYPE annotationNode, SOURCE_TYPE ast) {
        this.annotationNode = annotationNode;
        this.ast = ast;
    }

    protected void addPropertyChangeSupportField(final TYPE_TYPE type) {
        addField(type,
            PROPERTY_CHANGE_SUPPORT_TYPE,
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME)
            .makeTransient()
            .makeVolatile();
        addField(type,
            Type(JAVA_LANG_OBJECT).withDimensions(1),
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME_LOCK,
            NewArray(Type(Object.class)).withDimensionExpression(Number(0)))
            .makeFinal();

        type.editor().injectMethod(MethodDecl(Type(PROPERTY_CHANGE_SUPPORT_TYPE), METHOD_GET_PROPERTY_CHANGE_SUPPORT).makePrivate()
            .withStatement(If(Equal(Field(PROPERTY_CHANGE_SUPPORT_FIELD_NAME), Null())).Then(Block()
                .withStatement(Synchronized(Field(PROPERTY_CHANGE_SUPPORT_FIELD_NAME_LOCK))
                    .withStatement(If(Equal(Field(PROPERTY_CHANGE_SUPPORT_FIELD_NAME), Null())).Then(Block()
                        .withStatement(Assign(Field(PROPERTY_CHANGE_SUPPORT_FIELD_NAME), New(Type(PROPERTY_CHANGE_SUPPORT_TYPE)).withArgument(This()))))))))
            .withStatement(Return(Field(PROPERTY_CHANGE_SUPPORT_FIELD_NAME))));
    }

    protected void addObservableEventTriggerMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, OBSERVABLE_FIRE_METHODS, Call(METHOD_GET_PROPERTY_CHANGE_SUPPORT));
        for (IMethod<?, ?, ?, ?> method : type.methods()) {
            if (METHOD_FIRE_PROPERTY_CHANGE.equals(method.name())) {
                method.editor().makeProtected();
            }
        }
    }

    protected void addObservableMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, OBSERVABLE_METHODS, Call(METHOD_GET_PROPERTY_CHANGE_SUPPORT));
    }

    protected abstract void addInterface(final TYPE_TYPE type, String interfaceClassName);

    protected abstract TYPE_TYPE typeOf(final LOMBOK_NODE_TYPE node, final SOURCE_TYPE ast);

    protected abstract FIELD_TYPE fieldOf(final LOMBOK_NODE_TYPE node, final SOURCE_TYPE ast);

    protected abstract boolean hasMethodIncludingSupertypes(final TYPE_TYPE type, final String methodName, final TypeRef... argumentTypes);

    protected abstract boolean isAnnotatedWith(final TYPE_TYPE type, final Class<? extends java.lang.annotation.Annotation> annotationClass);

    protected abstract boolean isAnnotatedWith(final LOMBOK_NODE_TYPE type, final Class<? extends java.lang.annotation.Annotation> annotationClass);

    public void handle() {
        LOMBOK_NODE_TYPE mayBeField = annotationNode.up();
        if (mayBeField == null) return;
        TYPE_TYPE type = typeOf(annotationNode, ast);
        List<FIELD_TYPE> fields = new ArrayList<FIELD_TYPE>();
        boolean hasVetoableSupport = false;

        if (mayBeField.getKind() == AST.Kind.FIELD) {
            if (isAnnotatedWith(annotationNode.up().up(), Vetoable.class)) {
                hasVetoableSupport = true;
            } else {
                for (LOMBOK_NODE_TYPE node : annotationNode.upFromAnnotationToFields()) {
                    if (isAnnotatedWith(node, Vetoable.class)) {
                        hasVetoableSupport = true;
                        continue;
                    }
                    fields.add(fieldOf(node, ast));
                }
            }
        } else if (mayBeField.getKind() == AST.Kind.TYPE) {
            if (isAnnotatedWith(type, Vetoable.class)) return;
            for (FIELD_TYPE field : type.fields()) {
                if (field.name().startsWith("$") || field.name().startsWith("this$"))
                    continue;
                if (field.isFinal()) continue;
                if (field.isStatic()) continue;
                fields.add(field);
            }
        } else {
            annotationNode.addError(canBeUsedOnClassAndFieldOnly(Observable.class));
            return;
        }
        if (hasVetoableSupport || isAnnotatedWith(type, Vetoable.class)) return;
        apply(type, fields);
    }

    protected void apply(TYPE_TYPE type, List<FIELD_TYPE> fields) {
        if (!fields.isEmpty()) {
            if (!hasAllPropertyChangeMethods(type)) {
                addInterface(type, OBSERVABLE_TYPE);
                addPropertyChangeSupportField(type);
                addObservableMethods(type);
                addObservableEventTriggerMethods(type);
            }
        }

        for (FIELD_TYPE field : fields) {
            String propertyNameFieldName = "PROP_" + camelCaseToConstant(field.name());
            generatePropertyNameConstant(type, field, propertyNameFieldName);
            generateProperty(type, field, propertyNameFieldName);
        }
    }

    protected boolean hasAllPropertyChangeMethods(final TYPE_TYPE type) {
        for (IMethod<?, ?, ?, ?> method : type.methods()) {
            if (METHOD_GET_PROPERTY_CHANGE_SUPPORT.equals(method.name()))
                return true;
        }

        return hasMethodIncludingSupertypes(type, METHOD_ADD_PROPERTY_CHANGE_LISTENER, Type(PROPERTY_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_ADD_PROPERTY_CHANGE_LISTENER, Type(JAVA_LANG_STRING), Type(PROPERTY_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_REMOVE_PROPERTY_CHANGE_LISTENER, Type(PROPERTY_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_REMOVE_PROPERTY_CHANGE_LISTENER, Type(JAVA_LANG_STRING), Type(PROPERTY_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_GET_PROPERTY_CHANGE_LISTENERS) &&
            hasMethodIncludingSupertypes(type, METHOD_GET_PROPERTY_CHANGE_LISTENERS, Type(JAVA_LANG_STRING)) &&
            hasMethodIncludingSupertypes(type, METHOD_FIRE_PROPERTY_CHANGE, Type(PROPERTY_CHANGE_EVENT_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_FIRE_PROPERTY_CHANGE, Type(JAVA_LANG_STRING), Type(JAVA_LANG_OBJECT), Type(JAVA_LANG_OBJECT));
    }

    protected void generatePropertyNameConstant(final TYPE_TYPE type, final FIELD_TYPE field, final String propertyNameFieldName) {
        String propertyName = field.name();
        if (type.hasField(propertyNameFieldName)) return;
        type.editor().injectField(FieldDecl(Type(String.class), propertyNameFieldName).makePublic().makeStatic().makeFinal() //
            .withInitialization(String(propertyName)));
    }

    protected void generateProperty(TYPE_TYPE type, FIELD_TYPE field, String propertyNameFieldName) {
        generatePropertyGetter(type, field);
        generatePropertySetter(type, field, propertyNameFieldName);
    }

    protected void generatePropertyGetter(TYPE_TYPE type, FIELD_TYPE field) {
        String fieldName = field.name();
        String getterName = getGetterName(fieldName);
        if (type.hasMethod(getterName, field.type())) return;
        if (field.isOfType(BOOLEAN)) {
            getterName = "is" + capitalize(fieldName);
        }

        type.editor().injectMethod(
            MethodDecl(field.type(), getterName)
                .makePublic()
                .withStatement(Return(Field(fieldName)))
        );
    }

    protected void generatePropertySetter(TYPE_TYPE type, FIELD_TYPE field, String propertyNameFieldName) {
        String fieldName = field.name();
        String setterName = getSetterName(fieldName);
        String oldValueName = OLD_VALUE_VARIABLE_NAME;

        final MethodDecl method = MethodDecl(Type(VOID), setterName)
            .makePublic()
            .withArgument(Arg(field.type(), fieldName))
            .withStatement(LocalDecl(field.type(), oldValueName).makeFinal().withInitialization(Field(fieldName)));

        if (type.hasMethod(setterName, field.type())) {
            IMethod<?, ?, ?, ?> oldMethod = null;
            for (IMethod<?, ?, ?, ?> existingMethod : type.methods()) {
                if (setterName.equals(existingMethod.name())) {
                    List<Argument> args = method.getArguments();
                    if (args != null && args.size() == 1) {
                        oldMethod = existingMethod;
                        break;
                    }
                }
            }
            if (oldMethod != null) {
                method.withStatements(oldMethod.statements());
            } else {
                method.withStatement(Assign(Field(fieldName), Name(fieldName)));
            }
        } else {
            method.withStatement(Assign(Field(fieldName), Name(fieldName)));
        }

        method.withStatement(Call(METHOD_FIRE_PROPERTY_CHANGE)
            .withArgument(Name(propertyNameFieldName))
            .withArgument(Name(oldValueName))
            .withArgument(Name(fieldName)));

        type.editor().injectMethod(method);
    }
}
