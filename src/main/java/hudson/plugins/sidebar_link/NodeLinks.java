package hudson.plugins.sidebar_link;

import hudson.Extension;
import hudson.model.*;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

public class NodeLinks extends NodeProperty<Slave> {

    private List<LinkAction> links = new ArrayList<LinkAction>();

    @DataBoundConstructor
    public NodeLinks(List<LinkAction> links) {
        if (links != null) {
            this.links = links;
        }
    }

    public List<LinkAction> getLinks() {
        return links;
    }

    private Object readResolve() {
        if (links == null) {
            links = new ArrayList<>();
        }
        return this;
    }

    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Sidebar Links";
        }
    }
}
