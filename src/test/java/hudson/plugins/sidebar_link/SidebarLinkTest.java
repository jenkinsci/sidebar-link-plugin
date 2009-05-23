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

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.jvnet.hudson.test.HudsonTestCase;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Test interaction of sidebar-link plugin with Hudson core.
 * @author Alan.Harder@sun.com
 */
public class SidebarLinkTest extends HudsonTestCase {

    public void testPlugin() throws Exception {
        WebClient wc = new WebClient();

        // Configure plugin
        // (don't know how to use htmlunit with f:repeatable, so calling configure directly)
        //HtmlForm form = wc.goTo("configure").getFormByName("config");
        //..
        //submit(form);

        Hudson.getInstance().getActions().add(new SidebarLinkTestAction("SidebarLinkTest"));
        // This calls action class below to call configure() (needs to be in a Stapler context)
        wc.goTo("SidebarLinkTest");

        // Verify link appears on main page
        HtmlAnchor link = wc.goTo("").getFirstAnchorByText("Test Link");
        assertNotNull("link missing on main page", link);
        assertEquals("main page href", "http://test.com/test", link.getHrefAttribute());

        // Create view and verify link appears on other view tabs too
        HtmlForm form = wc.goTo("newView").getFormByName("createView");
        form.getInputByName("name").setValueAttribute("test-view");
        form.getInputByValue("hudson.model.ListView").setChecked(true);
        submit(form);
        link = wc.goTo("view/test-view/").getFirstAnchorByText("Test Link");
        assertNotNull("link missing on view page", link);
        assertEquals("view page href", "http://test.com/test", link.getHrefAttribute());
    }

    public class SidebarLinkTestAction extends LinkAction {
        public SidebarLinkTestAction(String name) { super(name, "test", null); }
        public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws Exception {
            JSONObject formData = new JSONObject();
            formData.put("links", JSONObject.fromObject(
                new LinkAction("http://test.com/test", "Test Link", "test.gif")));
            Hudson.getInstance().getPlugin(SidebarLinkPlugin.class).configure(formData);
            rsp.setContentType("text/html");
            rsp.getOutputStream().close();
        }
    }
}
