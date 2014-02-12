/*
 * Copyright 2010-2014 the original author or authors.
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
package griffon.exceptions;

/**
 * @author Andres Almiray
 * @since 0.9.3
 */
public class MVCGroupInstantiationException extends MVCGroupException {
    private final String mvcType;
    private final String mvcName;

    public MVCGroupInstantiationException(String message, String mvcType, String mvcName) {
        this(message, mvcType, mvcName, null);
    }

    public MVCGroupInstantiationException(String message, String mvcType, String mvcName, Throwable cause) {
        super(message, cause);
        this.mvcType = mvcType;
        this.mvcName = mvcName;
    }

    public MVCGroupInstantiationException(String mvcType, String mvcName, Throwable cause) {
        this("", mvcType, mvcName, cause);
    }

    public String getMvcType() {
        return mvcType;
    }

    public String getMvcName() {
        return mvcName;
    }
}
