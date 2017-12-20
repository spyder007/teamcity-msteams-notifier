Teamcity MS Teams Build Notifier
====================

## Overview

Posts Build Status to [MsTeams](http://teams.microsoft.com)

![Sample Notification](https://raw.github.com/spyder007/teamcity-msteams-notifier/master/docs/build-status_pass.png)
![Sample Notification](https://raw.github.com/spyder007/teamcity-msteams-notifier/master/docs/build-status_fail.png)

_Tested on TeamCity 8.1.2 (build 29993), 9.1 (build 36973)_

## Installation
Head over to the [releases](https://github.com/spyder007/teamcity-msteams-notifier/releases) section and get the zip labelled `tcMsTeamsNotifierPlugin.zip` from there (do not download the one on this page). Copy the zip file into your [TeamCity plugins directory](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins).

You will need to restart the TeamCity service before you can configure the plugin.

## Configuration

Once you have installed the plugin and restarted head on over to the Admin page and configure your MsTeams settings.

![Admin Page Configuration](https://raw.github.com/spyder007/teamcity-msteams-notifier/master/docs/AdminPageBig.png)

- This The user token is available when you got to https://api.msteams.com/web while logged in to your MsTeams instance. Under "authentication" you should see a token displayed like below. Alternatively you can use the full URL from the `Incoming Webhooks` integration in the API token field. The webhooks integration approach is sometimes more preferable as the token is not tied to a user account.

## Usage

From the MsTeams tab on the Project or Build Configuration page, add a new MsTeams Notification and you're away!

![Sample Build Configuration](https://raw.github.com/spyder007/teamcity-msteams-notifier/master/docs/build-msteams-config.png)

## Contribution

In order to contribute to the project you first need to checkout the project sources. This project uses the TeamCity Plugin SDK for development.

In order to test the plugin simply run the following command with java and mvn installed:

    mvn package tc-sdk:start

By default it will install TeamCity in the version listed in the property in the root `pom.xml`. However you can overwrite this setting by using the `-DteamcityVersion=10.0` switch.

Other available commands can be found [here](https://github.com/JetBrains/teamcity-sdk-maven-plugin).
