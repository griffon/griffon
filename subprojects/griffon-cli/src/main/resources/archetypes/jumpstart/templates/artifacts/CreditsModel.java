@artifact.package@import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Map;

public class CreditsModel extends AbstractDialogModel {
    private static String CREDITS = null;
    private static final Object[] lock = new Object[0];

    private String credits;

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        firePropertyChange("credits", this.credits, this.credits = credits);
    }

    public void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args);
        setCredits(fetchCreditsText());
    }

    protected String getDialogKey() {
        return "Credits";
    }

    protected String getDialogTitle() {
        return "Credits";
    }

    public static String fetchCreditsText() {
        synchronized (lock) {
            if (CREDITS == null) {
                try {
                    CREDITS = DefaultGroovyMethods.getText(CreditsModel.class.getResource("/credits.txt"));
                } catch (Exception e) {
                    CREDITS = "";
                }
            }
            return CREDITS;
        }
    }
}
