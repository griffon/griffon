/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.plugins.scaffolding;

import griffon.core.artifact.GriffonController;
import griffon.plugins.validation.FieldObjectError;
import griffon.plugins.validation.ObjectError;
import griffon.plugins.validation.Validateable;
import griffon.plugins.validation.constraints.ConstrainedProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by aalmiray on 26/02/14.
 */
public interface ScaffoldingContext extends Disposable {
    GriffonController getController();

    void setController(GriffonController controller);

    String getActionName();

    void setActionName(String actionName);

    Validateable getValidateable();

    void setValidateable(Validateable validateable);

    void addDisposable(Disposable disposable);

    void dispose();

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    Map<String, Object> widgetAttributes(String widget, ConstrainedProperty constrainedProperty);

    String resolveMessage(String key, String defaultValue);

    Class resolveLabeler(String property);

    Class resolveWidget(String property);

    String[] resolveErrorMessages();

    String[] resolveFieldErrorMessages(List<FieldObjectError> errors);

    String[] resolveErrorMessages(List<ObjectError> errors);

    String[] resolveErrorMessages(ObjectError error);
}
