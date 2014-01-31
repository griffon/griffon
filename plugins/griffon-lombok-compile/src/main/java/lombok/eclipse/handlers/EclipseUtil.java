/*
 * Copyright 2012-2013 the original author or authors.
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

package lombok.eclipse.handlers;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

import static lombok.eclipse.Eclipse.fromQualifiedName;
import static lombok.eclipse.Eclipse.poss;

/**
 * @author Andres Almiray
 */
public class EclipseUtil {
    public static void addInterface(TypeDeclaration type, String interfaceName, Annotation annotation) {
        TypeReference[] interfaces = null;
        if (type.superInterfaces != null) {
            interfaces = new TypeReference[type.superInterfaces.length + 1];
            System.arraycopy(type.superInterfaces, 0, interfaces, 0, type.superInterfaces.length);
        } else {
            interfaces = new TypeReference[1];
        }
        final char[][] typeNameTokens = fromQualifiedName(interfaceName);
        interfaces[interfaces.length - 1] = new QualifiedTypeReference(typeNameTokens, poss(annotation, typeNameTokens.length));
        type.superInterfaces = interfaces;
    }
}
