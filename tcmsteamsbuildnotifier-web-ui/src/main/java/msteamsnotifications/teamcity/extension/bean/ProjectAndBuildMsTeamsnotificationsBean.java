package msteamsnotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import java.util.ArrayList;
import java.util.List;

public class ProjectAndBuildMsTeamsnotificationsBean {
	SProject project;
	MsTeamsNotificationProjectSettings msteamsNotificationProjectSettings;
	List<MsTeamsNotificationConfig> projectMsTeamsnotifications;
	List<BuildMsTeamsnotificationsBean> buildMsTeamsnotifications;
	
	public static ProjectAndBuildMsTeamsnotificationsBean newInstance (SProject project, MsTeamsNotificationProjectSettings settings, SBuildType sBuild) {
		ProjectAndBuildMsTeamsnotificationsBean bean = new ProjectAndBuildMsTeamsnotificationsBean();
		bean.project = project;
		bean.msteamsNotificationProjectSettings = settings;
		
		bean.projectMsTeamsnotifications = settings.getProjectMsTeamsNotificationsAsList();
		bean.buildMsTeamsnotifications = new ArrayList<BuildMsTeamsnotificationsBean>();
		
		if (sBuild != null && sBuild.getProjectId().equals(project.getProjectId())){
			bean.buildMsTeamsnotifications.add(new BuildMsTeamsnotificationsBean(sBuild, settings.getBuildMsTeamsNotificationsAsList(sBuild)));
		}
		return bean;
	}

	public int getProjectMsTeamsnotificationCount(){
		return this.projectMsTeamsnotifications.size();
	}

	public int getBuildMsTeamsnotificationCount(){
		return this.buildMsTeamsnotifications.size();
	}
	
	public SProject getProject() {
		return project;
	}

	public MsTeamsNotificationProjectSettings getmsteamsNotificationProjectSettings() {
		return msteamsNotificationProjectSettings;
	}

	public List<MsTeamsNotificationConfig> getProjectMsTeamsnotifications() {
		return projectMsTeamsnotifications;
	}

	public List<BuildMsTeamsnotificationsBean> getBuildMsTeamsnotifications() {
		return buildMsTeamsnotifications;
	}
	
	public String getExternalProjectId(){
		return TeamCityIdResolver.getExternalProjectId(project);
	}

}
