package msteamsnotifications.teamcity.extension;



import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.junit.Ignore;
import org.junit.Test;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainConfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MsTeamsNotificationSettingsControllerTest {
    SBuildServer sBuildServer = mock(SBuildServer.class);
    WebControllerManager webControllerManager = mock(WebControllerManager.class);

    @Test
    public void createMockNotification_constructsValidNotification(){
        String expectedConfigDirectory = ".";
        ServerPaths serverPaths = mock(ServerPaths.class);
        when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

        PluginDescriptor pluginDescriptor = mock(PluginDescriptor.class);

        MsTeamsNotificationMainConfig config = new MsTeamsNotificationMainConfig(serverPaths);

        MsTeamsNotificationPayloadManager payloadManager = new MsTeamsNotificationPayloadManager(sBuildServer);
        MsTeamsNotifierSettingsController controller = new MsTeamsNotifierSettingsController(
                sBuildServer, serverPaths, webControllerManager,
                config, payloadManager, pluginDescriptor);

        MsTeamsNotification notification = controller.createMockNotification("The Bot", "tokenthingy",
                MsTeamsNotificationMainConfig.DEFAULT_ICONURL, 5, true, true, true, true, true, null, null, null, null);

        assertNotNull(notification);
        assertEquals(MsTeamsNotificationMainConfig.DEFAULT_ICONURL, notification.getIconUrl());
    }
}
