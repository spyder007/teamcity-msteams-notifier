package msteamsnotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.JDOMException;
import org.junit.Test;
import msteamsnotifications.teamcity.BuildStateEnum;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.testframework.MsTeamsNotificationMockingFramework;
import msteamsnotifications.testframework.MsTeamsNotificationMockingFrameworkImpl;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.mock;

public class ProjectMsTeamsNotificationsBeanTest {

	SortedMap<String, String> map = new TreeMap<String, String>();
	MsTeamsNotificationMockingFramework framework;
    SBuildServer sBuildServer = mock(SBuildServer.class);

	@Test
	public void JsonSerialisationTest() throws JDOMException, IOException {
        ServerPaths serverPaths = mock(ServerPaths.class);
        MsTeamsNotificationMainSettings myMainSettings = new MsTeamsNotificationMainSettings(sBuildServer, serverPaths);
        framework = MsTeamsNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadMsTeamsNotificationProjectSettingsFromConfigXml(new File("../tcmsteamsbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectMsTeamsNotificationsBean msteamsnotificationsConfig = ProjectMsTeamsNotificationsBean.build(framework.getMsTeamsNotificationProjectSettings() , framework.getServer().getProjectManager().findProjectById("project01"), myMainSettings);
		System.out.println(ProjectMsTeamsNotificationsBeanJsonSerialiser.serialise(msteamsnotificationsConfig));
	}
	
	@Test
	public void JsonBuildSerialisationTest() throws JDOMException, IOException {
        ServerPaths serverPaths = mock(ServerPaths.class);
        MsTeamsNotificationMainSettings myMainSettings = new MsTeamsNotificationMainSettings(sBuildServer, serverPaths);
        framework = MsTeamsNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadMsTeamsNotificationProjectSettingsFromConfigXml(new File("../tcmsteamsbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectMsTeamsNotificationsBean msteamsnotificationsConfig = ProjectMsTeamsNotificationsBean.build(framework.getMsTeamsNotificationProjectSettings() ,framework.getSBuildType() ,framework.getServer().getProjectManager().findProjectById("project01"), myMainSettings);
		System.out.println(ProjectMsTeamsNotificationsBeanJsonSerialiser.serialise(msteamsnotificationsConfig));
	}

}
