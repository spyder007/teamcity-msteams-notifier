package msteamsnotifications.teamcity.settings;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import msteamsnotifications.MsTeamsNotificationProxyConfig;
import msteamsnotifications.teamcity.Loggers;

import java.io.File;
import java.io.IOException;

public class MsTeamsNotificationMainConfig implements ChangeListener {
    public static final String DEFAULT_BOTNAME = "TeamCity";
    public static final String DEFAULT_ICONURL = "https://raw.githubusercontent.com/spyder007/teamcity-msteams-notifier/master/docs/TeamCity72x72.png";
	private static final String TOKEN = "token";
	private static final String ICON_URL = "iconurl";
	private static final String BOT_NAME = "botname";
	private static final String SHOW_BUILD_AGENT = "showBuildAgent";
	private static final String SHOW_COMMITS = "showCommits";
	private static final String SHOW_COMMITTERS = "showCommitters";
	private static final String SHOW_FAILURE_REASON = "showFailureReason";
	private static final String MAX_COMMITS_TO_DISPLAY = "maxCommitsToDisplay";
	private static final String SHOW_ELAPSED_BUILD_TIME = "showElapsedBuildTime";
	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";
	private static final String PROXY = "proxy";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ENABLED = "enabled";


    private final FileWatcher myChangeObserver;
	private final File myConfigDir;
	private final File myConfigFile;
	private String msteamsnotificationInfoUrl = null;
	private String msteamsnotificationInfoText = null;
	private Boolean msteamsnotificationShowFurtherReading = true;
	private Integer proxyPort = null;
	private String proxyHost = null;
	private String proxyUsername = null;
	private String proxyPassword = null;
    private String token;
	private Boolean proxyShortNames = false;
    private boolean enabled = true;
	
	public final String SINGLE_HOST_REGEX = "^[^./~`'\"]+(?:/.*)?$";
	public final String HOSTNAME_ONLY_REGEX = "^([^/]+)(?:/.*)?$";
    private MsTeamsNotificationContentConfig content;
    private boolean configFileExists;




	public MsTeamsNotificationMainConfig(ServerPaths serverPaths) {
        this.content = new MsTeamsNotificationContentConfig();
		this.myConfigDir = new File(serverPaths.getConfigDir(), "msteams");
		this.myConfigFile = new File(this.myConfigDir, "msteams-config.xml");
        configFileExists = this.myConfigFile.exists();
		reloadConfiguration();
		this.myChangeObserver = new FileWatcher(this.myConfigFile);
		this.myChangeObserver.setSleepingPeriod(10000L);
		this.myChangeObserver.registerListener(this);
		this.myChangeObserver.start();
	}

    public void refresh(){
        reloadConfiguration();
    }

	private void reloadConfiguration() {
		Loggers.ACTIVITIES.info("Loading configuration file: " + this.myConfigFile.getAbsolutePath());

		myConfigDir.mkdirs();
		FileUtil.copyResourceIfNotExists(getClass(), "/config_templates/msteams-config.xml", new File(this.myConfigDir, "msteams-config.xml"));

		Document document = parseFile(this.myConfigFile);
		if (document != null)
		{
			Element rootElement = document.getRootElement();
			readConfigurationFromXmlElement(rootElement);
		}
	}

	private Document parseFile(File configFile)
	{
		try
		{
			if (configFile.isFile()) {
				return JDOMUtil.loadDocument(configFile);
			}
		}
		catch (JDOMException e)
		{
			Loggers.ACTIVITIES.error("Failed to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		catch (IOException e)
		{
			Loggers.ACTIVITIES.error("I/O error occurred on attempt to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		return null;
	}

	public String getProxySettingsAsString(){
    	return " host:" + this.proxyHost + " port: " + this.proxyPort;
	}

	public String stripProtocolFromUrl(String url){
		String tmpURL = url;
		if(tmpURL.length() > HTTPS.length()
			&& HTTPS.equalsIgnoreCase(tmpURL.substring(0,HTTPS.length())))
		{
				tmpURL = tmpURL.substring(HTTPS.length());
		} else if (tmpURL.length() > HTTP.length()
			&& HTTP.equalsIgnoreCase(tmpURL.substring(0,HTTP.length())))
		{
				tmpURL = tmpURL.substring(HTTP.length());
		}
		return tmpURL;
	}


	
	public Element getInfoUrlAsElement(){
		/*
			<info url="http://acme.com/" text="Using MsTeamsNotifications in Acme Inc." />
		 */
		if (this.msteamsnotificationInfoUrl != null && this.msteamsnotificationInfoUrl.length() > 0){
			Element e = new Element("info");
			e.setAttribute("url", msteamsnotificationInfoUrl);
			if (this.msteamsnotificationInfoText != null && this.msteamsnotificationInfoText.length() > 0){
				e.setAttribute("text", msteamsnotificationInfoText);
			} else {
				e.setAttribute("text", msteamsnotificationInfoUrl);
			}
			e.setAttribute("show-reading", msteamsnotificationShowFurtherReading.toString());
			
			return e;
		}
		return null;
	}

	
	public Element getProxyAsElement(){
		/*
    		  <proxy host="myproxy.mycompany.com" port="8080" >
      			<noproxy url=".mycompany.com" />
      			<noproxy url="192.168.0." />
    		  </proxy>
		 */
		if (this.getProxyHost() == null || this.getProxyPort() == null){
			return null;
		}
		Element el = new Element(PROXY);
		el.setAttribute("host", this.getProxyHost());
		el.setAttribute("port", String.valueOf(this.getProxyPort()));
		if (   this.proxyPassword != null && this.proxyPassword.length() > 0 
			&& this.proxyUsername != null && this.proxyUsername.length() > 0 )
		{
			el.setAttribute(USERNAME, this.getProxyUsername());
			el.setAttribute(PASSWORD, this.getProxyPassword());
			
		}
		return el;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public Boolean isProxyShortNames() {
		return proxyShortNames;
	}

	public void setProxyShortNames(Boolean proxyShortNames) {
		this.proxyShortNames = proxyShortNames;
	}

	public String getMsTeamsNotificationInfoUrl() {
		return msteamsnotificationInfoUrl;
	}

	public String getMsTeamsNotificationInfoText() {
		return msteamsnotificationInfoText;
	}

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	public void setMsTeamsNotificationInfoUrl(String msteamsnotificationInfoUrl) {
		this.msteamsnotificationInfoUrl = msteamsnotificationInfoUrl;
	}

	public void setMsTeamsNotificationInfoText(String msteamsnotificationInfoText) {
		this.msteamsnotificationInfoText = msteamsnotificationInfoText;
	}

	public void setMsTeamsNotificationShowFurtherReading(Boolean msteamsnotificationShowFurtherReading) {
		this.msteamsnotificationShowFurtherReading = msteamsnotificationShowFurtherReading;
	}

	public Boolean getMsTeamsNotificationShowFurtherReading() {
		return msteamsnotificationShowFurtherReading;
	}


	public synchronized void save()
	{
		this.myChangeObserver.runActionWithDisabledObserver(new Runnable()
		{
			public void run()
			{
				FileUtil.processXmlFile(MsTeamsNotificationMainConfig.this.myConfigFile, new FileUtil.Processor() {
					public void process(Element rootElement) {
                        rootElement.setAttribute("enabled", Boolean.toString(MsTeamsNotificationMainConfig.this.enabled));
						rootElement.setAttribute(TOKEN, emptyIfNull(MsTeamsNotificationMainConfig.this.token));
						rootElement.setAttribute(ICON_URL, emptyIfNull(MsTeamsNotificationMainConfig.this.content.getIconUrl()));
						rootElement.setAttribute(BOT_NAME, emptyIfNull(MsTeamsNotificationMainConfig.this.content.getBotName()));
                        rootElement.setAttribute(ENABLED, Boolean.toString(MsTeamsNotificationMainConfig.this.enabled));
						rootElement.setAttribute("token", emptyIfNull(MsTeamsNotificationMainConfig.this.token));
						rootElement.setAttribute("iconurl", emptyIfNull(MsTeamsNotificationMainConfig.this.content.getIconUrl()));
						rootElement.setAttribute("botname", emptyIfNull(MsTeamsNotificationMainConfig.this.content.getBotName()));

						if(MsTeamsNotificationMainConfig.this.content.getShowBuildAgent() != null){
							rootElement.setAttribute(SHOW_BUILD_AGENT, Boolean.toString(MsTeamsNotificationMainConfig.this.content.getShowBuildAgent()));
						}
						if(MsTeamsNotificationMainConfig.this.content.getShowElapsedBuildTime() != null) {
							rootElement.setAttribute(SHOW_ELAPSED_BUILD_TIME, Boolean.toString(MsTeamsNotificationMainConfig.this.content.getShowElapsedBuildTime()));
						}
						if(MsTeamsNotificationMainConfig.this.content.getShowCommits() != null) {
							rootElement.setAttribute(SHOW_COMMITS, Boolean.toString(MsTeamsNotificationMainConfig.this.content.getShowCommits()));
						}
						if(MsTeamsNotificationMainConfig.this.content.getShowCommitters() != null) {
							rootElement.setAttribute(SHOW_COMMITTERS, Boolean.toString(MsTeamsNotificationMainConfig.this.content.getShowCommitters()));
						}
                        if(MsTeamsNotificationMainConfig.this.content.getShowFailureReason() != null) {
                            rootElement.setAttribute(SHOW_FAILURE_REASON, Boolean.toString(MsTeamsNotificationMainConfig.this.content.getShowFailureReason()));
                        }
						rootElement.setAttribute(MAX_COMMITS_TO_DISPLAY, Integer.toString(MsTeamsNotificationMainConfig.this.content.getMaxCommitsToDisplay()));

                        rootElement.removeChildren(PROXY);
                        rootElement.removeChildren("info");

						if(getProxyHost() != null && getProxyHost().length() > 0
								&& getProxyPort() != null && getProxyPort() > 0 )
						{
							rootElement.addContent(getProxyAsElement());
							Loggers.SERVER.debug(MsTeamsNotificationMainConfig.class.getName() + "writeTo :: proxyHost " + getProxyHost());
							Loggers.SERVER.debug(MsTeamsNotificationMainConfig.class.getName() + "writeTo :: proxyPort " + getProxyPort());
						}

						if(getInfoUrlAsElement() != null){
                            rootElement.addContent(getInfoUrlAsElement());
							Loggers.SERVER.debug(MsTeamsNotificationMainConfig.class.getName() + "writeTo :: infoText " + getMsTeamsNotificationInfoText());
							Loggers.SERVER.debug(MsTeamsNotificationMainConfig.class.getName() + "writeTo :: InfoUrl  " + getMsTeamsNotificationInfoUrl());
							Loggers.SERVER.debug(MsTeamsNotificationMainConfig.class.getName() + "writeTo :: show-reading  " + getMsTeamsNotificationShowFurtherReading().toString());
						}
					}
				});
			}
		});
	}

    private String emptyIfNull(String str){
        return str == null ? "" : str;
    }

	@Override
	public void changeOccured(String s) {
		reloadConfiguration();
	}

	public boolean getConfigFileExists() {
		return configFileExists;
	}

	void readConfigurationFromXmlElement(Element msteamsNotificationsElement) {
        if(msteamsNotificationsElement != null){
            content.setEnabled(true);
            if(msteamsNotificationsElement.getAttribute(ENABLED) != null)
            {
                setEnabled(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(ENABLED)));
            }
            if(msteamsNotificationsElement.getAttribute(TOKEN) != null)
            {
                setToken(msteamsNotificationsElement.getAttributeValue(TOKEN));
            }
            if(msteamsNotificationsElement.getAttribute(ICON_URL) != null)
            {
                content.setIconUrl(msteamsNotificationsElement.getAttributeValue(ICON_URL));
            }
            if(msteamsNotificationsElement.getAttribute(BOT_NAME) != null)
            {
                content.setBotName(msteamsNotificationsElement.getAttributeValue(BOT_NAME));
            }
            if(msteamsNotificationsElement.getAttribute(SHOW_BUILD_AGENT) != null)
            {
                content.setShowBuildAgent(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(SHOW_BUILD_AGENT)));
            }
            if(msteamsNotificationsElement.getAttribute(SHOW_ELAPSED_BUILD_TIME) != null)
            {
                content.setShowElapsedBuildTime(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(SHOW_ELAPSED_BUILD_TIME)));
            }
            if(msteamsNotificationsElement.getAttribute(SHOW_COMMITS) != null)
            {
                content.setShowCommits(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(SHOW_COMMITS)));
            }
            if(msteamsNotificationsElement.getAttribute(SHOW_COMMITTERS) != null)
            {
                content.setShowCommitters(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(SHOW_COMMITTERS)));
            }
            if(msteamsNotificationsElement.getAttribute(MAX_COMMITS_TO_DISPLAY) != null)
            {
                content.setMaxCommitsToDisplay(Integer.parseInt(msteamsNotificationsElement.getAttributeValue(MAX_COMMITS_TO_DISPLAY)));
            }
            if(msteamsNotificationsElement.getAttribute(SHOW_FAILURE_REASON) != null)
            {
                content.setShowFailureReason(Boolean.parseBoolean(msteamsNotificationsElement.getAttributeValue(SHOW_FAILURE_REASON)));
            }

            Element proxyElement = msteamsNotificationsElement.getChild(PROXY);
            if(proxyElement != null)
            {
                if (proxyElement.getAttribute("proxyShortNames") != null){
                    setProxyShortNames(Boolean.parseBoolean(proxyElement.getAttributeValue("proxyShortNames")));
                }

                if (proxyElement.getAttribute("host") != null){
                    setProxyHost(proxyElement.getAttributeValue("host"));
                }

                if (proxyElement.getAttribute("port") != null){
                    setProxyPort(Integer.parseInt(proxyElement.getAttributeValue("port")));
                }

                if (proxyElement.getAttribute(USERNAME) != null){
                    setProxyUsername(proxyElement.getAttributeValue(USERNAME));
                }

                if (proxyElement.getAttribute(PASSWORD) != null){
                    setProxyPassword(proxyElement.getAttributeValue(PASSWORD));
                }
            }
            else {
                setProxyHost(null);
                setProxyPort(null);
                setProxyUsername(null);
                setProxyPassword(null);
            }
        }
    }

    public MsTeamsNotificationProxyConfig getProxyConfig() {
        return new MsTeamsNotificationProxyConfig(proxyHost, proxyPort, proxyUsername, proxyPassword);
    }

    public MsTeamsNotificationContentConfig getContent() {
        if(content == null){
            this.content = new MsTeamsNotificationContentConfig();
        }
        return content;
    }
}