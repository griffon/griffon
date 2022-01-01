/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.exceptions;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class MVCGroupException extends GriffonException {
    private static final long serialVersionUID = 3911885131519171958L;

    public MVCGroupException() {
    }

    public MVCGroupException(String message) {
        super(message);
    }

    public MVCGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public MVCGroupException(Throwable cause) {
        super(cause);
    }
}
