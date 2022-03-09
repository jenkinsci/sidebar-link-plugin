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

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.net.URI;

import hudson.Functions;
import hudson.model.Action;
import hudson.util.FormValidation;
import io.jenkins.cli.shaded.org.apache.commons.lang.StringUtils;

import javax.annotation.CheckForNull;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Simple link.
 *
 * @author Alan Harder, Simon Michalke
 */
public class LinkAction implements Action {

    private final String url, text, icon;
    private final boolean addReferer, refererFullPath;

    /**
     * Caches the fact that the URL is safe or not.
     * It assumes that {@link LinkProtection#ALLOWED_URI_SCHEMES} is assigned once and only once on the startup.
     */
    @CheckForNull
    private transient Boolean isSafe = null;
    private static final Logger LOGGER = Logger.getLogger(LinkAction.class.getName());

    @DataBoundConstructor
    public LinkAction(String urlName, String displayName, String iconFileName, Boolean addReferer, Boolean refererFullPath) throws IllegalArgumentException {
        // Validate URL before proceeding
        FormValidation validationResult = LinkProtection.verifyUrl(urlName);
        if (validationResult.kind == FormValidation.Kind.ERROR) {
            throw new IllegalArgumentException(validationResult);
        }

        if(StringUtils.isBlank(iconFileName)) {
            iconFileName = "static/efbf17e4/images/16x16/help.png";
        }

        this.url = urlName;
        this.text = displayName;
        this.icon = iconFileName;
        LOGGER.info(String.format("Created link '%s': url='%s', icon='%s'", this.text, this.url, this.icon));
        this.addReferer = addReferer == null ? false : addReferer;
        this.refererFullPath = refererFullPath == null ? false : refererFullPath;
    }

    public String getUrlName() {
        String retUrl = url;
        if (addReferer) {
            String from = refererFullPath ?
                Stapler.getCurrentRequest().getRequestURL().toString() :
                Stapler.getCurrentRequest().getPathInfo();
            if (retUrl.contains("?")) {
                retUrl += "&from=" + from;
            } else {
                retUrl += "?from=" + from;
            }
        }
        if (isSafe == null) { // Refresh cache
            FormValidation validationResult = LinkProtection.verifyUrl(retUrl);
            isSafe = validationResult.kind != FormValidation.Kind.ERROR;
        }
        return isSafe ? retUrl : "unsafeLink-" + retUrl.hashCode();
    }

    public String getDisplayName() {
        return text;
    }

    public String getIconFileName() {
        return icon;
    }

    @Restricted(NoExternalUse.class)
    public String getUnprotectedUrlName() {
        return url;
    }

    @Restricted(NoExternalUse.class)
    public Object getAncestor() {
        StaplerRequest currentRequest = Stapler.getCurrentRequest();
        if (currentRequest != null) {
            List<Ancestor> ancestors = currentRequest.getAncestors();
            int listSize = ancestors != null ? ancestors.size() : 0;
            if (ancestors == null || listSize < 2) {
                return null;
            }
            // One level above the LinkAction
            return ancestors.get(listSize - 2).getObject();
        }

        return null;
    }

    @Restricted(NoExternalUse.class)
    public String getAllowedSchemes() {
        return LinkProtection.getAllowedUriSchemes();
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof LinkAction)) {
            return false;
        }

        LinkAction c = (LinkAction) o;
        return text.equals(c.text)
                && icon.equals(c.icon)
                && url.equals(c.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, icon, url);
    }
}
