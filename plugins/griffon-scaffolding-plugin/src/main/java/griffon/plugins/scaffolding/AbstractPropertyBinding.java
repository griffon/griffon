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
package griffon.plugins.scaffolding;

import griffon.core.editors.ExtendedPropertyEditor;
import griffon.core.editors.PropertyEditorResolver;
import griffon.core.editors.ValueConversionException;
import griffon.core.threading.UIThreadManager;
import griffon.exceptions.GriffonException;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonClassUtils.getPropertyDescriptor;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractPropertyBinding implements Disposable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPropertyBinding.class);

    protected ConstrainedProperty constrainedProperty;
    private final Object LOCK = new Object[0];
    private boolean firing = false;
    private UIThreadManager uiThreadManager;

    protected AbstractPropertyBinding(@Nonnull UIThreadManager uiThreadManager, @Nonnull ConstrainedProperty constrainedProperty) {
        this.uiThreadManager = requireNonNull(uiThreadManager, "Argument 'uiThreadManager' cannot be null");
        this.constrainedProperty = requireNonNull(constrainedProperty, "Argument 'constrainedProperty' cannot be null");
    }

    protected void bind() {
        bindSource();
        bindTarget();

        if (getTargetPropertyValue() != null) {
            updateSource();
        } else {
            updateTarget();
        }
    }

    public void dispose() {
        constrainedProperty = null;
    }

    protected void updateSource() {
        synchronized (LOCK) {
            if (firing) return;
            firing = true;
            try {
                PropertyEditor sourceEditor = resolveSourcePropertyEditor();
                sourceEditor.setValue(getTargetPropertyValue());
                applySourcePropertyValue(sourceEditor.getValue());
            } catch (ValueConversionException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Could not update target property '" + constrainedProperty.getPropertyName() + "'", sanitize(e));
                }
            } finally {
                firing = false;
            }
        }
    }

    protected void updateTarget() {
        synchronized (LOCK) {
            if (firing) return;
            firing = true;
            try {
                PropertyEditor targetEditor = resolveTargetPropertyEditor();
                targetEditor.setValue(getSourcePropertyValue());
                setTargetPropertyValue(targetEditor.getValue());
            } catch (ValueConversionException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Could not update source property", sanitize(e));
                }
                setTargetPropertyValue(null);
            } finally {
                firing = false;
            }
        }
    }

    protected abstract void bindSource();

    protected abstract void bindTarget();

    @Nullable
    protected abstract Object getTargetPropertyValue();

    protected abstract void setTargetPropertyValue(@Nullable Object value);

    protected abstract void setSourcePropertyValue(@Nullable Object value);

    @Nullable
    protected abstract Object getSourcePropertyValue();

    @Nonnull
    protected PropertyEditor resolveTargetPropertyEditor() {
        PropertyEditor editor = doResolveTargetPropertyEditor();
        configureTargetPropertyEditor(editor);
        return editor;
    }

    @Nonnull
    protected PropertyEditor doResolveTargetPropertyEditor() {
        return PropertyEditorResolver.findEditor(constrainedProperty.getPropertyType());
    }

    protected void configureTargetPropertyEditor(@Nonnull PropertyEditor editor) {
        if (editor instanceof ExtendedPropertyEditor) {
            ((ExtendedPropertyEditor) editor).setFormat(constrainedProperty.getFormat());
        }
    }

    @Nonnull
    protected abstract PropertyEditor resolveSourcePropertyEditor();

    @Nullable
    protected PropertyDescriptor resolvePropertyDescriptor(@Nonnull Object source, @Nonnull String sourcePropertyName) {
        try {
            return getPropertyDescriptor(source, sourcePropertyName);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new GriffonException(e);
        } catch (InvocationTargetException e) {
            throw new GriffonException(e.getTargetException());
        }
    }

    private void applySourcePropertyValue(final Object value) {
        uiThreadManager.runInsideUIAsync(new Runnable() {
            public void run() {
                setSourcePropertyValue(value);
            }
        });
    }
}
