package msteamsnotifications.teamcity.extension;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import org.jetbrains.annotations.NotNull;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.extension.bean.ProjectAndBuildMsTeamsnotificationsBean;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class MsTeamsNotificationBuildTabExtension extends BuildTypeTab {
	
	private static final String  SLACK_NOTIFICATIONS = "msteamsNotifications";
	MsTeamsNotificationProjectSettings settings;
	ProjectSettingsManager projSettings;
	String myPluginPath;

	protected MsTeamsNotificationBuildTabExtension(
            PagePlaces pagePlaces, ProjectManager projectManager,
            ProjectSettingsManager settings, WebControllerManager manager,
            PluginDescriptor pluginDescriptor) {
		//super(myTitle, myTitle, null, projectManager);
		super(SLACK_NOTIFICATIONS, "MsTeams", manager, projectManager);
		this.projSettings = settings;
		myPluginPath = pluginDescriptor.getPluginResourcesPath();
	}

	@Override
	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void fillModel(Map model, HttpServletRequest request,
			 @NotNull SBuildType buildType, SUser user) {
		this.settings = 
			(MsTeamsNotificationProjectSettings)this.projSettings.getSettings(buildType.getProject().getProjectId(), SLACK_NOTIFICATIONS);
		
		List<ProjectAndBuildMsTeamsnotificationsBean> projectAndParents = new ArrayList<ProjectAndBuildMsTeamsnotificationsBean>();
		List<SProject> parentProjects = buildType.getProject().getProjectPath();
		parentProjects.remove(0);
		for (SProject projectParent : parentProjects){
			projectAndParents.add(
					ProjectAndBuildMsTeamsnotificationsBean.newInstance(
							projectParent,
							(MsTeamsNotificationProjectSettings) this.projSettings.getSettings(projectParent.getProjectId(), SLACK_NOTIFICATIONS),
							buildType
							)
					);
		}
		
//		projectAndParents.add(
//				ProjectAndBuildMsTeamsnotificationsBean.newInstance(
//						project,
//						(MsTeamsNotificationProjectSettings) this.projSettings.getSettings(project.getProjectId(), SLACK_NOTIFICATIONS),
//						true
//						)
//				);

		model.put("projectAndParents", projectAndParents);
    	
//    	List<MsTeamsNotificationConfig> projectMsTeamsnotifications = this.settings.getProjectMsTeamsNotificationsAsList();
//    	List<MsTeamsNotificationConfig> buildMsTeamsnotifications = this.settings.getBuildMsTeamsNotificationsAsList(buildType);
//    	
//    	model.put("projectMsTeamsNotificationCount", projectMsTeamsnotifications.size());
//    	if (projectMsTeamsnotifications.size() == 0){
//    		model.put("noProjectMsTeamsNotifications", "true");
//    		model.put("projectMsTeamsNotifications", "false");
//    	} else {
//    		model.put("noProjectMsTeamsNotifications", "false");
//    		model.put("projectMsTeamsNotifications", "true");
//    		model.put("projectMsTeamsNotificationList", projectMsTeamsnotifications);
//    		model.put("projectMsTeamsNotificationsDisabled", !this.settings.isEnabled());
//    	}
//    	
//    	model.put("buildMsTeamsNotificationCount", buildMsTeamsnotifications.size());
//    	if (buildMsTeamsnotifications.size() == 0){
//    		model.put("noBuildMsTeamsNotifications", "true");
//    		model.put("buildMsTeamsNotifications", "false");
//    	} else {
//    		model.put("noBuildMsTeamsNotifications", "false");
//    		model.put("buildMsTeamsNotifications", "true");
//    		model.put("buildMsTeamsNotificationList", buildMsTeamsnotifications);
//    	}
//    	

    	model.put("projectId", buildType.getProject().getProjectId());
    	model.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(buildType.getProject()));
    	model.put("projectName", buildType.getProject().getName());
    	
    	model.put("buildTypeId", buildType.getBuildTypeId());
    	model.put("buildExternalId", TeamCityIdResolver.getExternalBuildId(buildType));
    	model.put("buildName", buildType.getName());
	}

	@Override
	public String getIncludeUrl() {
		//return myPluginPath + "MsTeamsNotification/buildMsTeamsNotificationTab.jsp";
		return myPluginPath + "MsTeamsNotification/projectMsTeamsNotificationTab.jsp";
	}


	
}
