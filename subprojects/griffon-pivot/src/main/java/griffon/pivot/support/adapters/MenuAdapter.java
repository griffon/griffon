/*
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
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;
import org.apache.pivot.wtk.Menu;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class MenuAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.MenuListener {
    private CallableWithArgs<Void> sectionInserted;
    private CallableWithArgs<Void> sectionsRemoved;
    private CallableWithArgs<Void> activeItemChanged;

    public CallableWithArgs<Void> getSectionInserted() {
        return this.sectionInserted;
    }

    public CallableWithArgs<Void> getSectionsRemoved() {
        return this.sectionsRemoved;
    }

    public CallableWithArgs<Void> getActiveItemChanged() {
        return this.activeItemChanged;
    }


    public void setSectionInserted(CallableWithArgs<Void> sectionInserted) {
        this.sectionInserted = sectionInserted;
    }

    public void setSectionsRemoved(CallableWithArgs<Void> sectionsRemoved) {
        this.sectionsRemoved = sectionsRemoved;
    }

    public void setActiveItemChanged(CallableWithArgs<Void> activeItemChanged) {
        this.activeItemChanged = activeItemChanged;
    }


    public void sectionInserted(org.apache.pivot.wtk.Menu arg0, int arg1) {
        if (sectionInserted != null) {
            sectionInserted.call(arg0, arg1);
        }
    }

    public void sectionsRemoved(org.apache.pivot.wtk.Menu arg0, int arg1, org.apache.pivot.collections.Sequence<Menu.Section> arg2) {
        if (sectionsRemoved != null) {
            sectionsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void activeItemChanged(org.apache.pivot.wtk.Menu arg0, org.apache.pivot.wtk.Menu.Item arg1) {
        if (activeItemChanged != null) {
            activeItemChanged.call(arg0, arg1);
        }
    }

}
