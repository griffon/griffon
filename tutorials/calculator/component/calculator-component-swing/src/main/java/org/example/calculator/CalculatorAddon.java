/*
 * Copyright 2016 the original author or authors.
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
package org.example.calculator;

import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Map;

@Named("calculator")
public class CalculatorAddon extends AbstractGriffonAddon {
    @Nonnull
    @Override
    public Map<String, Map<String, Object>> getMvcGroups() {
        return CollectionUtils.<String, Map<String, Object>>map()
            .e("calculator", CollectionUtils.<String, Object>map()
                    .e("model", "org.example.calculator.CalculatorModel")
                    .e("view", "org.example.calculator.CalculatorView")
                    .e("controller", "org.example.calculator.CalculatorController")
            );
    }
}
