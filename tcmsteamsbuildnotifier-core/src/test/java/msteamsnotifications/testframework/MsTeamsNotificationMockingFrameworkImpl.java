package msteamsnotifications.testframework;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.jdom.JDOMException;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.MsTeamsNotificationImpl;
import msteamsnotifications.teamcity.*;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;
import msteamsnotifications.testframework.util.ConfigLoaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MsTeamsNotificationMockingFrameworkImpl implements MsTeamsNotificationMockingFramework {
	
	MsTeamsNotificationPayloadContent content;
	MsTeamsNotificationConfig msteamsNotificationConfig;
	SBuildServer sBuildServer = mock(SBuildServer.class);
	BuildHistory buildHistory = mock(BuildHistory.class);
	ProjectSettingsManager settings = mock(ProjectSettingsManager.class);
	ProjectManager projectManager = mock(ProjectManager.class);
	MsTeamsNotificationMainSettings configSettings = mock(MsTeamsNotificationMainSettings.class);
	MsTeamsNotificationPayloadManager manager = mock(MsTeamsNotificationPayloadManager.class);
//	MsTeamsNotificationPayload payload = new MsTeamsNotificationPayloadDetailed(manager);
	MsTeamsNotificationProjectSettings projSettings;
	MsTeamsNotificationFactory factory = mock(MsTeamsNotificationFactory.class);
	MsTeamsNotification msteamsnotification = mock (MsTeamsNotification.class);
	MsTeamsNotification msteamsNotificationImpl;
	MsTeamsNotification spyMsTeamsNotification;
	SFinishedBuild previousSuccessfulBuild = mock(SFinishedBuild.class);
	SFinishedBuild previousFailedBuild = mock(SFinishedBuild.class);
	List<SFinishedBuild> finishedSuccessfulBuilds = new ArrayList<SFinishedBuild>();
	List<SFinishedBuild> finishedFailedBuilds = new ArrayList<SFinishedBuild>();
	SBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	SBuildType sBuildType02 = new MockSBuildType("Test Build-2", "A Test Build 02", "bt2");
	SBuildType sBuildType03 = new MockSBuildType("Test Build-2", "A Test Build 03", "bt3");
	SRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01");
	SProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	SProject sProject02 = new MockSProject("Test Project 02", "A test project 02", "project2", "TestProjectNumber02", sBuildType);
	SProject sProject03 = new MockSProject("Test Project 03", "A test sub project 03", "project3", "TestProjectNumber02_TestProjectNumber03", sBuildType);
	
	SBuildType build2 = mock(SBuildType.class);
	SBuildType build3 = mock(SBuildType.class);
	
	MsTeamsNotificationListener whl;
	BuildStateEnum buildstateEnum;
	
	private MsTeamsNotificationMockingFrameworkImpl() {
		msteamsNotificationImpl = new MsTeamsNotificationImpl();
		spyMsTeamsNotification = spy(msteamsNotificationImpl);
		whl = new MsTeamsNotificationListener(sBuildServer, settings, configSettings, manager, factory);
		projSettings = new MsTeamsNotificationProjectSettings();
		when(factory.getMsTeamsNotification()).thenReturn(spyMsTeamsNotification);
		//when(manager.isRegisteredFormat("JSON")).thenReturn(true);
//		when(manager.getFormat("JSON")).thenReturn(payload);
		//when(manager.getServer()).thenReturn(sBuildServer);
		when(projectManager.findProjectById("project01")).thenReturn(sProject);
		when(sBuildServer.getHistory()).thenReturn(buildHistory);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.server");
		when(sBuildServer.getProjectManager()).thenReturn(projectManager);
		when(previousSuccessfulBuild.getBuildStatus()).thenReturn(Status.NORMAL);
		when(previousSuccessfulBuild.isPersonal()).thenReturn(false);
		when(previousFailedBuild.getBuildStatus()).thenReturn(Status.FAILURE);
		when(previousFailedBuild.isPersonal()).thenReturn(false);
		finishedSuccessfulBuilds.add(previousSuccessfulBuild);
		finishedFailedBuilds.add(previousFailedBuild);
		((MockSBuildType) sBuildType).setProject(sProject);
		when(settings.getSettings(sRunningBuild.getProjectId(), "msteamsNotifications")).thenReturn(projSettings);
		
		when(build2.getBuildTypeId()).thenReturn("bt2");
		when(build2.getInternalId()).thenReturn("bt2");
		when(build2.getName()).thenReturn("This is Build 2");
		when(build3.getBuildTypeId()).thenReturn("bt3");
		when(build3.getInternalId()).thenReturn("bt3");
		when(build3.getName()).thenReturn("This is Build 3");
		((MockSProject) sProject).addANewBuildTypeToTheMock(build2);
		((MockSProject) sProject).addANewBuildTypeToTheMock(build3);
		((MockSProject) sProject02).addANewBuildTypeToTheMock(sBuildType02);
		((MockSProject) sProject03).addANewBuildTypeToTheMock(sBuildType03);
		((MockSProject) sProject03).setParentProject(sProject02);
		((MockSProject) sProject02).addChildProjectToMock(sProject03);
		whl.register();
		
	}

	public static MsTeamsNotificationMockingFramework create(BuildStateEnum buildState) {
		MsTeamsNotificationMockingFrameworkImpl framework = new MsTeamsNotificationMockingFrameworkImpl();
		framework.buildstateEnum = buildState;
		framework.content = new MsTeamsNotificationPayloadContent(framework.sBuildServer, framework.sRunningBuild, framework.previousSuccessfulBuild, buildState);
		return framework;
	}

	@Override
	public SBuildServer getServer() {
		return sBuildServer;
	}

	@Override
	public SRunningBuild getRunningBuild() {
		return sRunningBuild;
	}
	
	@Override
	public MsTeamsNotificationPayloadContent getMsTeamsNotificationContent() {
		return content;
	}

	@Override
	public void loadMsTeamsNotificationConfigXml(File xmlConfigFile) throws JDOMException, IOException {
		msteamsNotificationConfig = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(xmlConfigFile);
		this.content = new MsTeamsNotificationPayloadContent(this.sBuildServer, this.sRunningBuild, this.previousSuccessfulBuild, this.buildstateEnum);
		
	}
	
	@Override
	public void loadMsTeamsNotificationProjectSettingsFromConfigXml(File xmlConfigFile) throws IOException, JDOMException{
		projSettings.readFrom(ConfigLoaderUtil.getFullConfigElement(xmlConfigFile).getChild("msteamsNotifications"));
	}
	
	@Override
	public MsTeamsNotificationConfig getMsTeamsNotificationConfig() {
		return msteamsNotificationConfig;
	}

	@Override
	public MsTeamsNotificationProjectSettings getMsTeamsNotificationProjectSettings() {
		return projSettings;
	}

	@Override
	public MsTeamsNotificationPayloadManager getMsTeamsNotificationPayloadManager() {
		return manager;
	}

	@Override
	public SBuildType getSBuildType() {
		return sBuildType;
	}

	@Override
	public SBuildType getSBuildTypeFromSubProject() {
		return sBuildType03;
	}

}
