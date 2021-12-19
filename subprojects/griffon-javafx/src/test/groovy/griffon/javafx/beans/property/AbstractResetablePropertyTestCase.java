/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.javafx.beans.property;

import griffon.annotations.core.Nonnull;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractResetablePropertyTestCase<T> {
    @Test
    public void updatingValueDoesNotAffectBaseValue() {
        // given:
        ResetableProperty<T> property = resolveProperty();
        T baseValue = property.getBaseValue();

        // when:
        property.setValue(getNewValue());

        // then:
        assertThat(property.getBaseValue(), equalTo(baseValue));
    }

    @Test
    public void resetingPropertyAffectsValue() {
        // given:
        ResetableProperty<T> property = resolveProperty();
        T baseValue = property.getBaseValue();

        // when:
        property.setValue(getNewValue());
        property.reset();

        // then:
        assertThat(property.getBaseValue(), equalTo(baseValue));
        assertThat(property.getValue(), equalTo(baseValue));
    }

    @Test
    public void rebasingPropertyAffectsBaseValue() {
        // given:
        ResetableProperty<T> property = resolveProperty();
        T baseValue = property.getBaseValue();

        // when:
        property.setValue(getNewValue());
        property.rebase();

        // then:
        assertThat(property.getBaseValue(), equalTo(property.getValue()));
    }

    @Test
    public void dirtyChecks() {
        // given:
        ResetableProperty<T> property = resolveProperty();
        final AtomicBoolean witness = new AtomicBoolean(false);
        property.dirtyProperty().addListener((v, o, n) -> witness.set(n));

        // expect:
        assertThat(property.isDirty(), equalTo(false));
        assertThat(witness.get(), equalTo(false));

        // when:
        property.setValue(getNewValue());

        // then:
        assertThat(property.isDirty(), equalTo(true));
        assertThat(witness.get(), equalTo(true));

        // when:
        property.reset();

        // then:
        assertThat(property.isDirty(), equalTo(false));
        assertThat(witness.get(), equalTo(false));

        // when:
        property.setValue(getNewValue());

        // then:
        assertThat(property.isDirty(), equalTo(true));
        assertThat(witness.get(), equalTo(true));

        // when:
        property.rebase();

        // then:
        assertThat(property.isDirty(), equalTo(false));
        assertThat(witness.get(), equalTo(false));
    }

    @Nonnull
    protected abstract ResetableProperty<T> resolveProperty();

    @Nonnull
    protected abstract T getNewValue();
}
