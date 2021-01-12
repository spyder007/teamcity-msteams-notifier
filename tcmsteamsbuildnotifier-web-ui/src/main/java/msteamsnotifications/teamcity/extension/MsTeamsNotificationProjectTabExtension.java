package msteamsnotifications.teamcity.extension;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.project.ProjectTab;
import org.jetbrains.annotations.NotNull;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.extension.bean.ProjectAndBuildMsTeamsnotificationsBean;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class MsTeamsNotificationProjectTabExtension extends ProjectTab {
	
	ProjectSettingsManager projSettings;
	String myPluginPath;

	protected MsTeamsNotificationProjectTabExtension(
            PagePlaces pagePlaces, ProjectManager projectManager,
            ProjectSettingsManager settings, PluginDescriptor pluginDescriptor) {
		super("msteamsNotifications", "MsTeams", pagePlaces, projectManager);
		this.projSettings = settings;
		myPluginPath = pluginDescriptor.getPluginResourcesPath();
		register();
	}

	@Override
	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void fillModel(Map model, HttpServletRequest request,
			 @NotNull SProject project, SUser user) {
		
		List<ProjectAndBuildMsTeamsnotificationsBean> projectAndParents = new ArrayList<ProjectAndBuildMsTeamsnotificationsBean>();
		List<SProject> parentProjects = project.getProjectPath();
		parentProjects.remove(0);
		for (SProject projectParent : parentProjects){
			projectAndParents.add(
					ProjectAndBuildMsTeamsnotificationsBean.newInstance(
							projectParent,
							(MsTeamsNotificationProjectSettings) this.projSettings.getSettings(projectParent.getProjectId(), "msteamsNotifications"),
							null
							)
					);
		}
		
//		projectAndParents.add(
//				ProjectAndBuildMsTeamsnotificationsBean.newInstance(
//						project,
//						(MsTeamsNotificationProjectSettings) this.projSettings.getSettings(project.getProjectId(), "msteamsNotifications"),
//						true
//						)
//				);

		model.put("projectAndParents", projectAndParents);
		
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
//		model.put("buildMsTeamsNotificationList", buildMsTeamsnotifications);
    	
    	model.put("projectId", project.getProjectId());
    	model.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
    	model.put("projectName", project.getName());
	}

	@Override
	public String getIncludeUrl() {
		return myPluginPath+ "MsTeamsNotification/projectMsTeamsNotificationTab.jsp";
	}

}
