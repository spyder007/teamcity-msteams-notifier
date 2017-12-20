package msteamsnotifications.teamcity.payload.util;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.junit.Before;
import org.junit.Test;
import msteamsnotifications.teamcity.BuildStateEnum;
import msteamsnotifications.teamcity.MockSBuildType;
import msteamsnotifications.teamcity.MockSProject;
import msteamsnotifications.teamcity.MockSRunningBuild;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;

import static org.mockito.Mockito.mock;

public class VariableMessageBuilderTest {
	
	MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01");
	SFinishedBuild previousSuccessfulBuild = mock(SFinishedBuild.class);
	MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	SBuildServer sBuildServer;

	@Before
	public void setup(){
		sBuildType.setProject(sProject);
		sBuildServer = mock(SBuildServer.class);
	}
	
	@Test
	public void testBuild() {
		MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(sBuildServer, sRunningBuild, previousSuccessfulBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
		VariableMessageBuilder builder = VariableMessageBuilder.create("This is a test ${buildFullName}", new MsTeamsNotificationBeanUtilsVariableResolver(content));
		System.out.println(builder.build());
		System.out.println(content.getBuildFullName());
	}

}
