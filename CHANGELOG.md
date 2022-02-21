## Change Log

##### Version 2.1.0 (2022-02-17)

-   Fix icon upload iframe in global config
    ([PR #36](https://github.com/jenkinsci/sidebar-link-plugin/pull/36),
    fixes [#28](https://github.com/jenkinsci/sidebar-link-plugin/issues/28),
    fixes [JENKINS-55710](https://issues.jenkins.io/browse/JENKINS-55710))

##### Version 2.0.2 (2022-02-12)

-   Fix number hpi.forcompatibleSinceVersion
    ([PR #38](https://github.com/jenkinsci/sidebar-link-plugin/pull/38),
    fixes [#37](https://github.com/jenkinsci/sidebar-link-plugin/issues/37))

##### Version 2.0.1 (2022-02-10)

-   Add warning about new version
    ([PR #33](https://github.com/jenkinsci/sidebar-link-plugin/pull/33))
-   Update messages
    ([PR #34](https://github.com/jenkinsci/sidebar-link-plugin/pull/34))

##### Version 2.0.0 (2022-02-08)

:warning: Starting in this version, the plugin configuration is in the
`hudson.plugins.sidebar_link.SidebarLinkPlugin.xml` file on the Jenkins
controller.  Previous versions used `sidebar-link.xml` instead.

-   Remove Travis, add Jenkins badge
    ([PR #29](https://github.com/jenkinsci/sidebar-link-plugin/pull/29))
-   Remove outdated Codacy badge
    ([PR #30](https://github.com/jenkinsci/sidebar-link-plugin/pull/30))
-   Tweaks to use of Java generics
    ([PR #32](https://github.com/jenkinsci/sidebar-link-plugin/pull/32))
-   Adding JCasC support
    ([PR #25](https://github.com/jenkinsci/sidebar-link-plugin/pull/25),
    fixes [JENKINS-63149](https://issues.jenkins.io/browse/JENKINS-63149))

##### Version 1.12.1 (2021-10-24)

-   Hacktoberfest docs-to-code migration
    ([PR #27](https://github.com/jenkinsci/sidebar-link-plugin/pull/27),
    mostly fixes [#21](https://github.com/jenkinsci/sidebar-link-plugin/issues/21))

##### Version 1.12.0 (2021-03-14)

-   Integrates with TravisCI
    ([PR #15](https://github.com/jenkinsci/sidebar-link-plugin/pull/15))
-   Fixes broken link to badge
    ([PR #16](https://github.com/jenkinsci/sidebar-link-plugin/pull/16))
-   Adds Codacy badge, removes reported issues
    ([PR #18](https://github.com/jenkinsci/sidebar-link-plugin/pull/18))
-   Adds integration with Appveyor
    ([PR #19](https://github.com/jenkinsci/sidebar-link-plugin/pull/19))
-   Use HTTPS URLs in pom.xml
    ([PR #20](https://github.com/jenkinsci/sidebar-link-plugin/pull/20))
-   Migrate to JDK 11
    ([PR #24](https://github.com/jenkinsci/sidebar-link-plugin/pull/24))

##### Version 1.11.0 (2019-02-13)

-   Updates pom.xml, adds .gitignore, license
    ([PR #12](https://github.com/jenkinsci/sidebar-link-plugin/pull/12))
-   Upgrades dependencies and JDK to version 8
    ([PR #13](https://github.com/jenkinsci/sidebar-link-plugin/pull/13))
-   Reformats code to Java standards
    ([PR #14](https://github.com/jenkinsci/sidebar-link-plugin/pull/14))

##### Version 1.10 (2018-12-22)

-   Add support of Sidebar links for Jenkins Pipeline and other project
    types
    ([JENKINS-33458](https://issues.jenkins-ci.org/browse/JENKINS-33458))

##### Version 1.9.1 (2017-07-12)

-   Fix displaying of saved URL values in the configuration pages
    ([JENKINS-45451](https://issues.jenkins-ci.org/browse/JENKINS-45451),
    regression in 1.9)

##### Version 1.9 (2017-07-10)

-   [Fix security
    issue](https://jenkins.io/security/advisory/2017-07-10/)

##### Version 1.6 (2011-07-24)

-   Remove stray "\>" showing up on job config page.
-   Update for Jenkins.

##### Version 1.5 (2011-01-20)

-   Add option for sidebar links in project pages.
    ([JENKINS-7298](https://issues.jenkins-ci.org/browse/JENKINS-7298))
-   Add ability to upload image files into `JENKINS_HOME/userContent`
    directory from global config page.
    ([JENKINS-8320](https://issues.jenkins-ci.org/browse/JENKINS-8320))
-   Japanese translation

##### Version 1.4 (2010-03-08)

-   Add help text about how to use icon images placed in
    `HUDSON_HOME/userContent` directory, and for absolute vs
    in-this-Hudson URLs.

##### Version 1.3 (2010-02-10)

-   Update code for more recent Hudson.

##### Version 1.2 (2009-05-23)

-   Support for more than one link.

##### Version 1.1 (2009-01-13)

-   Avoid NullPointerException when plugin is installed but not
    configured.

##### Version 1.0 (2008-12-17)

-   Initial release.