/*
 * Copyright 2010-2012 the original author or authors.
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

package griffon.core.i18n

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
final class I18nEnhancer {
    private static final Logger LOG = LoggerFactory.getLogger(I18nEnhancer)

    private I18nEnhancer() {}

    static void enhance(MetaClass mc, MessageSourceProvider provider = MessageSourceHolder.instance) {
        if (LOG.debugEnabled) LOG.debug("Enhancing $mc with $provider")
        mc.getMessage = {String key ->
            provider.messageSource.getMessage(key)
        }
        mc.getMessage << {String key, Locale locale ->
            provider.messageSource.getMessage(key, locale)
        }
        mc.getMessage << {String key, Object[] args ->
            provider.messageSource.getMessage(key, args)
        }
        mc.getMessage << {String key, Object[] args, Locale locale ->
            provider.messageSource.getMessage(key, args, locale)
        }
        mc.getMessage << {String key, List args ->
            provider.messageSource.getMessage(key, args)
        }
        mc.getMessage << {String key, List args, Locale locale ->
            provider.messageSource.getMessage(key, args, locale)
        }
        mc.getMessage << {String key, String defaultMessage ->
            provider.messageSource.getMessage(key, defaultMessage)
        }
        mc.getMessage << {String key, String defaultMessage, Locale locale ->
            provider.messageSource.getMessage(key, defaultMessage, locale)
        }
        mc.getMessage << {String key, Object[] args, String defaultMessage ->
            provider.messageSource.getMessage(key, args, defaultMessage)
        }
        mc.getMessage << {String key, Object[] args, String defaultMessage, Locale locale ->
            provider.messageSource.getMessage(key, args, defaultMessage, locale)
        }
        mc.getMessage << {String key, List args, String defaultMessage ->
            provider.messageSource.getMessage(key, args, defaultMessage)
        }
        mc.getMessage << {String key, List args, String defaultMessage, Locale locale ->
            provider.messageSource.getMessage(key, args, defaultMessage, locale)
        }
    }
}