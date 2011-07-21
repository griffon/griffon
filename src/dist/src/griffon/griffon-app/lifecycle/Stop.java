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

import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler;
 
public class Stop extends AbstractLifecycleHandler {
    public void run() {
    }
}
