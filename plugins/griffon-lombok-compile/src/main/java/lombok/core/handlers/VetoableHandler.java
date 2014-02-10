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

import griffon.transform.Vetoable;
import lombok.ast.*;
import lombok.core.AST;
import lombok.core.LombokNode;
import org.codehaus.griffon.compile.core.VetoableConstants;

import java.util.ArrayList;
import java.util.List;

import static lombok.ast.AST.*;
import static lombok.ast.AST.Number;
import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndFieldOnly;
import static lombok.core.util.Names.camelCaseToConstant;

/**
 * Contains code copied from BoundSetterHandler.java, original from Philipp Eichhorn (lombok-pg)
 *
 * @author Andres Almiray
 */
public abstract class VetoableHandler<TYPE_TYPE extends IType<?, FIELD_TYPE, ?, ?, ?, ?>, FIELD_TYPE extends IField<?, ?, ?>, LOMBOK_NODE_TYPE extends LombokNode<?, LOMBOK_NODE_TYPE, ?>, SOURCE_TYPE> extends ObservableHandler<TYPE_TYPE, FIELD_TYPE, LOMBOK_NODE_TYPE, SOURCE_TYPE> implements VetoableConstants {
    public VetoableHandler(LOMBOK_NODE_TYPE annotationNode, SOURCE_TYPE ast) {
        super(annotationNode, ast);
    }

    protected void addVetoableChangeSupportField(final TYPE_TYPE type) {
        addField(type,
            VETOABLE_CHANGE_SUPPORT_TYPE,
            VETOABLE_CHANGE_SUPPORT_FIELD_NAME)
            .makeTransient()
            .makeVolatile();
        addField(type,
            Type(JAVA_LANG_OBJECT).withDimensions(1),
            VETOABLE_CHANGE_SUPPORT_FIELD_NAME_LOCK,
            NewArray(Type(Object.class)).withDimensionExpression(Number(0)))
            .makeFinal();

        type.editor().injectMethod(MethodDecl(Type(VETOABLE_CHANGE_SUPPORT_TYPE), METHOD_GET_VETOABLE_CHANGE_SUPPORT).makePrivate()
            .withStatement(If(Equal(Field(VETOABLE_CHANGE_SUPPORT_FIELD_NAME), Null())).Then(Block()
                .withStatement(Synchronized(Field(VETOABLE_CHANGE_SUPPORT_FIELD_NAME_LOCK))
                    .withStatement(If(Equal(Field(VETOABLE_CHANGE_SUPPORT_FIELD_NAME), Null())).Then(Block()
                        .withStatement(Assign(Field(VETOABLE_CHANGE_SUPPORT_FIELD_NAME), New(Type(VETOABLE_CHANGE_SUPPORT_TYPE)).withArgument(This()))))))))
            .withStatement(Return(Field(VETOABLE_CHANGE_SUPPORT_FIELD_NAME))));
    }

    protected void addVetoableEventTriggerMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, VETOABLE_FIRE_METHODS, Call(METHOD_GET_VETOABLE_CHANGE_SUPPORT));
        for (IMethod<?, ?, ?, ?> method : type.methods()) {
            if (METHOD_FIRE_VETOABLE_CHANGE.equals(method.name())) {
                method.editor().makeProtected();
            }
        }
    }

    protected void addVetoableMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, VETOABLE_METHODS, Call(METHOD_GET_VETOABLE_CHANGE_SUPPORT));
    }

    public void handle() {
        LOMBOK_NODE_TYPE mayBeField = annotationNode.up();
        if (mayBeField == null) return;
        TYPE_TYPE type = typeOf(annotationNode, ast);
        List<FIELD_TYPE> fields = new ArrayList<FIELD_TYPE>();
        if (mayBeField.getKind() == AST.Kind.FIELD) {
            for (LOMBOK_NODE_TYPE node : annotationNode.upFromAnnotationToFields()) {
                fields.add(fieldOf(node, ast));
            }
        } else if (mayBeField.getKind() == AST.Kind.TYPE) {
            for (FIELD_TYPE field : type.fields()) {
                if (field.name().startsWith("$") || field.name().startsWith("this$"))
                    continue;
                if (field.isFinal()) continue;
                if (field.isStatic()) continue;
                fields.add(field);
            }
        } else {
            annotationNode.addError(canBeUsedOnClassAndFieldOnly(Vetoable.class));
            return;
        }
        apply(type, fields);
    }

    protected void apply(TYPE_TYPE type, List<FIELD_TYPE> fields) {
        if (!fields.isEmpty()) {
            if (!hasAllVetoableChangeMethods(type)) {
                addInterface(type, VETOABLE_TYPE);
                addVetoableChangeSupportField(type);
                addVetoableMethods(type);
                addVetoableEventTriggerMethods(type);
            }

            if (!hasAllPropertyChangeMethods(type)) {
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

    protected boolean hasAllVetoableChangeMethods(final TYPE_TYPE type) {
        for (IMethod<?, ?, ?, ?> method : type.methods()) {
            if (METHOD_GET_VETOABLE_CHANGE_SUPPORT.equals(method.name()))
                return true;
        }
        return hasMethodIncludingSupertypes(type, METHOD_ADD_VETOABLE_CHANGE_LISTENER, Type(VETOABLE_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_ADD_VETOABLE_CHANGE_LISTENER, Type(JAVA_LANG_STRING), Type(VETOABLE_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_REMOVE_VETOABLE_CHANGE_LISTENER, Type(VETOABLE_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_REMOVE_VETOABLE_CHANGE_LISTENER, Type(JAVA_LANG_STRING), Type(VETOABLE_CHANGE_LISTENER_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_GET_VETOABLE_CHANGE_LISTENERS) &&
            hasMethodIncludingSupertypes(type, METHOD_GET_VETOABLE_CHANGE_LISTENERS, Type(JAVA_LANG_STRING)) &&
            hasMethodIncludingSupertypes(type, METHOD_FIRE_VETOABLE_CHANGE, Type(PROPERTY_CHANGE_EVENT_TYPE)) &&
            hasMethodIncludingSupertypes(type, METHOD_FIRE_VETOABLE_CHANGE, Type(JAVA_LANG_STRING), Type(JAVA_LANG_OBJECT), Type(JAVA_LANG_OBJECT));
    }

    protected void generatePropertySetter(TYPE_TYPE type, FIELD_TYPE field, String propertyNameFieldName) {
        String fieldName = field.name();
        String setterName = getSetterName(fieldName);
        String oldValueName = OLD_VALUE_VARIABLE_NAME;

        final MethodDecl method = MethodDecl(Type(VOID), setterName)
            .makePublic()
            .withThrownException(Type(PROPERTY_VETO_EXCEPTION_TYPE))
            .withArgument(Arg(field.type(), fieldName))
            .withStatement(LocalDecl(field.type(), oldValueName).makeFinal().withInitialization(Field(fieldName)))
            .withStatement(Call(METHOD_FIRE_VETOABLE_CHANGE)
                .withArgument(Name(propertyNameFieldName))
                .withArgument(Name(oldValueName))
                .withArgument(Name(fieldName)));

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
