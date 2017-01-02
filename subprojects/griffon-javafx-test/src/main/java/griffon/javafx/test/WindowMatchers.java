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
package griffon.javafx.test;

import javafx.stage.Window;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * TestFX matchers for {@code javafx.stage.Window}.
 *
 * @author Andres Almiray
 * @Deprecated use {@code org.testfx.matcher.base.WindowMatchers} instead
 * @since 2.6.0
 */
@Deprecated
public class WindowMatchers {
    @Factory
    public static Matcher<Window> isShowing() {
        return org.testfx.matcher.base.WindowMatchers.isShowing();
    }

    @Factory
    public static Matcher<Window> isNotShowing() {
        return org.testfx.matcher.base.WindowMatchers.isNotShowing();
    }

    @Factory
    public static Matcher<Window> isFocused() {
        return org.testfx.matcher.base.WindowMatchers.isFocused();
    }

    @Factory
    public static Matcher<Window> isNotFocused() {
        return org.testfx.matcher.base.WindowMatchers.isNotFocused();
    }
}
