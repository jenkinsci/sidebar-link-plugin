/*
 * The MIT License
 *
 * Copyright (c) 2004-2011, Sun Microsystems, Inc., Alan Harder
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

import hudson.FilePath;
import hudson.Plugin;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Add links in the main page sidepanel.
 * @author Alan Harder
 */
public class SidebarLinkPlugin extends Plugin {
    private List<LinkAction> links = new ArrayList<LinkAction>();

    // From older versions
    @Deprecated private transient String url, text, icon;

    @Override public void start() throws Exception {
	load();
        Jenkins.getInstance().getActions().addAll(links);
    }

    public List<LinkAction> getLinks() { return links; }

    @Override public void configure(StaplerRequest req, JSONObject formData)
	    throws IOException, ServletException, FormException {
        Jenkins.getInstance().getActions().removeAll(links);
	links.clear();
	links.addAll(req.bindJSONToList(LinkAction.class, formData.get("links")));
	save();
        Jenkins.getInstance().getActions().addAll(links);
    }

    private Object readResolve() {
	// Upgrade config from older version
	if (url != null && url.length() > 0) {
	    links.add(new LinkAction(url, text, icon));
	}
	return this;
    }

    /**
     * Receive file upload from startUpload.jelly.
     * File is placed in $JENKINS_HOME/userContent directory.
     */
    public void doUpload(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {
        Jenkins jenkins = Jenkins.getInstance();
        jenkins.checkPermission(Hudson.ADMINISTER);
        FileItem file = req.getFileItem("linkimage.file");
        String error = null, filename = null;
        if (file == null || file.getName().isEmpty())
            error = Messages.NoFile();
        else {
            filename = "userContent/"
                    // Sanitize given filename:
                    + file.getName().replaceFirst(".*/", "").replaceAll("[^\\w.,;:()#@!=+-]", "_");
            FilePath imageFile = jenkins.getRootPath().child(filename);
            if (imageFile.exists())
                error = Messages.DupName();
            else {
                imageFile.copyFrom(file.getInputStream());
                imageFile.chmod(0644);
            }
        }
        rsp.setContentType("text/html");
        rsp.getWriter().println(
                (error != null ? error : Messages.Uploaded("<tt>/" + filename + "</tt>"))
                + " <a href=\"javascript:history.back()\">" + Messages.Back() + "</a>");
    }
}
