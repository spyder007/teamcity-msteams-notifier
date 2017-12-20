package msteamsnotifications.teamcity.extension.bean;

import msteamsnotifications.teamcity.BuildStateEnum;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationContentConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;

import java.util.ArrayList;
import java.util.List;

public class MsTeamsnotificationConfigAndBuildTypeListHolder {
   

	private String token;
	private String uniqueKey;
	private boolean enabled;
	private String payloadFormatForWeb = "Unknown";
	private List<StateBean> states = new ArrayList<StateBean>();
	private boolean allBuildTypesEnabled;
	private boolean subProjectsEnabled;
	
	
	
	
	private List<MsTeamsnotificationBuildTypeEnabledStatusBean> builds = new ArrayList<MsTeamsnotificationBuildTypeEnabledStatusBean>();
	private String enabledEventsListForWeb;
	private String enabledBuildsListForWeb;
	private boolean mentionChannelEnabled;
	private boolean mentionMsTeamsUserEnabled;
    private boolean customContentEnabled;
    private boolean showBuildAgent;
    private boolean showElapsedBuildTime;
    private boolean showCommits;
    private boolean showCommitters;
    private int maxCommitsToDisplay;
    private boolean showFailureReason;
    private String botName;
    private String iconUrl;

	public MsTeamsnotificationConfigAndBuildTypeListHolder(MsTeamsNotificationConfig config, MsTeamsNotificationMainSettings mainSettings) {
		token = config.getToken();
		uniqueKey = config.getUniqueKey();
		enabled = config.getEnabled();
		setEnabledEventsListForWeb(config.getEnabledListAsString());
		setEnabledBuildsListForWeb(config.getBuildTypeCountAsFriendlyString());
		allBuildTypesEnabled = config.isEnabledForAllBuildsInProject();
		subProjectsEnabled = config.isEnabledForSubProjects();
		for (BuildStateEnum state : config.getBuildStates().getStateSet()){
			states.add(new StateBean(state.getShortName(), config.getBuildStates().enabled(state)));
		}
		mentionChannelEnabled = config.getMentionChannelEnabled();
		mentionMsTeamsUserEnabled = config.getMentionMsTeamsUserEnabled();
        maxCommitsToDisplay = config.getContent().getMaxCommitsToDisplay();
        customContentEnabled = config.getContent().isEnabled();
        showBuildAgent = valueOrFallback(config.getContent().getShowBuildAgent(), valueOrFallback(mainSettings.getShowBuildAgent(), MsTeamsNotificationContentConfig.DEFAULT_SHOW_BUILD_AGENT));
        showElapsedBuildTime = valueOrFallback(config.getContent().getShowElapsedBuildTime(), valueOrFallback(mainSettings.getShowElapsedBuildTime(), MsTeamsNotificationContentConfig.DEFAULT_SHOW_ELAPSED_BUILD_TIME));
        showCommits = valueOrFallback(config.getContent().getShowCommits(), valueOrFallback(mainSettings.getShowCommits(), MsTeamsNotificationContentConfig.DEFAULT_SHOW_COMMITS));
        showCommitters = valueOrFallback(config.getContent().getShowCommitters(), valueOrFallback(mainSettings.getShowCommitters(), MsTeamsNotificationContentConfig.DEFAULT_SHOW_COMMITTERS));
        showFailureReason = valueOrFallback(config.getContent().getShowFailureReason(), valueOrFallback(mainSettings.getShowFailureReason(), MsTeamsNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON));
        botName = valueOrFallback(config.getContent().getBotName(), MsTeamsNotificationMainConfig.DEFAULT_BOTNAME);
        iconUrl = valueOrFallback(config.getContent().getIconUrl(), MsTeamsNotificationMainConfig.DEFAULT_ICONURL);
	}

	
	 public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getUniqueKey() {
			return uniqueKey;
		}

		public void setUniqueKey(String uniqueKey) {
			this.uniqueKey = uniqueKey;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getPayloadFormatForWeb() {
			return payloadFormatForWeb;
		}

		public void setPayloadFormatForWeb(String payloadFormatForWeb) {
			this.payloadFormatForWeb = payloadFormatForWeb;
		}

		public List<StateBean> getStates() {
			return states;
		}

		public void setStates(List<StateBean> states) {
			this.states = states;
		}

		public boolean isAllBuildTypesEnabled() {
			return allBuildTypesEnabled;
		}

		public void setAllBuildTypesEnabled(boolean allBuildTypesEnabled) {
			this.allBuildTypesEnabled = allBuildTypesEnabled;
		}

		public boolean isSubProjectsEnabled() {
			return subProjectsEnabled;
		}

		public void setSubProjectsEnabled(boolean subProjectsEnabled) {
			this.subProjectsEnabled = subProjectsEnabled;
		}
	
	public List<MsTeamsnotificationBuildTypeEnabledStatusBean> getBuilds() {
		return builds;
	}
	
	public String getEnabledBuildTypes(){
		StringBuilder types = new StringBuilder();
		for (MsTeamsnotificationBuildTypeEnabledStatusBean build : getBuilds()){
			if (build.enabled){
				types.append(build.buildTypeId).append(",");
			}
		}
		return types.toString();
		
	}

    private boolean valueOrFallback(Boolean value, boolean fallback){
        return value == null ? fallback : value.booleanValue();
    }

    private String valueOrFallback(String value, String fallback){
        return value == null ? fallback : value;
    }

	public void setBuilds(List<MsTeamsnotificationBuildTypeEnabledStatusBean> builds) {
		this.builds = builds;
	}
	
	
	public void addMsTeamsNotificationBuildType(MsTeamsnotificationBuildTypeEnabledStatusBean status){
		this.builds.add(status);
	}

	public String getEnabledEventsListForWeb() {
		return enabledEventsListForWeb;
	}

	public void setEnabledEventsListForWeb(String enabledEventsListForWeb) {
		this.enabledEventsListForWeb = enabledEventsListForWeb;
	}

	public String getEnabledBuildsListForWeb() {
		return enabledBuildsListForWeb;
	}

	public void setEnabledBuildsListForWeb(String enabledBuildsListForWeb) {
		this.enabledBuildsListForWeb = enabledBuildsListForWeb;
	}
	
}
