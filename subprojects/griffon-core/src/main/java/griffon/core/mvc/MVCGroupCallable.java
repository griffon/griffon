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
package griffon.core.mvc;

import javax.annotation.Nullable;

/**
 * An specialized function for working with MVC members.
 *
 * @author Andres Almiray
 * @since 2.1.0
 */
public interface MVCGroupCallable {
    /**
     * Executes this function<p>
     *
     * @param group the MVC group
     */
    void call(@Nullable MVCGroup group);
}
