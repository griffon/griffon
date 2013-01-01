/*
 * Copyright 2011-2013 SpringSource
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

package org.codehaus.griffon.test.event;

// import griffon.build.logging.GriffonConsole;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.PrintWriter;
import java.io.StringWriter;

import static griffon.util.GriffonExceptionHandler.sanitize;

// import org.codehaus.griffon.exceptions.DefaultStackTraceFilterer;
// import org.codehaus.griffon.exceptions.StackTraceFilterer;

/**
 * A RunNotifier that logs the the GriffonConsole.
 *
 * @author Graeme Rocher (Grails 2.0)
 */
public class GriffonTestRunNotifier extends RunNotifier {
    int progress = 0;
    int total;
    // private GriffonConsole console = GriffonConsole.getInstance();

    public GriffonTestRunNotifier(int total) {
        this.total = total;
    }

    @Override
    public void fireTestRunFinished(Result result) {
        super.fireTestRunFinished(result);
    }

/*
    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException {
        console.indicateProgress(++progress, total);
        super.fireTestStarted(description);
    }
*/

    @Override
    public void fireTestFailure(Failure failure) {
        // console.error("Failure: ", failure.getDescription().getDisplayName());
        System.out.println("Failure: " + failure.getDescription().getDisplayName());
        Throwable exception = failure.getException();
        if (exception == null) {
            // console.error(failure.getMessage());
            System.out.println(failure.getMessage());
        } else {
            // StackTraceFilterer filterer = new DefaultStackTraceFilterer();
            // filterer.setCutOffPackage("org.junit");
            // filterer.filter(exception, true);
            sanitize(exception);

            StringWriter sw = new StringWriter();
            PrintWriter ps = new PrintWriter(sw);
            exception.printStackTrace(ps);

            // console.error("", sw.toString());
            System.out.println(sw.toString());
        }
        super.fireTestFailure(failure);
    }
}
