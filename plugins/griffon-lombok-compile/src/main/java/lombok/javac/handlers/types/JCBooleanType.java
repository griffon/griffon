/*
 * Copyright 2009-2013 the original author or authors.
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

package lombok.javac.handlers.types;

import com.sun.tools.javac.code.Type;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

import static com.sun.tools.javac.code.TypeTags.BOOLEAN;

/**
 * A type to represent "boolean".
 */
public class JCBooleanType extends Type implements PrimitiveType {
    public static final String type = "boolean";

    public static JCBooleanType booleanType() {
        return new JCBooleanType(BOOLEAN);
    }

    private JCBooleanType(int tag) {
        super(tag, null);
    }

    @Override
    public TypeKind getKind() {
        if (tag == BOOLEAN) return TypeKind.BOOLEAN;
        throw new IllegalStateException("Unexpected tag: " + tag);
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitPrimitive(this, p);
    }
}
