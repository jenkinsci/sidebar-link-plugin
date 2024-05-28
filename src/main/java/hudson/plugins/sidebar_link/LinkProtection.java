/*
 * The MIT License
 *
 * Copyright (c) 2017 CloudBees, Inc.
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import hudson.util.FormValidation;
import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Contains shared utility logic for the plugin.
 * @author Oleg Nenashev
 */
@Restricted(NoExternalUse.class)
class LinkProtection {

    /**
     * Defines a list of URL schemes, which are considered to be safe.
     * Default set comes from https://security.stackexchange.com/questions/148428/which-url-schemes-are-dangerous-xss-exploitable.
     */
    private static final Set<String> ALLOWED_URI_SCHEMES = new HashSet<String>();

    static {
        // Cannot be instantinated
        String customSchemes = System.getProperty(LinkProtection.class.getName() + ".whitelistedSchemes");
        if (customSchemes != null) {
            String[] schemes = customSchemes.split("\\s*,\\s*");
            ALLOWED_URI_SCHEMES.addAll(Arrays.asList(schemes));
        } else {
            ALLOWED_URI_SCHEMES.addAll(
                Arrays.asList("http", "https", "ftp", "ftps", "mailto", "news", "irc",
                "gopher", "nntp", "feed", "telnet", "mms", "rtsp", "svn", "tel", "fax", "xmpp"));
        }
    }

    @Nonnull
    public static String getAllowedUriSchemes() {
        return String.join(",", ALLOWED_URI_SCHEMES);
    }

    @CheckReturnValue
    @Nonnull
    public static FormValidation verifyUrl(@CheckForNull String urlString) {
        if (urlString == null || urlString.isBlank()) {
            return FormValidation.warning("The provided URL is blank or empty");
        }

        // Copy of the code from Functions#getActionUrl()
        final URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException ex) {
            return FormValidation.error(ex, "The provided URL is malformed: " + urlString);
        }

        // Let's check if the scheme is allowed
        String scheme = uri.getScheme();
        if (scheme != null) { // we do not check undefined schemes like relative links
            String toCheck = scheme.toLowerCase();
            if (!ALLOWED_URI_SCHEMES.contains(toCheck)) {
                StringBuilder bldr = new StringBuilder("URI scheme \"");
                bldr.append(toCheck).append("\" is not allowed. Allowed schemes: ");
                bldr.append(getAllowedUriSchemes());
                return FormValidation.error(bldr.toString());
            }
        }

        if (uri.isAbsolute()) {
            return FormValidation.ok("This is a valid absolute URL. The destination may not exist");
        } else {
            return FormValidation.ok("This is a valid relative URL. The destination may not exist");
        }
    }
}
