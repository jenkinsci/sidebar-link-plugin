/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.sidebar_link;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 *
 * @author Lucie Votypkova
 */
public class ProjectLinksTest {
    
    @Rule
    public JenkinsRule rule = new JenkinsRule();
    
    @Test
    public void testGetJobActionsDoesNotReturnNull() throws Exception{
        AbstractProject project = rule.jenkins.createProject(FreeStyleProject.class, "test_not_null");
        ProjectLinks links = new ProjectLinks(null);
        Assert.assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
        //from gui
        HtmlPage w = rule.createWebClient().goTo(project.getUrl() + "configure");
        HtmlForm form = w.getFormByName("config");
        form.getInputByName("sidebar-links").click();
        rule.submit(form);
        Assert.assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
    }
    
}
