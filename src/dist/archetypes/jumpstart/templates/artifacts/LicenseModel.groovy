@artifact.package@class LicenseModel extends AbstractDialogModel {
    @Bindable String license
    
    private static String LICENSE = null

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        width = 600
        height = 320
        resizable = false
        license = fetchLicenseText()
    }

    protected String getDialogKey() { 'License' }
    protected String getDialogTitle() { 'License' }
    
    @groovy.transform.Synchronized
    static fetchLicenseText() {
        if(LICENSE == null) {
            try {
                LICENSE = LicenseModel.class.getResource('/license.txt').text
            } catch(x) {
                LICENSE = ''
            }
        }
        LICENSE
    }
}
