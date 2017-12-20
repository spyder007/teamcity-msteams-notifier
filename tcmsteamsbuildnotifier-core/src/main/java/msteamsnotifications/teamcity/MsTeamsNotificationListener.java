package msteamsnotifications.teamcity;

import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.util.StringUtil;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationContentConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * MsTeamsNotificationListner
 * Listens for Server events and then triggers the execution of msteamsnotifications if configured.
 */
public class MsTeamsNotificationListener extends BuildServerAdapter {
    
    private static final String SLACKNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME = "msteamsNotifications";
    private static final String BUILD_STATE_MESSAGE_END = " at buildState responsibilityChanged";
    private static final String BUILD_STATE_MESSAGE_START = "About to process MsTeamsNotifications for ";
	private final SBuildServer myBuildServer;
    private final ProjectSettingsManager mySettings;
    private final MsTeamsNotificationMainSettings myMainSettings;
    private final MsTeamsNotificationPayloadManager myManager;
    private final MsTeamsNotificationFactory msteamsNotificationFactory;
	private NotificationUtility notificationUtility;

    public MsTeamsNotificationListener(){
        myBuildServer = null;
        mySettings = null;
        myMainSettings = null;
        myManager = null;
        msteamsNotificationFactory = null;
		notificationUtility = new NotificationUtility();
    }

    public MsTeamsNotificationListener(SBuildServer sBuildServer, ProjectSettingsManager settings,
                                     MsTeamsNotificationMainSettings configSettings, MsTeamsNotificationPayloadManager manager,
                                     MsTeamsNotificationFactory factory) {

        myBuildServer = sBuildServer;
        mySettings = settings;
        myMainSettings = configSettings;
        myManager = manager;
        msteamsNotificationFactory = factory;
		notificationUtility = new NotificationUtility();
        Loggers.SERVER.info("MsTeamsNotificationListener :: Starting");
    }
    
    public void register(){
        myBuildServer.addListener(this);
        Loggers.SERVER.info("MsTeamsNotificationListener :: Registering");
    }

	public void getFromConfig(MsTeamsNotification msteamsNotification, MsTeamsNotificationConfig msteamsNotificationConfig){
        msteamsNotification.setToken(StringUtil.isEmpty(msteamsNotificationConfig.getToken()) ? myMainSettings.getToken() : msteamsNotificationConfig.getToken());
        msteamsNotification.setIconUrl(myMainSettings.getIconUrl());
        msteamsNotification.setBotName(myMainSettings.getBotName());
		msteamsNotification.setEnabled(myMainSettings.getEnabled() && msteamsNotificationConfig.getEnabled());
		msteamsNotification.setBuildStates(msteamsNotificationConfig.getBuildStates());
		msteamsNotification.setProxy(myMainSettings.getProxyConfig());
        msteamsNotification.setShowBuildAgent(myMainSettings.getShowBuildAgent());
        msteamsNotification.setShowElapsedBuildTime(myMainSettings.getShowElapsedBuildTime());
        msteamsNotification.setShowCommits(myMainSettings.getShowCommits());
        msteamsNotification.setShowCommitters(myMainSettings.getShowCommitters());
        msteamsNotification.setShowFailureReason(myMainSettings.getShowFailureReason() == null ? MsTeamsNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON : myMainSettings.getShowFailureReason());
        msteamsNotification.setMaxCommitsToDisplay(myMainSettings.getMaxCommitsToDisplay());
        msteamsNotification.setMentionChannelEnabled(msteamsNotificationConfig.getMentionChannelEnabled());
		msteamsNotification.setMentionMsTeamsUserEnabled(msteamsNotificationConfig.getMentionMsTeamsUserEnabled());
        msteamsNotification.setShowElapsedBuildTime(myMainSettings.getShowElapsedBuildTime());
        if(msteamsNotificationConfig.getContent() != null && msteamsNotificationConfig.getContent().isEnabled()) {
            msteamsNotification.setBotName(msteamsNotificationConfig.getContent().getBotName());
            msteamsNotification.setIconUrl(msteamsNotificationConfig.getContent().getIconUrl());
            msteamsNotification.setMaxCommitsToDisplay(msteamsNotificationConfig.getContent().getMaxCommitsToDisplay());
            msteamsNotification.setShowBuildAgent(msteamsNotificationConfig.getContent().getShowBuildAgent());
            msteamsNotification.setShowElapsedBuildTime(msteamsNotificationConfig.getContent().getShowElapsedBuildTime());
            msteamsNotification.setShowCommits(msteamsNotificationConfig.getContent().getShowCommits());
            msteamsNotification.setShowCommitters(msteamsNotificationConfig.getContent().getShowCommitters());
            msteamsNotification.setShowFailureReason(msteamsNotificationConfig.getContent().getShowFailureReason() == null ? MsTeamsNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON : msteamsNotificationConfig.getContent().getShowFailureReason());
        }
		Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: MsTeamsNotification proxy set to "
				+ msteamsNotification.getProxyHost());
	}
    
	private void processBuildEvent(SRunningBuild sRunningBuild, BuildStateEnum state) {

			Loggers.SERVER.debug("About to process MsTeams notifications for " + sRunningBuild.getProjectId() + " at buildState " + state.getShortName());
			for (MsTeamsNotificationConfigWrapper msteamsNotificationConfigWrapper : getListOfEnabledMsTeamsNotifications(sRunningBuild.getProjectId())){

                if (state.equals(BuildStateEnum.BUILD_STARTED)){
					msteamsNotificationConfigWrapper.msteamsNotification.setPayload(myManager.buildStarted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					msteamsNotificationConfigWrapper.msteamsNotification.setEnabled(msteamsNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && msteamsNotificationConfigWrapper.msteamsNotification.getBuildStates().enabled(BuildStateEnum.BUILD_STARTED));
				} else if (state.equals(BuildStateEnum.BUILD_INTERRUPTED)){
					msteamsNotificationConfigWrapper.msteamsNotification.setPayload(myManager.buildInterrupted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					msteamsNotificationConfigWrapper.msteamsNotification.setEnabled(msteamsNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && msteamsNotificationConfigWrapper.msteamsNotification.getBuildStates().enabled(BuildStateEnum.BUILD_INTERRUPTED));
				} else if (state.equals(BuildStateEnum.BEFORE_BUILD_FINISHED)){
					msteamsNotificationConfigWrapper.msteamsNotification.setPayload(myManager.beforeBuildFinish(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					msteamsNotificationConfigWrapper.msteamsNotification.setEnabled(msteamsNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && msteamsNotificationConfigWrapper.msteamsNotification.getBuildStates().enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
				} else if (state.equals(BuildStateEnum.BUILD_FINISHED)){
					msteamsNotificationConfigWrapper.msteamsNotification.setEnabled(msteamsNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && msteamsNotificationConfigWrapper.msteamsNotification.getBuildStates().enabled(
							BuildStateEnum.BUILD_FINISHED, 
							sRunningBuild.getStatusDescriptor().isSuccessful(),
							this.hasBuildChangedHistoricalState(sRunningBuild)));
					msteamsNotificationConfigWrapper.msteamsNotification.setPayload(myManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));;
				}
				
				doPost(msteamsNotificationConfigWrapper.msteamsNotification);
				//Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: " + myManager.getFormat(msteamsNotificationConfigWrapper.whc.getPayloadFormat()).getFormatDescription());
	    	}
	}

	/** 
	 * Build a list of Enabled MsTeamsNotifications to pass to the POSTing logic.
	 * @param projectId
	 * @return
	 */
	private List<MsTeamsNotificationConfigWrapper> getListOfEnabledMsTeamsNotifications(String projectId) {
		List<MsTeamsNotificationConfigWrapper> configs = new ArrayList<MsTeamsNotificationConfigWrapper>();
		List<SProject> projects = new ArrayList<SProject>();
		SProject myProject = myBuildServer.getProjectManager().findProjectById(projectId);
		projects.addAll(myProject.getProjectPath());
		for (SProject project : projects){
			MsTeamsNotificationProjectSettings projSettings = (MsTeamsNotificationProjectSettings) mySettings.getSettings(project.getProjectId(), SLACKNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME);
	    	if (projSettings.isEnabled()){
		    	for (MsTeamsNotificationConfig whc : projSettings.getMsTeamsNotificationsConfigs()){
		    		if (whc.isEnabledForSubProjects() == false && !myProject.getProjectId().equals(project.getProjectId())){
		    			// Sub-projects are disabled and we are a subproject.
		    			if (Loggers.ACTIVITIES.isDebugEnabled()){
			    			Loggers.ACTIVITIES.debug(this.getClass().getSimpleName() + ":getListOfEnabledMsTeamsNotifications() "
			    					+ ":: subprojects not enabled. myProject is: " + myProject.getProjectId() + ". msteamsnotifications project is: " + project.getProjectId());
		    			}
		    			continue;
		    		}
		    		
		    		if (whc.getEnabled()){
						MsTeamsNotification wh = msteamsNotificationFactory.getMsTeamsNotification();
						this.getFromConfig(wh, whc);

                        configs.add(new MsTeamsNotificationConfigWrapper(wh, whc));

						 
		    		} else {
		    			Loggers.ACTIVITIES.debug(this.getClass().getSimpleName() 
		    					+ ":processBuildEvent() :: MsTeamsNotification disabled. Will not process ");
		    		}
				}
	    	} else {
	    		Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: MsTeamsNotifications are disasbled for  " + projectId);
	    	}
		}
    	return configs;
	}

	@Override
    public void buildStarted(SRunningBuild sRunningBuild){
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_STARTED);
    }	
	
    @Override
    public void buildFinished(SRunningBuild sRunningBuild){
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_FINISHED);
    }    

    @Override
    public void buildInterrupted(SRunningBuild sRunningBuild) {
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_INTERRUPTED);
    }      

    @Override
    public void beforeBuildFinish(SRunningBuild sRunningBuild) {
    	processBuildEvent(sRunningBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
	}
    
    @Deprecated
    /** This method has been removed from the TeamCity API as of version 7.1
     * 
     * @param sBuildType
     * @param responsibilityInfoOld
     * @param responsibilityInfoNew
     * @param isUserAction
     */
    public void responsibleChanged(@NotNull SBuildType sBuildType, 
    							   @NotNull ResponsibilityInfo responsibilityInfoOld, 
    							   @NotNull ResponsibilityInfo responsibilityInfoNew, 
    							   boolean isUserAction) {
    	
    	if (myBuildServer.getServerMajorVersion() >= 7){
    		return;
    	}
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + sBuildType.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (MsTeamsNotificationConfigWrapper whcw : getListOfEnabledMsTeamsNotifications(sBuildType.getProjectId())){

						//MsTeamsNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
                        whcw.msteamsNotification.setPayload(myManager.responsibleChanged(sBuildType,
                                responsibilityInfoOld,
                                responsibilityInfoNew,
                                isUserAction));
						whcw.msteamsNotification.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.msteamsNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.msteamsNotification);
						//Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
		}
     }

	@Override
	public void responsibleChanged(SProject project,
			Collection<TestName> testNames, ResponsibilityEntry entry,
			boolean isUserAction) {
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + project.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (MsTeamsNotificationConfigWrapper whcw : getListOfEnabledMsTeamsNotifications(project.getProjectId())){
                        whcw.msteamsNotification.setPayload(myManager.responsibleChanged(project,
                                testNames,
                                entry,
                                isUserAction));
						whcw.msteamsNotification.setEnabled(whcw.msteamsNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.msteamsNotification);
						//Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}

	@Override
	public void responsibleChanged(SProject project, TestNameResponsibilityEntry oldTestNameResponsibilityEntry, TestNameResponsibilityEntry newTestNameResponsibilityEntry, boolean isUserAction) {
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + project.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (MsTeamsNotificationConfigWrapper whcw : getListOfEnabledMsTeamsNotifications(project.getProjectId())){
						//MsTeamsNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.msteamsNotification.setPayload(myManager.responsibleChanged(project,
                                oldTestNameResponsibilityEntry,
                                newTestNameResponsibilityEntry,
                                isUserAction));
						whcw.msteamsNotification.setEnabled(whcw.msteamsNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.msteamsNotification);
						//Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}
	
	/**
	 * New version of responsibleChanged, which has some bugfixes, but 
	 * is only available in versions 7.0 and above.    
	 * @param sBuildType
	 * @param responsibilityEntryOld
	 * @param responsibilityEntryNew
	 * @since 7.0
	 */
	@Override
	public void responsibleChanged(@NotNull SBuildType sBuildType,
            @NotNull ResponsibilityEntry responsibilityEntryOld,
            @NotNull ResponsibilityEntry responsibilityEntryNew){
		
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + sBuildType.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (MsTeamsNotificationConfigWrapper whcw : getListOfEnabledMsTeamsNotifications(sBuildType.getProjectId())){
						//MsTeamsNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
                        whcw.msteamsNotification.setPayload(myManager.responsibleChanged(sBuildType,
                                responsibilityEntryOld,
                                responsibilityEntryNew));
						whcw.msteamsNotification.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.msteamsNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.msteamsNotification);
						//Loggers.ACTIVITIES.debug("MsTeamsNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
     	}
	}

	@Override
	public void responsibleRemoved(SProject project, TestNameResponsibilityEntry entry){
		
	}
	
    
	/** doPost used by responsibleChanged
	 * 
	 * @param notification
	 */
	public void doPost(MsTeamsNotification notification) {
		notificationUtility.doPost(notification);
	}

	@Nullable
	private SFinishedBuild getPreviousNonPersonalBuild(SRunningBuild paramSRunningBuild)
	  {
	    List<SFinishedBuild> localList = this.myBuildServer.getHistory().getEntriesBefore(paramSRunningBuild, false);

	    for (SFinishedBuild localSFinishedBuild : localList)
	      if (!(localSFinishedBuild.isPersonal())) return localSFinishedBuild;
	    return null;
	}
	
	private boolean hasBuildChangedHistoricalState(SRunningBuild sRunningBuild){
		SFinishedBuild previous = getPreviousNonPersonalBuild(sRunningBuild);
		if (previous != null){
			if (sRunningBuild.getBuildStatus().isSuccessful()){
				return previous.getBuildStatus().isFailed();
			} else if (sRunningBuild.getBuildStatus().isFailed()) {
				return previous.getBuildStatus().isSuccessful();
			}
		}
		return true; 
	}
	
	/**
	 * An inner class to wrap up the MsTeamsNotification and its MsTeamsNotificationConfig into one unit.
	 *
	 */
	
	private class MsTeamsNotificationConfigWrapper {
		 
		private MsTeamsNotification msteamsNotification;
		
		private MsTeamsNotificationConfig whc;
		
		public MsTeamsNotificationConfigWrapper(MsTeamsNotification msteamsNotification, MsTeamsNotificationConfig msteamsNotificationConfig) {
			this.msteamsNotification = msteamsNotification;
			this.whc = msteamsNotificationConfig;
		}
		 public void setMsTeamsNotification(MsTeamsNotification msteamsNotification){
			 this.msteamsNotification=msteamsNotification;
		 }
		 public MsTeamsNotification getMsTeamsNotification(){
			 return msteamsNotification;
		 }
		 
		 public void setMsTeamsNotificationConfig(MsTeamsNotificationConfig whc){
			 this.whc=whc;
		 }
		 public MsTeamsNotificationConfig getMsTeamsNotificationConfig(){
			 return whc;
		 }
	}

}
