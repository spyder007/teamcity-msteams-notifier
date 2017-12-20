package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.settings.ProjectSettings;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.jdom.Element;
import msteamsnotifications.teamcity.BuildState;
import msteamsnotifications.teamcity.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


public class MsTeamsNotificationProjectSettings implements ProjectSettings {
	private static final String ENABLED = "enabled";
	private static final String NAME = MsTeamsNotificationProjectSettings.class.getName();
	ProjectSettingsManager psm;
	ProjectSettings ps;
	private Boolean msTeamsNotificationsEnabled = true;
	private Boolean updateSuccess = false;
	private String updateMessage = "";
	private CopyOnWriteArrayList<MsTeamsNotificationConfig> msteamsNotificationsConfigs;
	
	public MsTeamsNotificationProjectSettings(){
		msteamsNotificationsConfigs = new CopyOnWriteArrayList<MsTeamsNotificationConfig>();
	}

    @SuppressWarnings("unchecked")
	@Override
	public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to load it into the in memory settings object.
     * Old settings should be overwritten.
     */
    {
    	Loggers.SERVER.debug("readFrom :: " + rootElement.toString());
    	CopyOnWriteArrayList<MsTeamsNotificationConfig> configs = new CopyOnWriteArrayList<MsTeamsNotificationConfig>();
    	
    	if (rootElement.getAttribute(ENABLED) != null){
    		this.msTeamsNotificationsEnabled = Boolean.parseBoolean(rootElement.getAttributeValue(ENABLED));
    	}
    	
		List<Element> namedChildren = rootElement.getChildren("msteamsNotification");
        if(namedChildren.isEmpty())
        {
            this.msteamsNotificationsConfigs = null;
        } else {
			for(Element e :  namedChildren)
	        {
				MsTeamsNotificationConfig whConfig = new MsTeamsNotificationConfig(e);
				Loggers.SERVER.debug(e.toString());
				configs.add(whConfig);
				Loggers.SERVER.debug(NAME + ":readFrom :: enabled " + String.valueOf(whConfig.getEnabled()));
	        }
			this.msteamsNotificationsConfigs = configs;
    	}
    }

	@Override
    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {
    	Loggers.SERVER.debug(NAME + ":writeTo :: " + parentElement.toString());
    	parentElement.setAttribute(ENABLED, String.valueOf(this.msTeamsNotificationsEnabled));
        if(msteamsNotificationsConfigs != null)
        {
            for(MsTeamsNotificationConfig whc : msteamsNotificationsConfigs){
            	Element el = whc.getAsElement();
            	Loggers.SERVER.debug(el.toString());
                parentElement.addContent(el);
				Loggers.SERVER.debug(NAME + ":writeTo :: enabled " + String.valueOf(whc.getEnabled()));
            }
        }
    }
    
    public List<MsTeamsNotificationConfig> getMsTeamsNotificationsAsList(){
    	return this.msteamsNotificationsConfigs;
    }    
    
    public List<MsTeamsNotificationConfig> getProjectMsTeamsNotificationsAsList(){
    	List<MsTeamsNotificationConfig> projHooks = new ArrayList<MsTeamsNotificationConfig>();
    	for (MsTeamsNotificationConfig config : getMsTeamsNotificationsAsList()){
    		if (config.isEnabledForAllBuildsInProject()){
    			projHooks.add(config);
    		}
    	}
    	return projHooks;
    }    
    
    public List<MsTeamsNotificationConfig> getBuildMsTeamsNotificationsAsList(SBuildType buildType){
    	List<MsTeamsNotificationConfig> buildHooks = new ArrayList<MsTeamsNotificationConfig>();
    	for (MsTeamsNotificationConfig config : getMsTeamsNotificationsAsList()){
    		if (config.isSpecificBuildTypeEnabled(buildType)){
    			buildHooks.add(config);
    		}
    	}
    	return buildHooks;
    }    
        
	
    public String getMsTeamsNotificationsAsString(){
    	String tmpString = "";
    	for(MsTeamsNotificationConfig whConf : msteamsNotificationsConfigs)
    	{
    		tmpString += whConf.getUniqueKey() + "<br/>";
    	}
    	return tmpString;
    }

    public void deleteMsTeamsNotification(String msTeamsNotificationId, String ProjectId){
        if(this.msteamsNotificationsConfigs != null)
        {
        	updateSuccess = false;
        	updateMessage = "";
        	List<MsTeamsNotificationConfig> tempMsTeamsNotificationList = new ArrayList<MsTeamsNotificationConfig>();
            for(MsTeamsNotificationConfig whc : msteamsNotificationsConfigs)
            {
                if (whc.getUniqueKey().equals(msTeamsNotificationId)){
                	Loggers.SERVER.debug(NAME + ":deleteMsTeamsNotification :: Deleting msteamsnotifications from " + ProjectId + " with Channel ");
                	tempMsTeamsNotificationList.add(whc);
                }
            }
            if (!tempMsTeamsNotificationList.isEmpty()){
            	this.updateSuccess = true;
            	this.msteamsNotificationsConfigs.removeAll(tempMsTeamsNotificationList);
            }
        }    	
    }

	public void updateMsTeamsNotification(String ProjectId, String token, String msTeamsNotificationId, Boolean enabled, BuildState buildState, boolean buildTypeAll, boolean buildSubProjects, Set<String> buildTypesEnabled, boolean mentionChannelEnabled, boolean mentionMsTeamsUserEnabled, MsTeamsNotificationContentConfig content) {
        if(this.msteamsNotificationsConfigs != null)
        {
        	updateSuccess = false;
        	updateMessage = "";
            for(MsTeamsNotificationConfig whc : msteamsNotificationsConfigs)
            {
                if (whc.getUniqueKey().equals(msTeamsNotificationId)){
                	whc.setEnabled(enabled);
					whc.setToken(token);
                    whc.setMentionChannelEnabled(mentionChannelEnabled);
					whc.setMentionMsTeamsUserEnabled(mentionMsTeamsUserEnabled);
                	whc.setBuildStates(buildState);
                	whc.enableForSubProjects(buildSubProjects);
                	whc.enableForAllBuildsInProject(buildTypeAll);
                    whc.setHasCustomContent(content.isEnabled());
                    whc.setContent(content);
                	if (!buildTypeAll){
                		whc.clearAllEnabledBuildsInProject();
                		for (String bt : buildTypesEnabled){
                			whc.enableBuildInProject(bt);
                		}
                	}
                	Loggers.SERVER.debug(NAME + ":updateMsTeamsNotification :: Updating msteamsnotifications from " + ProjectId + " with URL ");
                   	this.updateSuccess = true;
                }
            }
        }    			
	}

	public void addNewMsTeamsNotification(String ProjectId, String token, Boolean enabled, BuildState buildState, boolean buildTypeAll, boolean buildTypeSubProjects, Set<String> buildTypesEnabled, boolean mentionChannelEnabled, boolean mentionMsTeamsUserEnabled) {
		this.msteamsNotificationsConfigs.add(new MsTeamsNotificationConfig(token, enabled, buildState, buildTypeAll, buildTypeSubProjects, buildTypesEnabled, mentionChannelEnabled, mentionMsTeamsUserEnabled));
		Loggers.SERVER.debug(NAME + ":addNewMsTeamsNotification :: Adding msteams notifications to " + ProjectId);
		this.updateSuccess = true;
	}

    public Boolean updateSuccessful(){
    	return this.updateSuccess;
    }

	@Override
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}

	public Integer getMsTeamsNotificationsCount(){
		return this.msteamsNotificationsConfigs.size();
	}
	
	public Boolean isEnabled() {
		return msTeamsNotificationsEnabled;
	}

	public String isEnabledAsChecked() {
		if (this.msTeamsNotificationsEnabled){
			return "checked ";
		}
		return "";
	}
	
	public List<MsTeamsNotificationConfig> getMsTeamsNotificationsConfigs() {
		return msteamsNotificationsConfigs;
	}

	public String getUpdateMessage() {
		return updateMessage;
	}

}
