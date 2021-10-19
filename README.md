[![Build Travis](https://img.shields.io/travis/jenkinsci/sidebar-link-plugin/master.svg)](https://travis-ci.org/jenkinsci/sidebar-link-plugin)
[![Build Appveyor](https://ci.appveyor.com/api/projects/status/td957y8vrmb76ggt?svg=true)](https://ci.appveyor.com/project/damianszczepanik/sidebar-link-plugin)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c45db3b6680e4fc4ae114253be3dc2b3)](https://www.codacy.com/app/damianszczepanik/sidebar-link-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jenkinsci/sidebar-link-plugin&amp;utm_campaign=Badge_Grade)

# Sidebar Link Plug-in for Jenkins

Add links in the sidebar of the Jenkins main page, view tabs and project pages.

This simple plugin adds an Additional Sidebar Links section in the main Jenkins configuration page, with settings for link URLs, texts and icons. These links will be shown in the top-level Jenkins pages (main page, user list, build history, My Projects and other project view tabs). Sidebar links for particular jobs may also be added in the job configuration pages.

------------------------------------------------------------------------

## Change Log

##### Version 1.10 (Dec 22, 2018)

-   Add support of Sidebar links for Jenkins Pipeline and other project
    types
    ([JENKINS-33458](https://issues.jenkins-ci.org/browse/JENKINS-33458))

##### Version 1.9.1 (July 12, 2017)

-   Fix displaying of saved URL values in the configuration pages
    ([JENKINS-45451](https://issues.jenkins-ci.org/browse/JENKINS-45451),
    regression in 1.9)

##### Version 1.9 (July 10, 2017)

-   [Fix security
    issue](https://jenkins.io/security/advisory/2017-07-10/)

##### Version 1.6 (24-Jul-2011)

-   Remove stray "\>" showing up on job config page.
-   Update for Jenkins.

##### Version 1.5 (20-Jan-2011)

-   Add option for sidebar links in project pages.
    ([JENKINS-7298](https://issues.jenkins-ci.org/browse/JENKINS-7298))
-   Add ability to upload image files into `JENKINS_HOME/userContent`
    directory from global config page.
    ([JENKINS-8320](https://issues.jenkins-ci.org/browse/JENKINS-8320))
-   Japanese translation

##### Version 1.4 (8-Mar-2010)

-   Add help text about how to use icon images placed in
    `HUDSON_HOME/userContent` directory, and for absolute vs
    in-this-Hudson URLs.

##### Verison 1.3 (10-Feb-2010)

-   Update code for more recent Hudson.

##### Version 1.2 (23-May-2009)

-   Support for more than one link.

##### Version 1.1 (13-Jan-2009)

-   Avoid NullPointerException when plugin is installed but not
    configured.

##### Version 1.0 (17-Dec-2008)

-   Initial release.

See also:
 - [Jenkins Wiki - Sidebar Link Plug-in](https://wiki.jenkins-ci.org/display/JENKINS/Sidebar-Link+Plugin)
