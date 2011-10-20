/*
 * Copyright 2010-2011 the original author or authors.
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
 * @since 0.9.4
 */
public class MVCGroupConfigurationException extends MVCGroupException {
    private final String mvcType;

    public MVCGroupConfigurationException(String message, String mvcType) {
        this(message, mvcType, null);
    }

    public MVCGroupConfigurationException(String message, String mvcType, Throwable cause) {
        super(message, cause);
        this.mvcType = mvcType;
    }

    public MVCGroupConfigurationException(String mvcType, Throwable cause) {
        this("", mvcType, cause);
    }

    public String getMvcType() {
        return mvcType;
    }
}
