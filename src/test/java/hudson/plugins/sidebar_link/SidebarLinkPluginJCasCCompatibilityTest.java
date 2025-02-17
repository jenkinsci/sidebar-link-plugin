package hudson.plugins.sidebar_link;

import io.jenkins.plugins.casc.misc.junit.jupiter.AbstractRoundTripTest;
import jenkins.model.Jenkins;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithJenkins
public class SidebarLinkPluginJCasCCompatibilityTest extends AbstractRoundTripTest {
    @Override
    protected void assertConfiguredAsExpected(JenkinsRule restartableJenkinsRule, String s) {
        final Jenkins jenkins = Jenkins.get();
        SidebarLinkPlugin descriptor = jenkins.getDescriptorByType(SidebarLinkPlugin.class);
        descriptor.getLinks().forEach(temp -> {
            assertEquals("icon-help icon-md", temp.getIconFileName());
            assertEquals("testlink", temp.getDisplayName());
            assertEquals("www.none.com", temp.getUrlName());
        });
    }

    @Override
    protected String stringInLogExpected() {
        return "Setting class hudson.plugins.sidebar_link.LinkAction.urlName =";
    }

    @Override
    protected String configResource() {
        return "RoundTrip.yml";
    }
}
