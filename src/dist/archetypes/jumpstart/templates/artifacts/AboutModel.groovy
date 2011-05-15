@artifact.package@import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.SortedList

class AboutModel extends AbstractDialogModel {
    EventList plugins = new SortedList(new BasicEventList(),
                 {a, b -> a.name <=> b.name} as Comparator)
    @Bindable String description
    boolean includeCredits = true
    boolean includeLicense = true

    protected String getDialogKey() { 'About' }
    protected String getDialogTitle() { 'About' }

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        resizable = false
        description = app.getMessage('application.dialog.About.description', [Metadata.current.getGriffonVersion()])
 
        List tmp = []
        for(String addonName : app.addonManager.addonDescriptors.keySet().sort()) {
            GriffonAddonDescriptor gad = app.addonManager.findAddonDescriptor(addonName)
            tmp << [name: gad.pluginName, version: gad.version]
        }
        plugins.addAll(tmp)
    }
}
