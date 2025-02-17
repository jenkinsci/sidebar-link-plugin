/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.sidebar_link;

import hudson.model.FreeStyleProject;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Lucie Votypkova
 */
@WithJenkins
class ProjectLinksTest {

    @Test
    void testGetJobActionsDoesNotReturnNull(JenkinsRule rule) throws Exception {
        FreeStyleProject project = rule.jenkins.createProject(FreeStyleProject.class, "test_not_null");
        ProjectLinks links = new ProjectLinks(null);
        assertNotNull(links.getJobActions(project), "Method geJobActions should not return null.");
        //from gui
        HtmlPage w = rule.createWebClient().goTo(project.getUrl() + "configure");
        HtmlForm form = w.getFormByName("config");
        form.getInputByName("sidebar-links").click();
        rule.submit(form);
        assertNotNull(links.getJobActions(project), "Method geJobActions should not return null.");
    }

    @Test
    void testProjectProperty(JenkinsRule rule) throws Exception {
        FreeStyleProject project = rule.jenkins.createProject(FreeStyleProject.class, "test" + rule.jenkins.getItems().size());
        project.addProperty(new ProjectLinks(Collections.singletonList(
                new LinkAction("http://example.com", "Side Bar Example", "")
        )));
        project.save();
        assertNotNull(project.getActions());
        LinkAction sideBarLinkAction = project.getAction(LinkAction.class);
        assertNotNull(sideBarLinkAction);
        assertEquals("http://example.com", sideBarLinkAction.getUrlName());
        assertEquals("Side Bar Example", sideBarLinkAction.getDisplayName());
        assertEquals("icon-help icon-md", sideBarLinkAction.getIconFileName());
    }

    @Test
    void testWorkflowProperty(JenkinsRule rule) throws Exception {
        // A job need to build at least once so that the authorization is loaded
        WorkflowJob p = rule.jenkins.createProject(WorkflowJob.class, "test" + rule.jenkins.getItems().size());
        p.setDefinition(new CpsFlowDefinition("""
                pipeline {
                    agent any
                    options {
                        sidebarLinks([
                           [displayName: 'Side Bar Example', iconFileName: '', urlName: 'http://example.com']
                        ])
                    }
                    stages {
                        stage('Example') {
                            steps {
                                echo 'Test'
                            }
                        }
                    }
                }""", true));
        WorkflowRun b = p.scheduleBuild2(0).get();
        rule.assertBuildStatusSuccess(b);
        assertNotNull(p.getProperty(ProjectLinks.class));
        ProjectLinks projectLinks = p.getProperty(ProjectLinks.class);
        assertEquals(1, projectLinks.getLinks().size());
        LinkAction link = projectLinks.getLinks().get(0);
        assertEquals("http://example.com", link.getUrlName());
        assertEquals("Side Bar Example", link.getDisplayName());
        assertEquals("icon-help icon-md", link.getIconFileName());
    }
}
