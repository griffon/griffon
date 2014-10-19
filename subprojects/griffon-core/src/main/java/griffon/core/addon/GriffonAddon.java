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
package griffon.core.addon;

import griffon.core.GriffonApplication;
import griffon.core.ShutdownHandler;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Identifies an Addon artifact.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface GriffonAddon extends ShutdownHandler {
    String SUFFIX = "GriffonAddon";

    @Nonnull
    Logger getLog();

    void init(@Nonnull GriffonApplication app);

    @Nonnull
    Map<String, Map<String, Object>> getMvcGroups();

    /**
     * Returns a list of MVCGroup names that should be auto started.</p>
     *
     * @return a list of MVCGroup names that this addon contributes.
     * @since 2.1.0
     */
    @Nonnull
    List<String> getStartupGroups();
}
