/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.mvc;

import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import org.codehaus.griffon.runtime.core.mvc.DefaultMVCGroupFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class GroovyAwareMVCGroupFactory extends DefaultMVCGroupFactory {
    @Nonnull
    @Override
    public MVCGroup create(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members, @Nullable MVCGroup parentGroup) {
        return new GroovyAwareMVCGroup(mvcGroupManager, configuration, mvcId, members, parentGroup);
    }
}
