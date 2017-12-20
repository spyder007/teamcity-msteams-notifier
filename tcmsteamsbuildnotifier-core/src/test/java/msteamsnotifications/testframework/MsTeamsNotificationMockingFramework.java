package msteamsnotifications.testframework;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jdom.JDOMException;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import java.io.File;
import java.io.IOException;

public interface MsTeamsNotificationMockingFramework {
	
	public SBuildServer getServer();
	public SRunningBuild getRunningBuild();
	public SBuildType getSBuildType();
	public SBuildType getSBuildTypeFromSubProject();
	public MsTeamsNotificationConfig getMsTeamsNotificationConfig();
	public MsTeamsNotificationPayloadContent getMsTeamsNotificationContent();
	public MsTeamsNotificationPayloadManager getMsTeamsNotificationPayloadManager();
	public MsTeamsNotificationProjectSettings getMsTeamsNotificationProjectSettings();
	public void loadMsTeamsNotificationConfigXml(File xmlConfigFile) throws JDOMException, IOException;
	public void loadMsTeamsNotificationProjectSettingsFromConfigXml(File xmlConfigFile) throws IOException, JDOMException;

}
