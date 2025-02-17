/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Alan Harder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.sidebar_link;

import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlForm;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test interaction of sidebar-link plugin with Jenkins core.
 *
 * @author Alan Harder
 */
@WithJenkins
class SidebarLinkTest {

    @Test
    void testPlugin(JenkinsRule rule) throws Exception {
        JenkinsRule.WebClient wc = rule.createWebClient();

        rule.jenkins.getActions().add(new LinkAction("http://test.com/test", "Test Link", "test.gif"));

        // Verify link appears on main page
        HtmlAnchor link = wc.goTo("").getAnchorByText("Test Link");
        assertNotNull(link, "link missing on main page");
        assertEquals("http://test.com/test", link.getHrefAttribute(), "main page href");

        // Create view and verify link appears on other view tabs too
        HtmlForm form = wc.goTo("newView").getFormByName("createItem");
        form.getInputByName("name").setValue("test-view");
        form.getInputByValue("hudson.model.ListView").setChecked(true);
        rule.submit(form);
        link = wc.goTo("view/test-view/").getAnchorByText("Test Link");
        assertNotNull(link, "link missing on view page");
        assertEquals("http://test.com/test", link.getHrefAttribute(), "view page href");
    }

}
