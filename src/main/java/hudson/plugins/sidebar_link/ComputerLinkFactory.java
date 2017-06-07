package hudson.plugins.sidebar_link;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Computer;
import hudson.model.Node;
import jenkins.model.TransientActionFactory;

import java.util.ArrayList;
import java.util.Collection;

@Extension
public class ComputerLinkFactory extends TransientActionFactory<Computer> {

    @Override
    public Class<Computer> type() {
        return Computer.class;
    }

    @Override
    public Collection<? extends Action> createFor(Computer target) {
        Node node = target.getNode();
        if (node == null) {
            return new ArrayList<LinkAction>();
        }

        NodeLinks prop = node.getNodeProperties().get(NodeLinks.class);
        if (prop == null) {
            return new ArrayList<LinkAction>();
        }

        return prop.getLinks();
    }
}
