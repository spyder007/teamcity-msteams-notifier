package msteamsnotifications.teamcity.settings;


import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MsTeamsNotificationProjectSettingsTest {
	ProjectSettingsManager psm = mock(ProjectSettingsManager.class);
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void TestFactory(){
		MsTeamsNotificationProjectSettingsFactory psf = new MsTeamsNotificationProjectSettingsFactory(psm);
		psf.createProjectSettings("project1");
	}

	@Test
	public void TestSettings(){

	}
	
}
