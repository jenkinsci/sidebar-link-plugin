package hudson.plugins.sidebar_link;

import hudson.Plugin;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import java.io.IOException;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;

/**
 * Simply add a link in the main page sidepanel.
 * @author Alan.Harder@sun.com
 */
public class SidebarLinkPlugin extends Plugin {
    private String url = "", text = "", icon = null;

    @Override public void start() throws Exception {
	load();
	Hudson.getInstance().getActions().add(new LinkAction(this));
    }

    public String getUrl() { return url; }
    public String getText() { return text; }
    public String getIcon() { return icon; }

    @Override public void configure(JSONObject formData)
	    throws IOException, ServletException, FormException {
	url = formData.optString("url");
	text = formData.optString("text");
	icon = formData.optString("icon");
	save();
    }
}
