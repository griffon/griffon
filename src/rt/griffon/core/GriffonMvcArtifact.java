/*
 * Copyright 2009-2010 the original author or authors.
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
package griffon.core;

import java.util.Map;
import java.util.List;

/**
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public interface GriffonMvcArtifact extends GriffonArtifact {
    void mvcGroupInit(Map<String, ?> args);

    void mvcGroupDestroy();

    Map<String, ?> buildMVCGroup(String mvcType);

    Map<String, ?> buildMVCGroup(String mvcType, String mvcName);

    Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType);

    Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType, String mvcName);

    List<?> createMVCGroup(String mvcType);

    List<?> createMVCGroup(Map<String, ?> args, String mvcType);

    List<?> createMVCGroup(String mvcType, Map<String, ?> args);

    List<?> createMVCGroup(String mvcType, String mvcName);

    List<?> createMVCGroup(Map<String, ?> args, String mvcType, String mvcName);

    List<?> createMVCGroup(String mvcType, String mvcName, Map<String, ?> args);

    void destroyMVCGroup(String mvcName);
}