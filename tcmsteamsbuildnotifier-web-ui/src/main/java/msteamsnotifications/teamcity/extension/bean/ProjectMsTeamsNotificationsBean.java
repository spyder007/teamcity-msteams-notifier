package msteamsnotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import msteamsnotifications.teamcity.BuildState;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import java.util.*;

public class ProjectMsTeamsNotificationsBean {
	String projectId;
	Map<String, MsTeamsnotificationConfigAndBuildTypeListHolder> msTeamsNotificationList;
	
	
	public static ProjectMsTeamsNotificationsBean build(MsTeamsNotificationProjectSettings projSettings, SProject project, MsTeamsNotificationMainSettings mainSettings){
		ProjectMsTeamsNotificationsBean bean = new ProjectMsTeamsNotificationsBean();
		List<SBuildType> projectBuildTypes = TeamCityIdResolver.getOwnBuildTypes(project);
		
		bean.projectId = TeamCityIdResolver.getInternalProjectId(project);
		bean.msTeamsNotificationList = new LinkedHashMap<String, MsTeamsnotificationConfigAndBuildTypeListHolder>();

		/* Create a "new" config with blank stuff so that clicking the "new" button has a bunch of defaults to load in */
		MsTeamsNotificationConfig newBlankConfig = new MsTeamsNotificationConfig("", true, new BuildState().setAllEnabled(), true, true, null, true, true);
		newBlankConfig.setUniqueKey("new");
		/* And add it to the list */
		addMsTeamsNotificationConfigHolder(bean, projectBuildTypes, newBlankConfig, mainSettings);
		
		/* Iterate over the rest of the msteamsnotifications in this project and add them to the json config */
		for (MsTeamsNotificationConfig config : projSettings.getMsTeamsNotificationsAsList()){
			addMsTeamsNotificationConfigHolder(bean, projectBuildTypes, config, mainSettings);
		}
		
		return bean;
		
	}
	
	public static ProjectMsTeamsNotificationsBean build(MsTeamsNotificationProjectSettings projSettings, SBuildType sBuildType, SProject project, MsTeamsNotificationMainSettings mainSettings){
		ProjectMsTeamsNotificationsBean bean = new ProjectMsTeamsNotificationsBean();
		List<SBuildType> projectBuildTypes = TeamCityIdResolver.getOwnBuildTypes(project);
		Set<String> enabledBuildTypes = new HashSet<String>();
		enabledBuildTypes.add(sBuildType.getBuildTypeId());
		
		bean.projectId = TeamCityIdResolver.getInternalProjectId(project);
		bean.msTeamsNotificationList = new LinkedHashMap<String, MsTeamsnotificationConfigAndBuildTypeListHolder>();
		
		/* Create a "new" config with blank stuff so that clicking the "new" button has a bunch of defaults to load in */
		MsTeamsNotificationConfig newBlankConfig = new MsTeamsNotificationConfig("", true, new BuildState().setAllEnabled(), false, false, enabledBuildTypes, true, true);
		newBlankConfig.setUniqueKey("new");
		/* And add it to the list */
		addMsTeamsNotificationConfigHolder(bean, projectBuildTypes, newBlankConfig, mainSettings);
		
		/* Iterate over the rest of the msteamsnotifications in this project and add them to the json config */
		for (MsTeamsNotificationConfig config : projSettings.getBuildMsTeamsNotificationsAsList(sBuildType)){
			addMsTeamsNotificationConfigHolder(bean, projectBuildTypes, config, mainSettings);
		}
		
		return bean;
		
	}


	private static void addMsTeamsNotificationConfigHolder(ProjectMsTeamsNotificationsBean bean,
			List<SBuildType> projectBuildTypes, MsTeamsNotificationConfig config, MsTeamsNotificationMainSettings mainSettings) {
		MsTeamsnotificationConfigAndBuildTypeListHolder holder = new MsTeamsnotificationConfigAndBuildTypeListHolder(config, mainSettings);
		for (SBuildType sBuildType : projectBuildTypes){
			holder.addMsTeamsNotificationBuildType(new MsTeamsnotificationBuildTypeEnabledStatusBean(
													sBuildType.getBuildTypeId(), 
													sBuildType.getName(), 
													config.isEnabledForBuildType(sBuildType)
													)
										);
		}
		bean.msTeamsNotificationList.put(holder.getUniqueKey(), holder);
	}
}
