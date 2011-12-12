/*
 * This class is executed inside the UI thread, so be sure to  call 
 * long running code in another thread.
 *
 * You have the following options
 * - execOutside(new Runnable(){ // your code })
 * - execFuture(new Runnable(){ // your code })
 * - new Thread(new Runnable(){ // your code }).start()
 *
 * You have the following options to run code again inside the UI thread
 * - execAsync(new Runnable(){ // your code })
 * - execSync(new Runnable(){ // your code })
 */

import java.util.Map;
import java.util.HashMap;
import static java.util.Arrays.asList;
import groovy.swing.SwingBuilder;
import static griffon.util.GriffonApplicationUtils.isMacOSX;
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler;
 
public class Initialize extends AbstractLifecycleHandler {
    public void run() {
        String laf = isMacOSX() ? "system" : "nimbus";
        Map<String, Object> metalOptions = new HashMap<String, Object>();
        metalOptions.put("boldFonts", false);
        SwingBuilder.lookAndFeel(laf, "gtk", asList("metal", metalOptions));
    }
}
