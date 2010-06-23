/*
 * Copyright 2004-2010 the original author or authors.
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
package org.codehaus.griffon.ast;

import griffon.util.GriffonUtil;

import java.util.List;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

/**
 * Helper methods for working with Groovy AST trees.
 *
 * @author Graeme Rocher (Grails 0.3)
 */
public class GriffonASTUtils {
    /**
     * Returns whether a classNode has the specified property or not
     *
     * @param classNode    The ClassNode
     * @param propertyName The name of the property
     * @return True if the property exists in the ClassNode
     */
    public static boolean hasProperty(ClassNode classNode, String propertyName) {
        if (classNode == null || StringUtils.isBlank(propertyName)) {
            return false;
        }

        final MethodNode method = classNode.getMethod(GriffonUtil.getGetterName(propertyName), new Parameter[0]);
        if (method != null) return true;

        for (PropertyNode pn : classNode.getProperties()) {
            if (pn.getName().equals(propertyName) && !pn.isPrivate()) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasOrInheritsProperty(ClassNode classNode, String propertyName) {
        if (hasProperty(classNode, propertyName)) {
            return true;
        }

        ClassNode parent = classNode.getSuperClass();
        while (parent != null && !getFullName(parent).equals("java.lang.Object")) {
            if (hasProperty(parent, propertyName)) {
                return true;
            }
            parent = parent.getSuperClass();
        }

        return false;
    }

    /**
     * Tests whether the ClasNode implements the specified method name.
     *
     * @param classNode  The ClassNode
     * @param methodName The method name
     * @return True if it does implement the method
     */
    public static boolean implementsZeroArgMethod(ClassNode classNode, String methodName) {
        MethodNode method = classNode.getDeclaredMethod(methodName, new Parameter[]{});
        return method != null && (method.isPublic() || method.isProtected()) && !method.isAbstract();
    }

    @SuppressWarnings("unchecked")
    public static boolean implementsOrInheritsZeroArgMethod(ClassNode classNode, String methodName, List ignoreClasses) {
        if (implementsZeroArgMethod(classNode, methodName)) {
            return true;
        }

        ClassNode parent = classNode.getSuperClass();
        while (parent != null && !getFullName(parent).equals("java.lang.Object")) {
            if (!ignoreClasses.contains(parent) && implementsZeroArgMethod(parent, methodName)) {
                return true;
            }
            parent = parent.getSuperClass();
        }
        return false;
    }

    /**
     * Gets the full name of a ClassNode.
     *
     * @param classNode The class node
     * @return The full name
     */
    public static String getFullName(ClassNode classNode) {
        return classNode.getName();
    }

    public static ClassNode getFurthestParent(ClassNode classNode) {
        ClassNode parent = classNode.getSuperClass();
        while (parent != null && !getFullName(parent).equals("java.lang.Object")) {
            classNode = parent;
            parent = parent.getSuperClass();
        }
        return classNode;
    }

    public static boolean isEnum(ClassNode classNode) {
        ClassNode parent = classNode.getSuperClass();
        while (parent != null) {
            if (parent.getName().equals("java.lang.Enum")) return true;
            parent = parent.getSuperClass();
        }
        return false;
    }

    public static boolean addMethod(ClassNode classNode, MethodNode methodNode) {
        return addMethod(classNode, methodNode, false);
    }

    public static boolean addMethod(ClassNode classNode, MethodNode methodNode, boolean replace) {
        MethodNode oldMethod = classNode.getMethod(methodNode.getName(), methodNode.getParameters());
        if(oldMethod == null) {
            classNode.addMethod(methodNode);
            return true;
        } else if(replace) {
            classNode.getMethods().remove(oldMethod);
            classNode.addMethod(methodNode);
            return true;
        }
        return false;
    }

    /**
     * @return true if the two arrays are of the same size and have the same contents
     */
    public static boolean parametersEqual(Parameter[] a, Parameter[] b) {
        if (a.length == b.length) {
            boolean answer = true;
            for (int i = 0; i < a.length; i++) {
                if (!a[i].getType().equals(b[i].getType())) {
                    answer = false;
                    break;
                }
            }
            return answer;
        }
        return false;
    }

    public static void injectProperty(ClassNode classNode, String propertyName, Class propertyClass) {
        injectProperty(classNode, propertyName, propertyClass, null);
    }

    public static void injectProperty(ClassNode classNode, String propertyName, Class propertyClass, Object value) {
        final boolean hasProperty = hasOrInheritsProperty(classNode, propertyName);

        if (!hasProperty) {
            // inject into furthest relative
            ClassNode parent = getFurthestParent(classNode);
            Expression initialExpression = null;
            if(value != null) initialExpression = new ConstantExpression(value);
            parent.addProperty(propertyName, Modifier.PUBLIC, new ClassNode(propertyClass), initialExpression, null, null);
        }
    }

    public static void injectConstant(ClassNode classNode, String propertyName, Class propertyClass, Object value) {
        final boolean hasProperty = hasOrInheritsProperty(classNode, propertyName);

        if (!hasProperty) {
            // inject into furthest relative
            ClassNode parent = getFurthestParent(classNode);
            Expression initialExpression = new ConstantExpression(value);
            parent.addProperty(propertyName, Modifier.PUBLIC | Modifier.FINAL, new ClassNode(propertyClass), initialExpression, null, null)
;
        }
    }
}
