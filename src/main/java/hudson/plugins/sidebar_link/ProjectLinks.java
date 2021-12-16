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
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import jenkins.model.TransientActionFactory;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Add links in a job page sidepanel.
 *
 * @author Alan Harder
 */
@SuppressWarnings("rawtypes")
public class ProjectLinks extends JobProperty<Job<?, ?>> {
    private List<LinkAction> links = new ArrayList<>();

    @DataBoundConstructor
    public ProjectLinks(List<LinkAction> links) {
        if (links != null) {
            this.links = links;
        } else {
            this.links = new ArrayList<>();
        }
    }

    public List<LinkAction> getLinks() {
        return links;
    }

    @Override
    public Collection<? extends Action> getJobActions(Job<?, ?> job) {
        return Collections.emptyList();
    }

    @Extension
    @Symbol("sidebarLinks")
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

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }
    }


    /**
     * The action factory responsible for adding the {@link LinkAction}.
     *
     * @since 1.3.0
     */
    @Extension
    public static class TransientActionFactoryImpl extends TransientActionFactory<Job> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<Job> type() {
            return Job.class;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Collection<? extends Action> createFor(@Nonnull Job target) {
            ProjectLinks sideBarLinksProperty = ((Job<?, ?>) target).getProperty(ProjectLinks.class);
            if (sideBarLinksProperty != null) {
                return sideBarLinksProperty.getLinks();
            } else {
                return Collections.emptyList();
            }
        }
    }
}
