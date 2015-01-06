/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swinghelper.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import java.applet.Applet;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.ref.WeakReference;

import static griffon.core.GriffonExceptionHandler.sanitize;


/**
 * <p>This class is used to detect Event Dispatch Thread rule violations<br>
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How to Use Threads</a>
 * for more info</p>
 * <p/>
 * <p>This is a modification of original idea of Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here<br>
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily Find Swing Threading Mistakes</a>
 * </p>
 * <p/>
 * <p>Links</ul>
 * <li>https://swinghelper.dev.java.net</li>
 * <li>http://weblogs.java.net/blog/alexfromsun/archive/2006/02/debugging_swing.html</li>
 * </ul></p>
 *
 * @author Scott Delap
 * @author Alexander Potochkin
 * @author Andres Almiray
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {
    private static final Logger LOG = LoggerFactory.getLogger(CheckThreadViolationRepaintManager.class);
    // it is recommended to pass the complete check  
    private boolean completeCheck = true;
    private WeakReference<JComponent> lastComponent;
    private final RepaintManager delegate;

    public CheckThreadViolationRepaintManager() {
        this(new RepaintManager());
    }

    public CheckThreadViolationRepaintManager(RepaintManager delegate) {
        if (delegate == null || delegate instanceof CheckThreadViolationRepaintManager) {
            throw new IllegalArgumentException();
        }
        this.delegate = delegate;
    }

    public boolean isCompleteCheck() {
        return completeCheck;
    }

    public void setCompleteCheck(boolean completeCheck) {
        this.completeCheck = completeCheck;
    }

    public synchronized void addInvalidComponent(JComponent component) {
        checkThreadViolations(component);
        delegate.addInvalidComponent(component);
    }

    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkThreadViolations(component);
        delegate.addDirtyRegion(component, x, y, w, h);
    }

    private void checkThreadViolations(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
            boolean repaint = false;
            boolean fromSwing = false;
            boolean imageUpdate = false;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (repaint && st.getClassName().startsWith("javax.swing.") &&
                    // for details see
                    // https://swinghelper.dev.java.net/issues/show_bug.cgi?id=1
                    !st.getClassName().startsWith("javax.swing.SwingWorker")) {
                    fromSwing = true;
                }
                if (repaint && "imageUpdate".equals(st.getMethodName())) {
                    imageUpdate = true;
                }
                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                    fromSwing = false;
                }
            }
            if (imageUpdate) {
                //assuming it is java.awt.image.ImageObserver.imageUpdate(...)
                //image was asynchronously updated, that's ok
                return;
            }
            if (repaint && !fromSwing) {
                //no problems here, since repaint() is thread safe
                return;
            }
            //ignore the last processed component
            if (lastComponent != null && c == lastComponent.get()) {
                return;
            }
            lastComponent = new WeakReference<>(c);
            violationFound(c, stackTrace);
        }
    }

    protected void violationFound(JComponent c, StackTraceElement[] stackTrace) {
        stackTrace = sanitize(stackTrace);
        StringBuilder sb = new StringBuilder("EDT violation detected").append('\n');
        sb.append(c).append('\n');
        for (StackTraceElement st : stackTrace) {
            sb.append("\tat ").append(st).append('\n');
        }
        if (LOG.isWarnEnabled()) {
            LOG.warn(sb.toString());
        }
    }


    // -- delegate methods

    public static RepaintManager currentManager(Component component) {
        return RepaintManager.currentManager(component);
    }

    public static RepaintManager currentManager(JComponent jComponent) {
        return RepaintManager.currentManager(jComponent);
    }

    @Override
    public Rectangle getDirtyRegion(JComponent jComponent) {
        return delegate.getDirtyRegion(jComponent);
    }

    @Override
    public Dimension getDoubleBufferMaximumSize() {
        return delegate.getDoubleBufferMaximumSize();
    }

    @Override
    public Image getOffscreenBuffer(Component component, int i, int i1) {
        return delegate.getOffscreenBuffer(component, i, i1);
    }

    @Override
    public Image getVolatileOffscreenBuffer(Component component, int i, int i1) {
        return delegate.getVolatileOffscreenBuffer(component, i, i1);
    }

    @Override
    public boolean isCompletelyDirty(JComponent jComponent) {
        return delegate.isCompletelyDirty(jComponent);
    }

    @Override
    public boolean isDoubleBufferingEnabled() {
        return delegate.isDoubleBufferingEnabled();
    }

    @Override
    public void markCompletelyClean(JComponent jComponent) {
        delegate.markCompletelyClean(jComponent);
    }

    @Override
    public void markCompletelyDirty(JComponent jComponent) {
        delegate.markCompletelyDirty(jComponent);
    }

    @Override
    public void paintDirtyRegions() {
        delegate.paintDirtyRegions();
    }

    @Override
    public void removeInvalidComponent(JComponent jComponent) {
        delegate.removeInvalidComponent(jComponent);
    }

    public static void setCurrentManager(RepaintManager repaintManager) {
        RepaintManager.setCurrentManager(repaintManager);
    }

    @Override
    public void setDoubleBufferingEnabled(boolean b) {
        delegate.setDoubleBufferingEnabled(b);
    }

    @Override
    public void setDoubleBufferMaximumSize(Dimension dimension) {
        delegate.setDoubleBufferMaximumSize(dimension);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void validateInvalidComponents() {
        delegate.validateInvalidComponents();
    }

    @Override
    public void addDirtyRegion(Window window, int i, int i1, int i2, int i3) {
        delegate.addDirtyRegion(window, i, i1, i2, i3);
    }

    @Override
    public void addDirtyRegion(Applet applet, int i, int i1, int i2, int i3) {
        delegate.addDirtyRegion(applet, i, i1, i2, i3);
    }
}