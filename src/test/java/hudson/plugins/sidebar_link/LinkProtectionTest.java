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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.util.FormValidation;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the {@link LinkProtection} engine.
 *
 * @author Oleg Nenashev
 */
@WithJenkins
class LinkProtectionTest {

    @Test
    void shouldAcceptAbsoluteLinks(JenkinsRule rule) {
        assertLinkIsAccepted(rule, "http://localhost:8080/jenkins");
        assertLinkIsAccepted(rule, "HTTP://localhost:8080/jenkins");
        assertLinkIsAccepted(rule, "https://localhost:8080/jenkins");
        assertLinkIsAccepted(rule, "https://localhost:8080");
        assertLinkIsAccepted(rule, "https://localhost:8080/jenkins?myparam=1&myparam2=value");
        assertLinkIsAccepted(rule, "mailto:my@nonexistentinbox.com");

        // Java Web Start should always ask for confirmation hence it is probably fine
        assertLinkIsAccepted(rule, "https://localhost:8080/computer/agent1/my.jnlp");
    }

    @Test
    void shouldAcceptRelativeLinks(JenkinsRule rule) {
        assertLinkIsAccepted(rule, "computer");
        assertLinkIsAccepted(rule, "/computer");
        assertLinkIsAccepted(rule, "../computer");
    }

    @Test
    @Issue("SECURITY-352")
    void shouldNotAcceptNonUrlLinks(JenkinsRule rule) {
        // Javascript
        assertSchemeIsNotAccepted(rule, "javascript:alert(document.domain)", "javascript");
        assertSchemeIsNotAccepted(rule, "JAVASCRIPT:alert(document.domain)", "javascript");

        // File access
        assertSchemeIsNotAccepted(rule, "file:///var/lib/myfile.html", "file");

        // Whatever custom scheme (e.g. on smartphone browsers)
        assertSchemeIsNotAccepted(rule, "twitter://iamhacked_lol", "twitter");
    }

    /**
     * Checks if the validation accepts the specified URL.
     * Validation warnings are fine
     *
     * @param url URL
     */
    private static void assertLinkIsAccepted(JenkinsRule rule, @CheckForNull String url) {
        SidebarLinkPlugin descriptor = rule.jenkins.getDescriptorByType(SidebarLinkPlugin.class);
        FormValidation validationResult = descriptor.doCheckLinkUrl(url);
        assertThat("Expected the validation of link '" + "' to pass, but got " + validationResult,
                validationResult.kind, not(equalTo(FormValidation.Kind.ERROR)));

        new LinkAction(url, "test link", null);
    }

    private static void assertSchemeIsNotAccepted(JenkinsRule rule, @CheckForNull String url, String scheme) {
        assertLinkIsNotAccepted(rule, url, "URI scheme &quot;" + scheme + "&quot; is not allowed.");
    }

    /**
     * Checks that the URL validation does not the specified URL.
     *
     * @param url                 URL
     * @param expectedMessagePart Expected error message segment
     */
    private static void assertLinkIsNotAccepted(JenkinsRule rule, @CheckForNull String url, @CheckForNull String expectedMessagePart) {
        // Try Form validation first
        SidebarLinkPlugin descriptor = rule.jenkins.getDescriptorByType(SidebarLinkPlugin.class);
        FormValidation validationResult = descriptor.doCheckLinkUrl(url);
        assertThat("Expected the validation of link '" + url + "' to fail, but got " + validationResult,
                validationResult.kind, equalTo(FormValidation.Kind.ERROR));
        if (expectedMessagePart != null) {
            assertThat("Expected another error message",
                    validationResult.getMessage(), StringContains.containsString(expectedMessagePart));
        }

        // Try to instantiate LinkAction
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new LinkAction(url, "test URL", null),
                "Expected the LinkAction constructor to throw an exception");
        assertThat(ex.getCause(), instanceOf(FormValidation.class));
        FormValidation res = (FormValidation) ex.getCause();
        assertThat("Expected the validation of link '" + url + "' to fail in the LinkAction, but got " + res,
                res.kind, equalTo(FormValidation.Kind.ERROR));
        if (expectedMessagePart != null) {
            assertThat("Expected another error message",
                    res.getMessage(), StringContains.containsString(expectedMessagePart));
        }
    }
}
