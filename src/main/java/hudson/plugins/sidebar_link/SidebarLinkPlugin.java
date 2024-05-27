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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
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

    private List<LinkAction> links = new ArrayList<>();

    public SidebarLinkPlugin() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
        Jenkins.get().getActions().addAll(links);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        super.configure(req, json);
        if (!json.containsKey("links")) {
            setLinks(Collections.emptyList());
        }
        return true;
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
                (error != null ? error : Messages.Uploaded("<code>/" + filename + "</code>"))
                        + " <a href=\"javascript:history.back()\">" + Messages.Back() + "</a>");
    }

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkUrl(@QueryParameter String value) {
        return LinkProtection.verifyUrl(value);
    }

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkText(@QueryParameter String value) {
        if (value == null || value.isBlank()) {
            return FormValidation.error("The provided text is blank or empty");
        }
        return FormValidation.ok();
    }

    @Restricted(NoExternalUse.class)
    public FormValidation doCheckLinkIcon(@QueryParameter String value) {
        // use default icon when value is not provided
        if (value == null || value.isBlank()) {
            return FormValidation.warning("The provided icon is blank or empty. "
                    + "Default will be used: " + LinkAction.DEFAULT_ICON_NAME);
        }

        // do not validate if default icon is used
        if (value.equals(LinkAction.DEFAULT_ICON_NAME)) {
            return FormValidation.ok();
        }

        // icons supported out of the box by Jenkins
        if (isAcceptedIconName(value)) {
            return FormValidation.ok();
        }

        try {
            File jenkinsHomeDirectory = Jenkins.get().getRootDir();
            String userContentDirectory = jenkinsHomeDirectory.getCanonicalPath() + File.separatorChar + "userContent" + File.separatorChar;
            File imageFile = new File(jenkinsHomeDirectory, value);

            String canonicalPath = imageFile.getCanonicalPath();
            if (!canonicalPath.startsWith(userContentDirectory)) {
                return FormValidation.error("Use path to JENKINS_HOME/userContent directory, eg. userContent/myIcon.png");
            }

            if (!imageFile.exists()) {
                return FormValidation.error("Image does not exist. "
                        + "Open browser with /userContent to check available images");
            }
        } catch (IOException e) {
            return FormValidation.error(e, "Problem with link icon:  " + value);
        }
        return FormValidation.ok();
    }

    /**
     * Validates if passed icon may be supported by Jenkins.
     * It accepts values like <code>document</code>, <code>disabled.gif</code> or <code>document-properties.svg</code>.
     * List of supported icons located in JENKINS_HOME/var/images directory.
     */
    public boolean isAcceptedIconName(String iconName) {
        // accepts .gif extension as a compatibility with older version of the plugin
        return iconName.matches(("^[\\w-]+(\\.gif)?$"));
    }
}
