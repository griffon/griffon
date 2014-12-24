/*
 * Copyright 2008-2015 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TextPaneCharacterAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextPaneCharacterListener {
    private CallableWithArgs<Void> charactersInserted;
    private CallableWithArgs<Void> charactersRemoved;

    public CallableWithArgs<Void> getCharactersInserted() {
        return this.charactersInserted;
    }

    public CallableWithArgs<Void> getCharactersRemoved() {
        return this.charactersRemoved;
    }


    public void setCharactersInserted(CallableWithArgs<Void> charactersInserted) {
        this.charactersInserted = charactersInserted;
    }

    public void setCharactersRemoved(CallableWithArgs<Void> charactersRemoved) {
        this.charactersRemoved = charactersRemoved;
    }


    public void charactersInserted(org.apache.pivot.wtk.TextPane arg0, int arg1, int arg2) {
        if (charactersInserted != null) {
            charactersInserted.call(arg0, arg1, arg2);
        }
    }

    public void charactersRemoved(org.apache.pivot.wtk.TextPane arg0, int arg1, int arg2) {
        if (charactersRemoved != null) {
            charactersRemoved.call(arg0, arg1, arg2);
        }
    }

}
