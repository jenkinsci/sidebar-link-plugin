package hudson.plugins.sidebar_link;

import hudson.model.Action;

/**
 * Simple link with settings from configured plugin.
 * @author Alan.Harder@sun.com
 */
public class LinkAction implements Action {
    private SidebarLinkPlugin plugin;

    LinkAction(SidebarLinkPlugin plugin) {
	this.plugin = plugin;
    }

    public String getUrlName() { return plugin.getUrl(); }
    public String getDisplayName() { return plugin.getText(); }
    public String getIconFileName() { return plugin.getIcon(); }
}
