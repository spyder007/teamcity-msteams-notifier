package msteamsnotifications.teamcity.extension.bean;

public class MsTeamsnotificationBuildTypeEnabledStatusBean {
	
	boolean enabled;
	String buildTypeId;
	String buildTypeName;
	
	public MsTeamsnotificationBuildTypeEnabledStatusBean(String buildTypeId, String buildTypeName, boolean enabled) {
		this.buildTypeId = buildTypeId;
		this.buildTypeName = buildTypeName;
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getEnabledAsChecked(){
		return enabled ? "checked" : "";
	}

	public String getBuildTypeId() {
		return buildTypeId;
	}

	public String getBuildTypeName() {
		return buildTypeName;
	}
	
	

}
