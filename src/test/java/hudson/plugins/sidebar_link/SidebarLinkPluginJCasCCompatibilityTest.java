package hudson.plugins.sidebar_link;

import static org.junit.Assert.assertEquals;

import org.jvnet.hudson.test.RestartableJenkinsRule;

import io.jenkins.plugins.casc.misc.RoundTripAbstractTest;
import jenkins.model.Jenkins;

public class SidebarLinkPluginJCasCCompatibilityTest extends RoundTripAbstractTest {
    @Override
    protected void assertConfiguredAsExpected(RestartableJenkinsRule restartableJenkinsRule, String s) {
        final Jenkins jenkins = Jenkins.get();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins instance is not ready");
        }
        SidebarLinkPlugin descriptor = jenkins.getDescriptorByType(SidebarLinkPlugin.class);
        descriptor.getLinks().forEach((temp) -> {
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
