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
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jenkins.model.TransientActionFactory;
import net.sf.json.JSONObject;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Add links in a job page sidepanel.
 * @author Alan Harder
 */
public class ProjectLinks extends JobProperty<Job<?,?>> {
    private List<LinkAction> links = new ArrayList<LinkAction>();

    @DataBoundConstructor
    public ProjectLinks(List<LinkAction> links) {
        if (links != null) {
            this.links = links;
        }
        else{
            this.links = new ArrayList<LinkAction>();
        }
    }

    public List<LinkAction> getLinks() { return links; }

    public Collection<? extends Action> getJobActions(AbstractProject<?,?> job) {
        if(links == null)
            return new ArrayList<LinkAction>();
        return links;
    }

    @Override
    public Collection<? extends Action> getJobActions(Job<?,?> job) {
        if (job instanceof AbstractProject) {
            return getJobActions((AbstractProject) job);
        }
        return Collections.EMPTY_SET;
    }

    private Object readResolve() {
        if (links == null) {
            links = new ArrayList<LinkAction>();
        }
        return this;
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

    @Restricted(NoExternalUse.class)
    @Extension(optional = true)
    public static class TransientActionFactoryImpl extends TransientActionFactory<Job> {
        @Override
        public Class<Job> type() {
            return Job.class;
        }

        public Collection<LinkAction> createFor(Job job) {
            if (job instanceof AbstractProject) {
                return Collections.EMPTY_SET;
            }
            ProjectLinks links = (ProjectLinks) job.getProperty(ProjectLinks.class);
            if (links == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(links.getLinks());
        }
    }
}
