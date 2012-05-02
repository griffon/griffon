/*
 * Copyright 2011-2012 the original author or authors.
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

    /**
     * Controls the action to take when a plugin fails to be installed.
     * <p>Valid values are <tt>'abort'</tt>, <tt>'continue'</tt> or <tt>'retry'</tt>. Default is <tt>'continue'</tt>.</p>
     */
    String KEY_INSTALL_FAILURE_KEY = "griffon.install.failure";

    /**
     * Defines the name of the default search repository to use.
     * <p>Defaults to <tt>ArtifactRepository.DEFAULT_REMOTE_NAME</tt>.</p>
     */
    String KEY_DEFAULT_ARTIFACT_REPOSITORY = "griffon.artifact.repository.default.search";

    /**
     * Defines the name of the default publish repository to use.
     * <p>Defaults to <tt>ArtifactRepository.DEFAULT_REMOTE_NAME</tt>.</p>
     */
    String KEY_DEFAULT_RELEASE_ARTIFACT_REPOSITORY = "griffon.artifact.repository.default.release";

    /**
     * Defines the name of the default local install repository to use.
     * <p>Defaults to <tt>ArtifactRepository.DEFAULT_LOCAL_NAME</tt>.</p>
     */
    String KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY = "griffon.artifact.repository.default.install";

    /**
     * Controls if installed artifacts are copied to a local repository for faster lookups.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>. Default is <tt>'false'</tt>.</p>
     */
    String KEY_DISABLE_LOCAL_REPOSITORY_SYNC = "griffon.disable.local.repository.sync";

    /**
     * Defines if the command tool should resolve dependencies over the network or not.
     * <p>Valid values are <tt>'true'</tt> or <tt>'false'</tt>.</p>
     */
    String KEY_OFFLINE_MODE = "griffon.offline.mode";

    /**
     * The compiler source level to use.
     * Defaults to <tt>1.6</tt>
     */
    String KEY_COMPILER_SOURCE_LEVEL = "griffon.project.source.level";

    /**
     * The compiler target level to use.
     * Defaults to <tt>1.6</tt>
     */
    String KEY_COMPILER_TARGET_LEVEL = "griffon.project.target.level";

    /**
     * Source file encoding.
     * Defaults to <tt>UTF-8</tt>
     */
    String KEY_SOURCE_ENCODING = "griffon.source.encoding";

    /**
     * Add debug information to compiled classes.
     * Defaults to <tt>'yes'</tt>
     */
    String KEY_COMPILER_DEBUG = "griffon.project.compiler.debug";

    String[] KEYS = {
            KEY_INTERACTIVE_MODE,
            KEY_NON_INTERACTIVE_DEFAULT_ANSWER,
            KEY_FULL_STACKTRACE,
            KEY_CLI_VERBOSE,
            KEY_DISABLE_AST_INJECTION,
            KEY_DISABLE_AUTO_IMPORTS,
            KEY_DISABLE_LOGGING_INJECTION,
            KEY_DISABLE_THREADING_INJECTION,
            KEY_INSTALL_FAILURE_KEY,
            KEY_DEFAULT_ARTIFACT_REPOSITORY,
            KEY_DEFAULT_RELEASE_ARTIFACT_REPOSITORY,
            KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY,
            KEY_DISABLE_LOCAL_REPOSITORY_SYNC,
            KEY_OFFLINE_MODE,
            KEY_COMPILER_SOURCE_LEVEL,
            KEY_COMPILER_TARGET_LEVEL,
            KEY_SOURCE_ENCODING,
            KEY_COMPILER_DEBUG
    };
}
