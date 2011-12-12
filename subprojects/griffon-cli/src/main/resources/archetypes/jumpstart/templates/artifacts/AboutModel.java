@artifact.package@import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;
import griffon.core.GriffonAddonDescriptor;
import griffon.util.Metadata;

import java.util.*;

import griffon.plugins.i18n.MessageSourceHolder;

import static java.util.Arrays.asList;

public class AboutModel extends AbstractDialogModel {
    private EventList<Map<String, String>> plugins = new SortedList<Map<String, String>>(new BasicEventList<Map<String, String>>(),
            new Comparator<Map<String, String>>() {
                public int compare(Map<String, String> a, Map<String, String> b) {
                    String name1 = a.get("name");
                    String name2 = b.get("name");
                    return name1.compareTo(name2);
                }
            });
    private String description;
    private boolean includeCredits = true;
    private boolean includeLicense = true;

    protected String getDialogKey() {
        return "About";
    }

    protected String getDialogTitle() {
        return "About";
    }

    public EventList getPlugins() {
        return plugins;
    }

    public boolean isIncludeCredits() {
        return includeCredits;
    }

    public boolean isIncludeLicense() {
        return includeLicense;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        firePropertyChange("description", this.description, this.description = description);
    }

    public void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args);
        setResizable(false);
        setDescription(MessageSourceHolder.getMessageSource().getMessage("application.dialog.About.description", asList(Metadata.getCurrent().getGriffonVersion())));

        List<Map<String, String>> tmp = new ArrayList<Map<String, String>>();
        for (String addonName : getApp().getAddonManager().getAddonDescriptors().keySet()) {
            GriffonAddonDescriptor gad = getApp().getAddonManager().findAddonDescriptor(addonName);
            Map<String, String> plugin = new HashMap<String, String>();
            plugin.put("name", gad.getPluginName());
            plugin.put("version", gad.getVersion());
            tmp.add(plugin);
        }
        plugins.addAll(tmp);
    }
}
