/*
 * Copyright 2008-2010 the original author or authors.
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
 * See the License for the specific language govnerning permissions and
 * limitations under the License.
 */
package griffon.application

import griffon.util.GriffonExceptionHandler

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 * @deprecated use SwingApplication instead
 */
class SingleFrameApplication extends SwingApplication {
    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler()
        SingleFrameApplication sfa = new SingleFrameApplication()
        sfa.bootstrap()
        sfa.realize()
        sfa.show()
    }
}
