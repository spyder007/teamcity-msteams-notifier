package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.Element;
import msteamsnotifications.MsTeamsNotificationProxyConfig;
import msteamsnotifications.teamcity.Loggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MsTeamsNotificationMainSettings implements MainConfigProcessor {
	private static final String NAME = MsTeamsNotificationMainSettings.class.getName();
	private MsTeamsNotificationMainConfig msteamsNotificationMainConfig;
	private SBuildServer server;
    private ServerPaths serverPaths;
    private String version;

    public MsTeamsNotificationMainSettings(SBuildServer server, ServerPaths serverPaths){
        this.serverPaths = serverPaths;
        Loggers.SERVER.debug(NAME + " :: Constructor called");
		this.server = server;
		msteamsNotificationMainConfig = new MsTeamsNotificationMainConfig(serverPaths);

	}

    public void register(){
        Loggers.SERVER.debug(NAME + ":: Registering");
        server.registerExtension(MainConfigProcessor.class, "msteamsnotifications", this);
    }
    
	public String getProxySettingsAsString(){
		return this.msteamsNotificationMainConfig.getProxySettingsAsString();
	}
	
    @SuppressWarnings("unchecked")
    @Override
    public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to persist it to the settings object.
     * Old settings should be overwritten.
     */
    {
        if(msteamsNotificationMainConfig.getConfigFileExists()){
            // The MainConfigProcessor approach has been deprecated.
            // Instead we will use our own config file so we have better control over when it is persisted
            return;
        }
    	Loggers.SERVER.info("MsTeamsNotificationMainSettings: re-reading main settings using old-style MainConfigProcessor. From now on we will use the msteams/msteams-config.xml file instead of main-config.xml");
    	Loggers.SERVER.debug(NAME + ":readFrom :: " + rootElement.toString());
    	MsTeamsNotificationMainConfig tempConfig = new MsTeamsNotificationMainConfig(serverPaths);
    	Element msteamsNotificationsElement = rootElement.getChild("msteamsnotifications");
        tempConfig.readConfigurationFromXmlElement(msteamsNotificationsElement);
        this.msteamsNotificationMainConfig = tempConfig;
        tempConfig.save();
    }

    @Override
    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {

    }
    
    public String getProxy(){
    	return this.msteamsNotificationMainConfig.getProxyConfig().getProxyHost();
    }

    public String getInfoText(){
    	return this.msteamsNotificationMainConfig.getMsTeamsNotificationInfoText();
    }

    public String getInfoUrl(){
    	return this.msteamsNotificationMainConfig.getMsTeamsNotificationInfoUrl();
    }

    public String getToken() {
        return this.msteamsNotificationMainConfig.getToken();
    }

    public String getIconUrl()
    {
        return this.msteamsNotificationMainConfig.getContent().getIconUrl();
    }

    public String getBotName()
    {
        return this.msteamsNotificationMainConfig.getContent().getBotName();
    }

    public boolean getEnabled(){
        return this.msteamsNotificationMainConfig.getEnabled();
    }


    public Boolean getShowBuildAgent() {
        return this.msteamsNotificationMainConfig.getContent().getShowBuildAgent();
    }

    public Boolean getShowElapsedBuildTime() {
        return this.msteamsNotificationMainConfig.getContent().getShowElapsedBuildTime();
    }

    public boolean getShowCommits(){
        return this.msteamsNotificationMainConfig.getContent().getShowCommits();
    }
	
    public boolean getShowCommitters(){
        return this.msteamsNotificationMainConfig.getContent().getShowCommitters();
    }

    public Boolean getShowFailureReason() {
        return this.msteamsNotificationMainConfig.getContent().getShowFailureReason();
    }

    public Boolean getMsTeamsNotificationShowFurtherReading(){
    	return this.msteamsNotificationMainConfig.getMsTeamsNotificationShowFurtherReading();
    }
    
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}

	public MsTeamsNotificationProxyConfig getProxyConfig() {
		return this.msteamsNotificationMainConfig.getProxyConfig();	}


    public int getMaxCommitsToDisplay() {
        return this.msteamsNotificationMainConfig.getContent().getMaxCommitsToDisplay();
    }

    public void refresh() {
        this.msteamsNotificationMainConfig.refresh();
    }

    public String getPluginVersion() throws IOException {
        if(version != null){
            return version;
        }
        Properties props = new Properties();
        props.load(MsTeamsNotificationMainSettings.class.getResourceAsStream("/version.txt"));
        version = props.getProperty("version");
        return version;
    }
}
