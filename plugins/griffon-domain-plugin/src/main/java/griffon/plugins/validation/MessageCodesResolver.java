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
package griffon.plugins.validation;

import javax.annotation.Nonnull;

/**
 * Strategy interface for building message codes from validation error codes.
 * Used by DataBinder to build the codes list for ObjectErrors and FieldErrors.
 * <p/>
 * <p>The resulting message codes correspond to the codes of a
 * MessageSourceResolvable (as implemented by ObjectError and DefaultFieldObjectError).
 *
 * @author Juergen Hoeller (Spring 1.0.1)
 */
public interface MessageCodesResolver {
    /**
     * Build message codes for the given error code and object name.
     * Used for building the codes list of an ObjectError.
     *
     * @param errorCode  the error code used for rejecting the object
     * @param objectName the name of the object
     * @return the message codes to use
     */
    @Nonnull
    String[] resolveMessageCodes(@Nonnull String errorCode, @Nonnull String objectName);

    /**
     * Build message codes for the given error code and field specification.
     * Used for building the codes list of an DefaultFieldObjectError.
     *
     * @param errorCode  the error code used for rejecting the value
     * @param objectName the name of the object
     * @param field      the field name
     * @param fieldType  the field type (may be <code>null</code> if not determinable)
     * @return the message codes to use
     */
    @Nonnull
    String[] resolveMessageCodes(@Nonnull String errorCode, @Nonnull String objectName, @Nonnull String field, @Nonnull Class<?> fieldType);
}
