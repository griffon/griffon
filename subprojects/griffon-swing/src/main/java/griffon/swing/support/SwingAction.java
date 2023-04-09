/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.RunnableWithArgs;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static griffon.util.StringUtils.isNotBlank;
import static java.util.Objects.requireNonNull;

/**
 * An action implementation that relies on a closure to handle events.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingAction extends AbstractAction {
    private static final long serialVersionUID = 4493562556110760713L;
    private static final String ERROR_CALLABLE_NULL = "Argument 'callable' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private final transient RunnableWithArgs runnable;

    public SwingAction(@Nonnull RunnableWithArgs runnable) {
        this.runnable = requireNonNull(runnable, ERROR_RUNNABLE_NULL);
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

    public final void actionPerformed(ActionEvent evt) {
        runnable.run(evt);
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
        private RunnableWithArgs runnable;
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
            if (isNotBlank(mnemonic)) {
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
            if (isNotBlank(accelerator)) {
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
        public ActionBuilder withRunnable(@Nullable RunnableWithArgs runnable) {
            this.runnable = runnable;
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
            this.selectedSet = true;
            return this;
        }

        @Nonnull
        public Action build() {
            if (runnable == null && action == null) {
                throw new IllegalArgumentException("Either runnable, callable, or action must have a value.");
            }
            if (action == null) {
                action = new SwingAction(runnable);
            }
            if (isNotBlank(command)) {
                action.putValue(Action.ACTION_COMMAND_KEY, command);
            }
            if (isNotBlank(name)) {
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
            if (isNotBlank(longDescription)) {
                action.putValue(Action.LONG_DESCRIPTION, longDescription);
            }
            if (isNotBlank(shortDescription)) {
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