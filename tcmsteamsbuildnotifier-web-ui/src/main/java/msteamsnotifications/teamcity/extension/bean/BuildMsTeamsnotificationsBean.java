package msteamsnotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildType;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;

import java.util.List;

public class BuildMsTeamsnotificationsBean{
	
	private SBuildType sBuildType;
	private List<MsTeamsNotificationConfig> buildConfigs;
	
	public BuildMsTeamsnotificationsBean(SBuildType b, List<MsTeamsNotificationConfig> c) {
		this.setsBuildType(b);
		this.setBuildConfigs(c);
	}

	public SBuildType getsBuildType() {
		return sBuildType;
	}

	public void setsBuildType(SBuildType sBuildType) {
		this.sBuildType = sBuildType;
	}

	public List<MsTeamsNotificationConfig> getbuildMsTeamsNotificationList() {
		return buildConfigs;
	}

	public void setBuildConfigs(List<MsTeamsNotificationConfig> buildConfigs) {
		this.buildConfigs = buildConfigs;
	}
	
	public boolean hasBuilds(){
		return !this.buildConfigs.isEmpty();
	}
	
	public boolean hasNoBuildMsTeamsNotifications(){
		return this.buildConfigs.isEmpty();
	}
	
	public boolean hasBuildMsTeamsNotifications(){
		return !this.buildConfigs.isEmpty();
	}
	
	public int getBuildCount(){
		return this.buildConfigs.size();
	}
	
	public String getBuildExternalId(){
		return TeamCityIdResolver.getExternalBuildId(sBuildType);
	}
	public String getBuildName(){
		return sBuildType.getName();
	}
	
}