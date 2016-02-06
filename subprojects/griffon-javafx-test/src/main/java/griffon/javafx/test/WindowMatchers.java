/*
 * Copyright 2008-2016 the original author or authors.
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

import com.google.common.base.Predicate;
import javafx.stage.Window;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import javax.annotation.Nullable;

import static org.testfx.matcher.base.GeneralMatchers.baseMatcher;

/**
 * TestFX matchers for {@code javafx.stage.Window}.
 *
 * @author Andres Almiray
 * @since 2.6.0
 */
public class WindowMatchers {
    //---------------------------------------------------------------------------------------------
    // STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    @Factory
    public static Matcher<Window> isShowing() {
        return baseMatcher("Window is showing", new Predicate<Window>() {
            @Override
            public boolean apply(@Nullable Window window) {
                return isShowing(window);
            }
        });
    }

    @Factory
    public static Matcher<Window> isNotShowing() {
        return baseMatcher("Window is not showing", window -> !isShowing(window));
    }

    @Factory
    public static Matcher<Window> isFocused() {
        return baseMatcher("Window is focused", new Predicate<Window>() {
            @Override
            public boolean apply(@Nullable Window window) {
                return isFocused(window);
            }
        });
    }

    @Factory
    public static Matcher<Window> isNotFocused() {
        return baseMatcher("Window is not focused", new Predicate<Window>() {
            @Override
            public boolean apply(@Nullable Window window) {
                return !isFocused(window);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    // PRIVATE STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    private static boolean isShowing(Window window) {
        return window.isShowing();
    }

    private static boolean isFocused(Window window) {
        return window.isFocused();
    }
}
