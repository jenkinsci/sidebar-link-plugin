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

import hudson.Plugin;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Stapler;

/**
 * Simply add a link in the main page sidepanel.
 * @author Alan.Harder@sun.com
 */
public class SidebarLinkPlugin extends Plugin {
    private List<LinkAction> links = new ArrayList<LinkAction>();

    // From older versions
    @Deprecated private transient String url, text, icon;

    @Override public void start() throws Exception {
	load();
	Hudson.getInstance().getActions().addAll(links);
    }

    public List<LinkAction> getLinks() { return links; }

    @Override public void configure(JSONObject formData)
	    throws IOException, ServletException, FormException {
	Hudson.getInstance().getActions().removeAll(links);
	links.clear();
	links.addAll(Stapler.getCurrentRequest().bindJSONToList(
	    LinkAction.class, formData.get("links")));
	save();
	Hudson.getInstance().getActions().addAll(links);
    }

    private Object readResolve() {
	// Upgrade config from older version
	if (url != null && url.length() > 0) {
	    links.add(new LinkAction(url, text, icon));
	}
	return this;
    }
}
