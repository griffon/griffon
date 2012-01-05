/*
 * Copyright 2011 the original author or authors.
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

package org.codehaus.griffon.cli;

/**
 * Defines all flags that can be used with the command line.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
public interface CommandLineConstants {
    /**
     * Defines if the command tool should run interactively or not.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>.</p>
     */
    String KEY_INTERACTIVE_MODE = "griffon.interactive.mode";

    /**
     * Holds the default answer to yield when running in non interactive mode.
     * <p>Valid values depend on the question being asked. However this answer will be
     * applied to all questions that arise during the command invocation.</p>
     */
    String KEY_NON_INTERACTIVE_DEFAULT_ANSWER = "griffon.noninteractive.default.answer";

    /**
     * Whether to output full stack traces or not in the logs.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_FULL_STACKTRACE = "griffon.full.stacktrace";

    /**
     * Controls verbose output when the command tool runs.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_CLI_VERBOSE = "griffon.cli.verbose";

    /**
     * Controls AST injection performed to artifacts by the compiler.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_DISABLE_AST_INJECTION = "griffon.disable.ast.injection";

    /**
     * Controls additional default imports handled by the compiler.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_DISABLE_AUTO_IMPORTS = "griffon.disable.auto.imports";

    /**
     * Controls AST injection adeed to logging statements performed by the compiler.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_DISABLE_LOGGING_INJECTION = "griffon.disable.logging.injection";

    /**
     * Controls threading management code inject to controllers by the compiler.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_DISABLE_THREADING_INJECTION = "griffon.disable.threading.injection";
}
