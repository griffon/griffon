/**
 * Test case for the "@script.name@" Griffon command.
 */

import griffon.test.AbstractCliTestCase

class @artifact.name@Tests extends AbstractCliTestCase {
    void testDefault() {
        execute(["@script.name@"])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "@artifact.name@ script not found.", output.contains("Script not found:")
    }
}
