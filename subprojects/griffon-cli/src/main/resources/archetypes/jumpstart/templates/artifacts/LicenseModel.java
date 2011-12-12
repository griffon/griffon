@artifact.package@import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Map;

public class LicenseModel extends AbstractDialogModel {
    private static String LICENSE = null;
    private static final Object[] lock = new Object[0];

    private String license;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        firePropertyChange("license", this.license, this.license = license);
    }

    public void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args);
        setWidth(600);
        setHeight(320);
        setResizable(false);
        setLicense(fetchLicenseText());
    }

    protected String getDialogKey() {
        return "License";
    }

    protected String getDialogTitle() {
        return "License";
    }

    public static String fetchLicenseText() {
        synchronized (lock) {
            if (LICENSE == null) {
                try {
                    LICENSE = DefaultGroovyMethods.getText(LicenseModel.class.getResource("/license.txt"));
                } catch (Exception e) {
                    LICENSE = "";
                }
            }
            return LICENSE;
        }
    }
}
