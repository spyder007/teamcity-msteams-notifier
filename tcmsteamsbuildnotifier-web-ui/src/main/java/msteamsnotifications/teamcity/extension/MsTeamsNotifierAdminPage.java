package msteamsnotifications.teamcity.extension;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;
import msteamsnotifications.MsTeamsNotificationProxyConfig;
import msteamsnotifications.teamcity.Loggers;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Peter on 24/01/2015.
 */
public class MsTeamsNotifierAdminPage extends AdminPage {


    private static final String AFTER_PAGE_ID = "jabber";
    private static final String BEFORE_PAGE_ID = "clouds";
    private static final String PAGE = "MsTeamsNotification/msTeamsAdminSettings.jsp";
    private static final String PLUGIN_NAME = "msTeamsNotifications";
    private static final String TAB_TITLE = "MsTeams Notifications";
    private final String jspHome;
    private SBuildServer sBuildServer;
    private MsTeamsNotificationMainSettings msteamsMainSettings;

    protected MsTeamsNotifierAdminPage(@NotNull PagePlaces pagePlaces,
                                     @NotNull PluginDescriptor descriptor,
                                     @NotNull SBuildServer sBuildServer,
                                     @NotNull MsTeamsNotificationMainSettings msteamsMainSettings
                                     ) {
        super(pagePlaces);
        this.sBuildServer = sBuildServer;
        this.msteamsMainSettings = msteamsMainSettings;

        setPluginName(PLUGIN_NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
        jspHome = descriptor.getPluginResourcesPath();
        setTabTitle(TAB_TITLE);
        ArrayList<String> after = new ArrayList<String>();
        after.add(AFTER_PAGE_ID);
        ArrayList<String> before = new ArrayList<String>();
        before.add(BEFORE_PAGE_ID);
        setPosition(PositionConstraint.between(after, before));
        register();
        Loggers.SERVER.info("MsTeams global configuration page registered");
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request){
        super.fillModel(model, request);
        msteamsMainSettings.refresh();
        model.put("token", this.msteamsMainSettings.getToken());
        model.put("botName", this.msteamsMainSettings.getBotName());
        model.put("iconUrl", this.msteamsMainSettings.getIconUrl());
        model.put("maxCommitsToDisplay", this.msteamsMainSettings.getMaxCommitsToDisplay());
        model.put("showBuildAgent", this.msteamsMainSettings.getShowBuildAgent());
        model.put("showCommits", this.msteamsMainSettings.getShowCommits());
        model.put("showCommitters", this.msteamsMainSettings.getShowCommitters());
        model.put("showElapsedBuildTime", this.msteamsMainSettings.getShowElapsedBuildTime());
        model.put("showFailureReason", this.msteamsMainSettings.getShowFailureReason());

        MsTeamsNotificationProxyConfig proxyConfig = this.msteamsMainSettings.getProxyConfig();
        model.put("proxyHost", proxyConfig.getProxyHost());
        model.put("proxyPort", proxyConfig.getProxyPort());
        model.put("proxyUser", proxyConfig.getCreds() == null ? null : proxyConfig.getCreds().getUserPrincipal().getName());
        model.put("proxyPassword", proxyConfig.getCreds() == null ? null : proxyConfig.getCreds().getPassword());
        model.put("encryptedProxyPassword", proxyConfig.getCreds() == null || proxyConfig.getCreds().getPassword() == null ? null : RSACipher.encryptDataForWeb(proxyConfig.getCreds().getPassword()));
        model.put("hexEncodedPublicKey", RSACipher.getHexEncodedPublicKey());

        try {
            model.put("pluginVersion", this.msteamsMainSettings.getPluginVersion());
        } catch (IOException e) {
            Loggers.ACTIVITIES.error("Could not retrieve msteams plugin version", e);
        }

        model.put("disabled", !this.msteamsMainSettings.getEnabled());
        model.put("jspHome", this.jspHome);
    }

    @NotNull
    @Override
    public String getGroup() {
        return SERVER_RELATED_GROUP;
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
    }
}
