package msteamsnotifications.teamcity.extension;


import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.MsTeamsNotificationImpl;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.payload.content.Commit;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MsTeamsNotifierSettingsController extends BaseController {

    private static final String CONTROLLER_PATH = "/msteamsNotifier/adminSettings.html";
    public static final String EDIT_PARAMETER = "edit";
    public static final String TEST_PARAMETER = "test";
    private static final Object ACTION_ENABLE = "enable";
    private static final String ACTION_PARAMETER = "action";
    private String token;
    private String botName;
    private String iconUrl;
    private String maxCommitsToDisplay;
    private String showBuildAgent;
    private String showCommits;
    private String showCommitters;
    private String showElapsedBuildTime;
    private String showFailureReason;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private String proxyPassword;

    private SBuildServer server;
    private ServerPaths serverPaths;
    private WebControllerManager manager;
    private MsTeamsNotificationMainConfig config;
    private MsTeamsNotificationPayloadManager payloadManager;
    private PluginDescriptor descriptor;

    public MsTeamsNotifierSettingsController(@NotNull SBuildServer server,
                                           @NotNull ServerPaths serverPaths,
                                           @NotNull WebControllerManager manager,
                                           @NotNull MsTeamsNotificationMainConfig config,
                                           MsTeamsNotificationPayloadManager payloadManager,
                                           PluginDescriptor descriptor){

        this.server = server;
        this.serverPaths = serverPaths;
        this.manager = manager;
        this.config = config;
        this.payloadManager = payloadManager;
        this.descriptor = descriptor;

        manager.registerController(CONTROLLER_PATH, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(request.getParameter(EDIT_PARAMETER) != null){
            logger.debug("Updating configuration");
            params = this.handleConfigurationChange(request);
        }
        else if(request.getParameter(TEST_PARAMETER) != null){
            logger.debug("Sending test notification");
            params = this.handleTestNotification(request);
        } else if (request.getParameter(ACTION_PARAMETER) != null) {
            logger.debug("Changing plugin status");
            this.handlePluginStatusChange(request);
        }
        return new ModelAndView(descriptor.getPluginResourcesPath() + "MsTeamsNotification/ajaxEdit.jsp", params);
    }

    private void handlePluginStatusChange(HttpServletRequest request) {
        logger.debug("Changing status");
        Boolean disabled = !request.getParameter(ACTION_PARAMETER).equals(ACTION_ENABLE);
        logger.debug(String.format("Disabled status: %s", disabled));
        this.config.setEnabled(!disabled);
        this.config.save();
    }

    private void setRequestParams(HttpServletRequest request) {
        token = request.getParameter("token");
        botName = request.getParameter("botName");
        iconUrl = request.getParameter("iconUrl");
        maxCommitsToDisplay = request.getParameter("maxCommitsToDisplay");
        showBuildAgent = request.getParameter("showBuildAgent");
        showCommits = request.getParameter("showCommits");
        showCommitters = request.getParameter("showCommitters");
        showElapsedBuildTime = request.getParameter("showElapsedBuildTime");
        showFailureReason = request.getParameter("showFailureReason");
        proxyHost = request.getParameter("proxyHost");
        proxyPort = request.getParameter("proxyPort");
        proxyUser = request.getParameter("proxyUser");
        proxyPassword = request.getParameter("proxyPassword");
    }

    private HashMap<String, Object> handleTestNotification(HttpServletRequest request) throws IOException, MsTeamsConfigValidationException {
        setRequestParams(request);
        HashMap<String, Object> params = new HashMap<String, Object>();

        Validate(token, botName, iconUrl, maxCommitsToDisplay, showBuildAgent, proxyHost, proxyPort, proxyUser, proxyPassword);

        MsTeamsNotification notification = createMockNotification(botName,
                token, iconUrl, Integer.parseInt(maxCommitsToDisplay),
                Boolean.parseBoolean(showElapsedBuildTime), Boolean.parseBoolean(showBuildAgent),
                Boolean.parseBoolean(showCommits), Boolean.parseBoolean(showCommitters), Boolean.parseBoolean(showFailureReason),
                proxyHost, proxyPort, proxyUser, proxyPassword);



        notification.post();

        this.getOrCreateMessages(request).addMessage("notificationSent", "The notification has been sent");

        params.put("messages", "Sent");


        return params;
    }

    private void Validate(String token, String botName, String iconUrl
            , String maxCommitsToDisplay, String showBuildAgent, String proxyHost, String proxyPort, String proxyUser, String proxyPassword) throws MsTeamsConfigValidationException {
        if(token == null || StringUtil.isEmpty(token)
                || botName == null || StringUtil.isEmpty(botName)
                || iconUrl == null || StringUtil.isEmpty(iconUrl)
                || (showBuildAgent.toLowerCase() == "false" && (maxCommitsToDisplay == null || StringUtil.isEmpty(maxCommitsToDisplay)))
                || tryParseInt(maxCommitsToDisplay) == null
                || (!isNullOrEmpty(proxyHost) && isNullOrEmpty(proxyPort))
                || (!isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPassword))
                || (!isNullOrEmpty(proxyPort) && tryParseInt(proxyPort) == null)
                ){

            throw new MsTeamsConfigValidationException("Could not validate parameters. Please recheck the request.");
        }
    }

    private boolean isNullOrEmpty(String str){
        return str == null || StringUtil.isEmpty(str);
    }

    public MsTeamsNotification createMockNotification(String botName,
                                                    String token, String iconUrl, Integer maxCommitsToDisplay,
                                                    Boolean showElapsedBuildTime, Boolean showBuildAgent, Boolean showCommits,
                                                    Boolean showCommitters, Boolean showFailureReason, String proxyHost,
                                                    String proxyPort, String proxyUser, String proxyPassword) {
        MsTeamsNotification notification = new MsTeamsNotificationImpl();
        notification.setBotName(botName);
        notification.setToken(token);
        notification.setIconUrl(iconUrl);
        notification.setMaxCommitsToDisplay(maxCommitsToDisplay);
        notification.setShowElapsedBuildTime(showElapsedBuildTime);
        notification.setShowBuildAgent(showBuildAgent);
        notification.setShowCommits(showCommits);
        notification.setShowCommitters(showCommitters);
        notification.setShowFailureReason(showFailureReason);

        if(proxyHost != null && !StringUtil.isEmpty(proxyHost)){
            Credentials creds = null;
            if(proxyUser != null && !StringUtil.isEmpty(proxyUser)){
                creds = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            }
            notification.setProxy(proxyHost, Integer.parseInt(proxyPort), creds);
        }

        MsTeamsNotificationPayloadContent payload = new MsTeamsNotificationPayloadContent();
        payload.setAgentName("Build Agent 1");

        payload.setBranchDisplayName("master");
        payload.setBranchIsDefault(true);
        payload.setBuildDescriptionWithLinkSyntax(String.format("[Failed - My Awesome Build #5](http://buildserver/builds/)"));
        payload.setBuildFullName("The Awesome Build");
        payload.setBuildId("b123");
        payload.setBuildName("My Awesome Build");
        payload.setBuildResult("Failed");
        payload.setBuildStatusUrl("http://buildserver/builds");
        payload.setBuildTypeId("b123");
        payload.setColor("ff0000");
        List<Commit> commits = new ArrayList<Commit>();
        commits.add(new Commit("a5bdc78", "Bug fix for that awful thing", "jbloggs", "jbloggs"));
        commits.add(new Commit("cc4500d", "New feature that rocks", "bbrave", "bbrave"));
        commits.add(new Commit("abb23b4", "Merge of branch xyz", "ddruff", "ddruff"));
        payload.setCommits(commits);
        payload.setElapsedTime(13452);
        payload.setFirstFailedBuild(true);
        payload.setIsComplete(true);
        payload.setText("My Awesome build");
        notification.setPayload(payload);
        notification.setEnabled(true);

        return notification;
    }

    public Integer tryParseInt(String str) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            retVal = null; // or null if that is your preference
        }
        return retVal;
    }

    private HashMap<String, Object> handleConfigurationChange(HttpServletRequest request) throws IOException, MsTeamsConfigValidationException {
        setRequestParams(request);
        if(!isNullOrEmpty(proxyPassword)){
            proxyPassword = RSACipher.decryptWebRequestData(proxyPassword);
        }

        Validate(token, botName, iconUrl, maxCommitsToDisplay, showBuildAgent, proxyHost, proxyPort, proxyUser, proxyPassword);

        this.config.setToken(token);
        this.config.getContent().setBotName(botName);
        this.config.getContent().setIconUrl(iconUrl);
        this.config.getContent().setMaxCommitsToDisplay(Integer.parseInt(maxCommitsToDisplay));
        this.config.getContent().setShowBuildAgent(Boolean.parseBoolean(showBuildAgent));
        this.config.getContent().setShowCommits(Boolean.parseBoolean(showCommits));
        this.config.getContent().setShowCommitters(Boolean.parseBoolean(showCommitters));
        this.config.getContent().setShowElapsedBuildTime((Boolean.parseBoolean(showElapsedBuildTime)));
        this.config.getContent().setShowFailureReason((Boolean.parseBoolean(showFailureReason)));


        this.config.setProxyHost(proxyHost);
        this.config.setProxyPort(isNullOrEmpty(proxyPort) ? null : Integer.parseInt(proxyPort));
        this.config.setProxyUsername(proxyUser);
        this.config.setProxyPassword(proxyPassword);


        this.config.save();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Saved");
        return params;
    }

    public class MsTeamsConfigValidationException extends Exception {
        public MsTeamsConfigValidationException(String message) {
            super(message);
        }
    }
}
