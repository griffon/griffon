@artifact.package@class CreditsModel extends AbstractDialogModel {
    @Bindable String credits
    
    private static String CREDITS = null

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        credits = fetchCreditsText()
    }

    protected String getDialogKey() { 'Credits' }
    protected String getDialogTitle() { 'Credits' }

    @groovy.transform.Synchronized
    static fetchCreditsText() {
        if(CREDITS == null) {
            try {
                CREDITS = CreditsModel.class.getResource('/credits.txt').text
            } catch(x) {
                CREDITS = ''
            }
        }
        CREDITS
    }
}
