package msteamsnotifications.teamcity;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.MsTeamsNotificationImpl;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.payload.content.PostMessageResponse;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;

public class MsTeamsNotificationListenerTest {
	SBuildServer sBuildServer = mock(SBuildServer.class);
	BuildHistory buildHistory = mock(BuildHistory.class);
	ProjectManager projectManager = mock(ProjectManager.class);
	ProjectSettingsManager settings = mock(ProjectSettingsManager.class);
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
	MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01");
	MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	MsTeamsNotificationListener whl;


    @After
    @Before
    public void deleteMsTeamsConfigFile(){
        DeleteConfigFiles();
    }
    
    private void DeleteConfigFiles() {
        File outputFile = new File("msteams", "msteams-config.xml");
        outputFile.delete();

        File outputDir = new File("msteams");
        outputDir.delete();
    }
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, ""));
        PostMessageResponse successfulResponse = new PostMessageResponse();
        successfulResponse.setOk(true);
        successfulResponse.setError("channel_not_found");
        response.setEntity(new StringEntity(successfulResponse.toJson()));

        when(httpClient.execute(isA(HttpUriRequest.class))).thenReturn(response);
		msteamsNotificationImpl = new MsTeamsNotificationImpl(httpClient);
		spyMsTeamsNotification = spy(msteamsNotificationImpl);
		whl = new MsTeamsNotificationListener(sBuildServer, settings, configSettings, manager, factory);
		projSettings = new MsTeamsNotificationProjectSettings();
		when(factory.getMsTeamsNotification()).thenReturn(spyMsTeamsNotification);
		//when(manager.isRegisteredFormat("JSON")).thenReturn(true);
//		when(manager.getFormat("JSON")).thenReturn(payload);
		//when(manager.getServer()).thenReturn(sBuildServer);
		when(sBuildServer.getProjectManager()).thenReturn(projectManager);
		when(projectManager.findProjectById("project1")).thenReturn(sProject);
		when(sBuildServer.getHistory()).thenReturn(buildHistory);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.server");
		when(previousSuccessfulBuild.getBuildStatus()).thenReturn(Status.NORMAL);
		when(previousSuccessfulBuild.isPersonal()).thenReturn(false);
		when(previousFailedBuild.getBuildStatus()).thenReturn(Status.FAILURE);
		when(previousFailedBuild.isPersonal()).thenReturn(false);
		finishedSuccessfulBuilds.add(previousSuccessfulBuild);
		finishedFailedBuilds.add(previousFailedBuild);
		sBuildType.setProject(sProject);
		when(settings.getSettings(sRunningBuild.getProjectId(), "msteamsNotifications")).thenReturn(projSettings);
		whl.register();
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unused")
	@Test
	public void testMsTeamsNotificationListener() {
		MsTeamsNotificationListener whl = new MsTeamsNotificationListener(sBuildServer, settings,configSettings, manager, factory);
	}

	@Test
	public void testRegister() {
		MsTeamsNotificationListener whl = new MsTeamsNotificationListener(sBuildServer, settings,configSettings, manager, factory);
		whl.register();
		verify(sBuildServer).addListener(whl);
	}	@Test
	public void testGetFromConfig() {
        String expectedConfigDirectory = ".";
        ServerPaths serverPaths = mock(ServerPaths.class);
        when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);
	    BuildState buildState = new BuildState();
	    MsTeamsNotificationMainSettings mainSettings = new MsTeamsNotificationMainSettings(sBuildServer, serverPaths);
	    mainSettings.readFrom(getFullConfigElement());
	    MsTeamsNotificationConfig config = new MsTeamsNotificationConfig("", true, buildState, true, true, null, true, true);
	    MsTeamsNotificationListener whl = new MsTeamsNotificationListener(sBuildServer, settings, mainSettings, manager, factory);
	    
	    whl.getFromConfig(msteamsNotificationImpl, config);
	    
		assertEquals("myproxy.mycompany.com", msteamsNotificationImpl.getProxyHost());
		assertEquals(8080, msteamsNotificationImpl.getProxyPort());
	}
	
	private Element getFullConfigElement(){
        return getElement("src/test/resources/main-config-full.xml");
    }

    private Element getElement(String filePath){
        SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
        builder.setIgnoringElementContentWhitespace(true);
        try {
            Document doc = builder.build(filePath);
            return doc.getRootElement();
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

	@Test
	public void testBuildStartedSRunningBuild() throws FileNotFoundException, IOException {
		BuildState state = new BuildState().setAllEnabled();
		projSettings.addNewMsTeamsNotification("", "project1", true, state, true, true, new HashSet<String>(), true, true);
		when(msteamsnotification.isEnabled()).thenReturn(state.allEnabled());
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedSuccessfulBuilds);
		
		whl.buildStarted(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(1)).post();
	}

	@Test
	public void testBuildFinishedSRunningBuild() throws FileNotFoundException, IOException {
		BuildState state = new BuildState().setAllEnabled();
		projSettings.addNewMsTeamsNotification("", "1234", true, state , true, true, new HashSet<String>(), true, true);
		when(msteamsnotification.isEnabled()).thenReturn(state.allEnabled());
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedSuccessfulBuilds);
		
		whl.buildFinished(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(1)).post();
	}
	
	@Test
	public void testBuildFinishedSRunningBuildSuccessAfterFailure() throws FileNotFoundException, IOException {
		BuildState state = new BuildState();
		state.enable(BuildStateEnum.BUILD_FIXED);
		state.enable(BuildStateEnum.BUILD_FINISHED);
		state.enable(BuildStateEnum.BUILD_SUCCESSFUL);
		projSettings.addNewMsTeamsNotification("", "1234", true, state, true, true, new HashSet<String>(), true, true);
		when(msteamsnotification.isEnabled()).thenReturn(state.enabled(BuildStateEnum.BUILD_FIXED));
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedFailedBuilds);
		
		whl.buildFinished(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(1)).post();
	}
	
	@Test
	public void testBuildFinishedSRunningBuildSuccessAfterSuccess() throws FileNotFoundException, IOException {
		BuildState state = new BuildState();
		state.enable(BuildStateEnum.BUILD_FIXED);
		projSettings.addNewMsTeamsNotification("", "1234",  true, state, true, true, new HashSet<String>(), true, true);
		when(msteamsnotification.isEnabled()).thenReturn(state.enabled(BuildStateEnum.BUILD_FIXED));
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedSuccessfulBuilds);
		
		whl.buildFinished(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(0)).post();
	}

	@Test
	public void testBuildInterruptedSRunningBuild() throws FileNotFoundException, IOException {
		BuildState state = new BuildState().setAllEnabled();
		projSettings.addNewMsTeamsNotification("", "1234",  true, state, true, true, new HashSet<String>(), true, true);
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedSuccessfulBuilds);
		
		whl.buildInterrupted(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(1)).post();
	}

	@Test
	public void testBeforeBuildFinishSRunningBuild() throws FileNotFoundException, IOException {
		BuildState state = new BuildState();
		state.enable(BuildStateEnum.BEFORE_BUILD_FINISHED);
		projSettings.addNewMsTeamsNotification("", "1234",  true, state, true, true, new HashSet<String>(), true, true);
		when(buildHistory.getEntriesBefore(sRunningBuild, false)).thenReturn(finishedSuccessfulBuilds);
		
		whl.beforeBuildFinish(sRunningBuild);
		verify(factory.getMsTeamsNotification(), times(1)).post();
	}

	@Test
	public void testBuildChangedStatusSRunningBuildStatusStatus() throws FileNotFoundException, IOException {
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		sBuildType.setProject(sProject);
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running", "TestBuild01");
		
		when(settings.getSettings(sRunningBuild.getProjectId(), "msteamsNotifications")).thenReturn(projSettings);
		
		MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
		sBuildType.setProject(sProject);
		MsTeamsNotificationListener whl = new MsTeamsNotificationListener(sBuildServer, settings,configSettings, manager, factory);
		Status oldStatus = Status.NORMAL;
		Status newStatus = Status.FAILURE;
		whl.register();
		whl.buildChangedStatus(sRunningBuild, oldStatus, newStatus);
		verify(factory.getMsTeamsNotification(), times(0)).post();
	}

//	@Test
//	public void testResponsibleChangedSBuildTypeResponsibilityInfoResponsibilityInfoBoolean() {
//		
//	}

}
