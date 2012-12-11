/*
 * This class is executed inside the UI thread, so be sure to  call 
 * long running code in another thread.
 *
 * You have the following options
 * - execOutsideUI(new Runnable(){ // your code })
 * - execFuture(new Runnable(){ // your code })
 * - new Thread(new Runnable(){ // your code }).start()
 *
 * You have the following options to run code again inside the UI thread
 * - execInsideUIAsync(new Runnable(){ // your code })
 * - execInsideUISync(new Runnable(){ // your code })
 */

import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler;
 
public class Shutdown extends AbstractLifecycleHandler {
    public void run() {
    }
}
