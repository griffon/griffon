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
package griffon.exceptions;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class MVCGroupInstantiationException extends MVCGroupException {
    private static final long serialVersionUID = -3908214044662655669L;

    private final String mvcType;
    private final String mvcId;

    public MVCGroupInstantiationException(String message, String mvcType, String mvcId) {
        this(message, mvcType, mvcId, null);
    }

    public MVCGroupInstantiationException(String message, String mvcType, String mvcId, Throwable cause) {
        super(message, cause);
        this.mvcType = mvcType;
        this.mvcId = mvcId;
    }

    public MVCGroupInstantiationException(String mvcType, String mvcId, Throwable cause) {
        this("", mvcType, mvcId, cause);
    }

    public String getMvcType() {
        return mvcType;
    }

    public String getMvcName() {
        return mvcId;
    }
}
