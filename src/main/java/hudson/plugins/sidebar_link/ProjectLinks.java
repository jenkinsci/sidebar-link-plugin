/*
 * The MIT License
 *
 * Copyright (c) 2011, Alan Harder
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

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Add links in a job page sidepanel.
 * @author Alan Harder
 */
public class ProjectLinks extends JobProperty<AbstractProject<?,?>> {
    private List<LinkAction> links = new ArrayList<LinkAction>();

    @DataBoundConstructor
    public ProjectLinks(List<LinkAction> links) {
        this.links = links;
    }

    public List<LinkAction> getLinks() { return links; }

    @Override
    public Collection<? extends Action> getJobActions(AbstractProject<?,?> job) {
        return links;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Sidebar Links";
        }

        @Override
        public ProjectLinks newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return formData.has("sidebar-links")
                    ? req.bindJSON(ProjectLinks.class, formData.getJSONObject("sidebar-links"))
                    : null;
        }
    }
}
