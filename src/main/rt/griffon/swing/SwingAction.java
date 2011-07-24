/*
 * Copyright 2008-2011 the original author or authors.
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
package griffon.swing;

import griffon.util.GriffonNameUtils;
import griffon.util.RunnableWithArgs;
import griffon.util.RunnableWithArgsClosure;
import groovy.lang.Closure;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * An action implementation that relies on a closure to handle events.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class SwingAction extends AbstractAction {
    private final Closure closure;

    public SwingAction(Closure closure) {
        this.closure = closure;
    }

    public final void actionPerformed(ActionEvent evt) {
        closure.call(evt);
    }

    public static ActionBuilder action() {
        return new ActionBuilder();
    }

    public static ActionBuilder action(Action action) {
        return new ActionBuilder(action);
    }

    public static ActionBuilder action(String name) {
        return new ActionBuilder().withName(name);
    }

    /**
     * A builder for actions.
     *
     * @author Andres Almiray
     * @since 0.9.3
     */
    public static class ActionBuilder {
        private String name;
        private int mnemonic;
        private KeyStroke accelerator;
        private String shortDescription;
        private String longDescription;
        private String command;
        private Icon smallIcon;
        private Icon largeIcon;
        private Closure closure;
        private boolean enabled = true;

        private Action action;
        private boolean mnemonicSet = false;
        private boolean enabledSet = false;

        public ActionBuilder() {
            this(null);
        }

        public ActionBuilder(Action action) {
            this.action = action;
        }

        public ActionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ActionBuilder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public ActionBuilder withLongDescription(String longDescription) {
            this.longDescription = longDescription;
            return this;
        }

        public ActionBuilder withCommand(String command) {
            this.command = command;
            return this;
        }

        public ActionBuilder withMnemonic(String mnemonic) {
            this.mnemonic = KeyStroke.getKeyStroke(mnemonic).getKeyCode();
            mnemonicSet = true;
            return this;
        }

        public ActionBuilder withMnemonic(int mnemonic) {
            this.mnemonic = mnemonic;
            mnemonicSet = true;
            return this;
        }

        public ActionBuilder withAccelerator(String accelerator) {
            this.accelerator = KeyStroke.getKeyStroke(accelerator);
            return this;
        }

        public ActionBuilder withAccelerator(KeyStroke accelerator) {
            this.accelerator = accelerator;
            return this;
        }

        public ActionBuilder withSmallIcon(Icon smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public ActionBuilder withLargeIcon(Icon largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public ActionBuilder withClosure(Closure closure) {
            this.closure = closure;
            return this;
        }

        public ActionBuilder withRunnable(RunnableWithArgs runnable) {
            if (runnable != null) this.closure = new RunnableWithArgsClosure(runnable);
            return this;
        }

        public ActionBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            this.enabledSet = true;
            return this;
        }

        public Action build() {
            if (closure == null && action == null) {
                throw new IllegalArgumentException("Either closure: or action: must have a value.");
            }
            if (action == null) action = new SwingAction(closure);
            if (!isBlank(command)) action.putValue(Action.ACTION_COMMAND_KEY, command);
            if (!isBlank(name)) action.putValue(Action.NAME, name);
            if (mnemonicSet) {
                action.putValue(Action.MNEMONIC_KEY, mnemonic);
            }
            if (accelerator != null) action.putValue(Action.ACCELERATOR_KEY, accelerator);
            if (largeIcon != null) action.putValue(Action.LARGE_ICON_KEY, largeIcon);
            if (smallIcon != null) action.putValue(Action.SMALL_ICON, smallIcon);
            if (!isBlank(longDescription)) action.putValue(Action.LONG_DESCRIPTION, longDescription);
            if (!isBlank(shortDescription)) action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
            if (enabledSet) action.setEnabled(enabled);
            return action;
        }
    }
}
