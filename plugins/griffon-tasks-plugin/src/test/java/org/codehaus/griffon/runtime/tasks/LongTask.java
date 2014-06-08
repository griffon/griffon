/*
 * Copyright 2011 Eike Kettner
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

package org.codehaus.griffon.runtime.tasks;

import griffon.plugins.tasks.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 19:17
 */
public class LongTask extends AbstractTask<Long, Long> {
    private final static Logger log = LoggerFactory.getLogger(LongTask.class);

    public LongTask() {
        super("long-running", Mode.BACKGROUND);
    }

    public LongTask(Mode mode) {
        super("long-running", mode);
    }

    public Long execute(Tracker<Long> tracker) throws Exception {
        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
            log.info("task found number: " + i);
            tracker.publish((long) i);
            tracker.setProgress(0, 10, i);
            tracker.setPhase("Looking for " + (i + 1));
        }
        return 40L;
    }

    public void done(Long value) {
        log.info("Done: " + value);
    }

    public void failed(Throwable cause) {
        log.error("Task failed: " + cause);
    }

    public void process(List<Long> chunks) {
        log.info("Intermediate: " + chunks);
    }

}
