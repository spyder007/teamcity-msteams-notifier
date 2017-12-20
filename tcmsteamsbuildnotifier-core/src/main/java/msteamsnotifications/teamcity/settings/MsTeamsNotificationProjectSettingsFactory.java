package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.settings.ProjectSettingsFactory;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import msteamsnotifications.teamcity.Loggers;


public class MsTeamsNotificationProjectSettingsFactory implements ProjectSettingsFactory {
	
	public MsTeamsNotificationProjectSettingsFactory(ProjectSettingsManager projectSettingsManager){
		Loggers.SERVER.info("MsTeamsNotificationProjectSettingsFactory :: Registering");
		projectSettingsManager.registerSettingsFactory("msteamsNotifications", this);
	}

	@Override
	public MsTeamsNotificationProjectSettings createProjectSettings(String projectId) {
		Loggers.SERVER.info("MsTeamsNotificationProjectSettingsFactory: re-reading settings for " + projectId);
		MsTeamsNotificationProjectSettings whs = new MsTeamsNotificationProjectSettings();
		return whs;
	}


}
