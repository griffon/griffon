/*
 * Copyright 2012-2014 the original author or authors.
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

package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ElementAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.text.ElementListener {
    private CallableWithArgs<?> nodeInserted;
    private CallableWithArgs<?> nodesRemoved;
    private CallableWithArgs<?> fontChanged;
    private CallableWithArgs<?> foregroundColorChanged;
    private CallableWithArgs<?> backgroundColorChanged;
    private CallableWithArgs<?> underlineChanged;
    private CallableWithArgs<?> strikethroughChanged;

    public CallableWithArgs<?> getNodeInserted() {
        return this.nodeInserted;
    }

    public CallableWithArgs<?> getNodesRemoved() {
        return this.nodesRemoved;
    }

    public CallableWithArgs<?> getFontChanged() {
        return this.fontChanged;
    }

    public CallableWithArgs<?> getForegroundColorChanged() {
        return this.foregroundColorChanged;
    }

    public CallableWithArgs<?> getBackgroundColorChanged() {
        return this.backgroundColorChanged;
    }

    public CallableWithArgs<?> getUnderlineChanged() {
        return this.underlineChanged;
    }

    public CallableWithArgs<?> getStrikethroughChanged() {
        return this.strikethroughChanged;
    }


    public void setNodeInserted(CallableWithArgs<?> nodeInserted) {
        this.nodeInserted = nodeInserted;
    }

    public void setNodesRemoved(CallableWithArgs<?> nodesRemoved) {
        this.nodesRemoved = nodesRemoved;
    }

    public void setFontChanged(CallableWithArgs<?> fontChanged) {
        this.fontChanged = fontChanged;
    }

    public void setForegroundColorChanged(CallableWithArgs<?> foregroundColorChanged) {
        this.foregroundColorChanged = foregroundColorChanged;
    }

    public void setBackgroundColorChanged(CallableWithArgs<?> backgroundColorChanged) {
        this.backgroundColorChanged = backgroundColorChanged;
    }

    public void setUnderlineChanged(CallableWithArgs<?> underlineChanged) {
        this.underlineChanged = underlineChanged;
    }

    public void setStrikethroughChanged(CallableWithArgs<?> strikethroughChanged) {
        this.strikethroughChanged = strikethroughChanged;
    }


    public void nodeInserted(org.apache.pivot.wtk.text.Element arg0, int arg1) {
        if (nodeInserted != null) {
            nodeInserted.call(arg0, arg1);
        }
    }

    public void nodesRemoved(org.apache.pivot.wtk.text.Element arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (nodesRemoved != null) {
            nodesRemoved.call(arg0, arg1, arg2);
        }
    }

    public void fontChanged(org.apache.pivot.wtk.text.Element arg0, java.awt.Font arg1) {
        if (fontChanged != null) {
            fontChanged.call(arg0, arg1);
        }
    }

    public void foregroundColorChanged(org.apache.pivot.wtk.text.Element arg0, java.awt.Color arg1) {
        if (foregroundColorChanged != null) {
            foregroundColorChanged.call(arg0, arg1);
        }
    }

    public void backgroundColorChanged(org.apache.pivot.wtk.text.Element arg0, java.awt.Color arg1) {
        if (backgroundColorChanged != null) {
            backgroundColorChanged.call(arg0, arg1);
        }
    }

    public void underlineChanged(org.apache.pivot.wtk.text.Element arg0) {
        if (underlineChanged != null) {
            underlineChanged.call(arg0);
        }
    }

    public void strikethroughChanged(org.apache.pivot.wtk.text.Element arg0) {
        if (strikethroughChanged != null) {
            strikethroughChanged.call(arg0);
        }
    }

}
