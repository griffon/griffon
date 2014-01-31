/*
 * Created on Dec 1, 2010
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright @2010 the original author or authors.
 */
package lombok.javac.handlers.types;

import com.sun.tools.javac.code.Type;

import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

import static com.sun.tools.javac.code.TypeTags.NONE;
import static com.sun.tools.javac.code.TypeTags.VOID;

/**
 * A type to represent "void" and "none." Copied from Lombok.
 *
 * @author Alex Ruiz
 */
public class JCNoType extends Type implements NoType {
    public static JCNoType voidType() {
        return new JCNoType(VOID);
    }

    private JCNoType(int tag) {
        super(tag, null);
    }

    @Override
    public TypeKind getKind() {
        if (tag == VOID) return TypeKind.VOID;
        if (tag == NONE) return TypeKind.NONE;
        throw new IllegalStateException("Unexpected tag: " + tag);
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitNoType(this, p);
    }
}
