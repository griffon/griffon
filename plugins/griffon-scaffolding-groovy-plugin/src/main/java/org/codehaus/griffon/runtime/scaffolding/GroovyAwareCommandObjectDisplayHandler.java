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
package org.codehaus.griffon.runtime.scaffolding;

import griffon.core.GriffonApplication;
import griffon.core.mvc.MVCGroup;
import griffon.plugins.scaffolding.ScaffoldingContext;
import org.codehaus.griffon.runtime.groovy.mvc.GroovyAwareMVCGroup;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 */
public class GroovyAwareCommandObjectDisplayHandler extends DefaultCommandObjectDisplayHandler {
    @Inject
    public GroovyAwareCommandObjectDisplayHandler(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    protected void configureContext(@Nonnull ScaffoldingContext scaffoldingContext, @Nonnull MVCGroup mvcGroup) {
        if (scaffoldingContext instanceof GroovyAwareScaffoldingContext && mvcGroup instanceof GroovyAwareMVCGroup) {
            GroovyAwareScaffoldingContext gasc = (GroovyAwareScaffoldingContext) scaffoldingContext;
            GroovyAwareMVCGroup gamvcg = (GroovyAwareMVCGroup) mvcGroup;
            gasc.setBinding(gamvcg.getBuilder());
        }
    }
}
