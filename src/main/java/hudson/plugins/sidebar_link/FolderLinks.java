/*
 * The MIT License
 *
 * Copyright (c) 2014, Daniel Beck
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
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.FolderProperty;
import com.cloudbees.hudson.plugins.folder.FolderPropertyDescriptor;
import com.cloudbees.hudson.plugins.folder.TransientFolderActionFactory;

/**
 * Add links in a folder page sidepanel.
 * @author Daniel Beck
 */
public class FolderLinks extends FolderProperty<Folder> {
    private List<LinkAction> links = new ArrayList<LinkAction>();

    @DataBoundConstructor
    public FolderLinks(List<LinkAction> links) {
        this.links = links;
    }

    public List<LinkAction> getLinks() { return links; }

    @Extension(optional = true)
    public static class DescriptorImpl extends FolderPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Sidebar Links";
        }
    }

    @Extension(optional = true)
    public static class TransientFolderActionFactoryImpl extends TransientFolderActionFactory {
        public Collection<LinkAction> createFor(Folder target) {
            FolderLinks fl = target.getProperties().get(FolderLinks.class);
            if (fl == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(fl.getLinks());
        }
    }
}
