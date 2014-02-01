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
package griffon.swing.support;

import griffon.core.CallableWithArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * An action implementation that relies on a closure to handle events.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingAction extends AbstractAction {
    private static final long serialVersionUID = 4598888514827528435L;
    private static final String ERROR_CALLABLE_NULL = "Argument 'callable' cannot be null";
    private final CallableWithArgs<?> callable;

    public SwingAction(@Nonnull CallableWithArgs<?> callable) {
        this.callable = requireNonNull(callable, ERROR_CALLABLE_NULL);
    }

    public final void actionPerformed(ActionEvent evt) {
        callable.call(evt);
    }

    @Nonnull
    public static ActionBuilder action() {
        return new ActionBuilder();
    }

    @Nonnull
    public static ActionBuilder action(@Nullable Action action) {
        return new ActionBuilder(action);
    }

    @Nonnull
    public static ActionBuilder action(@Nullable String name) {
        return new ActionBuilder().withName(name);
    }

    /**
     * A builder for actions.
     *
     * @author Andres Almiray
     * @since 2.0.0
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
        private CallableWithArgs<?> callable;
        private boolean enabled = true;
        private boolean selected = false;

        private Action action;
        private boolean mnemonicSet = false;
        private boolean enabledSet = false;
        private boolean selectedSet = false;

        public ActionBuilder() {
            this(null);
        }

        @Nonnull
        public ActionBuilder(@Nullable Action action) {
            this.action = action;
        }

        @Nonnull
        public ActionBuilder withName(@Nullable String name) {
            this.name = name;
            return this;
        }

        @Nonnull
        public ActionBuilder withShortDescription(@Nullable String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        @Nonnull
        public ActionBuilder withLongDescription(@Nullable String longDescription) {
            this.longDescription = longDescription;
            return this;
        }

        @Nonnull
        public ActionBuilder withCommand(@Nullable String command) {
            this.command = command;
            return this;
        }

        @Nonnull
        public ActionBuilder withMnemonic(@Nullable String mnemonic) {
            if (!isBlank(mnemonic)) {
                this.mnemonic = KeyStroke.getKeyStroke(mnemonic).getKeyCode();
            }
            mnemonicSet = true;
            return this;
        }

        @Nonnull
        public ActionBuilder withMnemonic(int mnemonic) {
            this.mnemonic = mnemonic;
            mnemonicSet = true;
            return this;
        }

        @Nonnull
        public ActionBuilder withAccelerator(@Nullable String accelerator) {
            if (!isBlank(accelerator)) {
                this.accelerator = KeyStroke.getKeyStroke(accelerator);
            }
            return this;
        }

        @Nonnull
        public ActionBuilder withAccelerator(@Nullable KeyStroke accelerator) {
            this.accelerator = accelerator;
            return this;
        }

        @Nonnull
        public ActionBuilder withSmallIcon(@Nullable Icon smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        @Nonnull
        public ActionBuilder withLargeIcon(@Nullable Icon largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        @Nonnull
        public ActionBuilder withRunnable(@Nullable CallableWithArgs<?> callable) {
            this.callable = callable;
            return this;
        }

        @Nonnull
        public ActionBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            this.enabledSet = true;
            return this;
        }

        @Nonnull
        public ActionBuilder withSelected(boolean selected) {
            this.selected = selected;
            this.enabledSet = true;
            return this;
        }

        @Nonnull
        public Action build() {
            if (callable == null && action == null) {
                throw new IllegalArgumentException("Either closure: or action: must have a value.");
            }
            if (action == null) {
                action = new SwingAction(callable);
            }
            if (!isBlank(command)) {
                action.putValue(Action.ACTION_COMMAND_KEY, command);
            }
            if (!isBlank(name)) {
                action.putValue(Action.NAME, name);
            }
            if (mnemonicSet) {
                action.putValue(Action.MNEMONIC_KEY, mnemonic);
            }
            if (accelerator != null) {
                action.putValue(Action.ACCELERATOR_KEY, accelerator);
            }
            if (largeIcon != null) {
                action.putValue(Action.LARGE_ICON_KEY, largeIcon);
            }
            if (smallIcon != null) {
                action.putValue(Action.SMALL_ICON, smallIcon);
            }
            if (!isBlank(longDescription)) {
                action.putValue(Action.LONG_DESCRIPTION, longDescription);
            }
            if (!isBlank(shortDescription)) {
                action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
            }
            if (enabledSet) {
                action.setEnabled(enabled);
            }
            if (selectedSet) {
                action.putValue(Action.SELECTED_KEY, selected);
            }
            return action;
        }
    }
}
