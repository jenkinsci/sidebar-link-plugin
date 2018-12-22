/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.sidebar_link;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
        //from gui
        HtmlPage w = rule.createWebClient().goTo(project.getUrl() + "configure");
        HtmlForm form = w.getFormByName("config");
        form.getInputByName("sidebar-links").click();
        rule.submit(form);
        assertNotNull("Method geJobActions should not return null.", links.getJobActions(project));
    }

    @Test
    public void testProjectProperty() throws Exception {
        AbstractProject project = rule.jenkins.createProject(FreeStyleProject.class, "test" + rule.jenkins.getItems().size());
        ProjectLinks links = new ProjectLinks(null);
        project.addProperty(new ProjectLinks(Collections.singletonList(
                new LinkAction("http://example.com", "Side Bar Example", "")
        )));
        project.save();
        assertNotNull(project.getActions());
        LinkAction sideBarLinkAction = project.getAction(LinkAction.class);
        assertNotNull(sideBarLinkAction);
        assertEquals("http://example.com", sideBarLinkAction.getUrlName());
        assertEquals("Side Bar Example", sideBarLinkAction.getDisplayName());
        assertEquals("", sideBarLinkAction.getIconFileName());
    }

    @Test
    public void testWorkflowProperty() throws Exception {
        // A job need to build at least once so that the authorization is loaded
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "test" + rule.jenkins.getItems().size());
        p.setDefinition(new CpsFlowDefinition("" +
                "pipeline {\n" +
                "    agent any\n" +
                "    options { \n" +
                "        sidebarLinks([\n" +
                "           [displayName: 'Side Bar Example', iconFileName: '', urlName: 'http://example.com']\n" +
                "        ])" +
                "    }\n" +
                "    stages {\n" +
                "        stage('Example') {\n" +
                "            steps {\n" +
                "                echo 'Test'\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}", true));
        WorkflowRun b = p.scheduleBuild2(0).get();
        rule.assertBuildStatusSuccess(b);
        assertNotNull(p.getProperty(ProjectLinks.class));
        ProjectLinks projectLinks = p.getProperty(ProjectLinks.class);
        assertEquals(1, projectLinks.getLinks().size());
        LinkAction link = projectLinks.getLinks().get(0);
        assertEquals("http://example.com", link.getUrlName());
        assertEquals("Side Bar Example", link.getDisplayName());
        assertEquals("", link.getIconFileName());
    }
}
