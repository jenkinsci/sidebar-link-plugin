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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

/**
 * Add links in the main page sidepanel.
 *
 * @author Alan Harder
 */

@Extension
@Symbol("sidebarGlobalLink")
public class SidebarLinkPlugin extends GlobalConfiguration {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(SidebarLinkPlugin.class.getName());

    private List<LinkAction> links = new ArrayList<>();

    public SidebarLinkPlugin() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
        Jenkins.get().getActions().addAll(links);
    }

    /** @return the currently configured links, if any */
    public List<LinkAction> getLinks() {
        return links;
    }

    /**
     * Together with {@link #getLinks}, binds to entry in {@code config.jelly}.
     * @param links the new value of this field
     */
    @DataBoundSetter
    public void setLinks(List<LinkAction> links) {
        /* remove all links of this type */
        Jenkins.get().getActions().removeAll(this.links);
        this.links.clear();
        this.links.addAll(links);
        Jenkins.get().getActions().addAll(this.links);
        save();
    }

    /**
     * Receive file upload from startUpload.jelly.
     * File is placed in $JENKINS_HOME/userContent directory.
     */
    @RequirePOST
    @Restricted(NoExternalUse.class)
    public void doUploadLinkImage(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {
        Jenkins jenkins = Jenkins.get();
        jenkins.checkPermission(Hudson.ADMINISTER);
        FileItem file = req.getFileItem("linkimage.file");
        String error = null;
        String filename = null;
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

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkUrl(@QueryParameter String value) {
        return LinkProtection.verifyUrl(value);
    }

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkText(@QueryParameter String value) {
        if (StringUtils.isBlank(value)) {
            return FormValidation.error("The provided text is blank or empty");
        }
        return FormValidation.ok();
    }

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkIcon(@QueryParameter String value) {
        if (StringUtils.isBlank(value)) {
            return FormValidation.warning("The provided icon is blank or empty. Defautl will used.");
        }
        FilePath imageFile = Jenkins.get().getRootPath().child(value);
        try {
            if (!imageFile.exists()) {
                return FormValidation.error("Image does not exists:  " + imageFile);
            }
        } catch (Exception e) {
            return FormValidation.error(e, "Problems using link icon:  " + value);
        }
        return FormValidation.ok();
    }
}
