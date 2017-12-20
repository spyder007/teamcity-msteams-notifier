package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.SBuildType;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import msteamsnotifications.teamcity.MockSBuildType;
import msteamsnotifications.testframework.util.ConfigLoaderUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MsTeamsNotificationConfigTestForBuildTypeSpecifics {

	MsTeamsNotificationConfig msteamsnotificationAllBuilds;
	MsTeamsNotificationConfig msteamsnotificationSpecificBuilds;
	SBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	SBuildType sBuildType02 = new MockSBuildType("Test Build", "A Test Build", "bt2");
	SBuildType sBuildType03 = new MockSBuildType("Test Build", "A Test Build", "bt3");
	
	
	@Before
	public void setup() throws JDOMException, IOException{
		msteamsnotificationSpecificBuilds  = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		msteamsnotificationAllBuilds  = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled.xml"));
	}

	@Test
	public void testGetBuildTypeEnabled() {
		assertTrue(msteamsnotificationSpecificBuilds.isEnabledForBuildType(sBuildType));
		assertTrue(msteamsnotificationSpecificBuilds.isEnabledForBuildType(sBuildType02));
		assertFalse(msteamsnotificationSpecificBuilds.isEnabledForBuildType(sBuildType03));
		
		assertTrue(msteamsnotificationAllBuilds.isEnabledForBuildType(sBuildType));
		assertTrue(msteamsnotificationAllBuilds.isEnabledForBuildType(sBuildType02));
		assertTrue(msteamsnotificationAllBuilds.isEnabledForBuildType(sBuildType03));
	}
	
	@Test
	public void testGetAsElementSpecific() {
		Element e = msteamsnotificationSpecificBuilds.getAsElement();
		MsTeamsNotificationConfig whc = new MsTeamsNotificationConfig(e);
		assertTrue(whc.isEnabledForBuildType(sBuildType));
		assertTrue(whc.isEnabledForBuildType(sBuildType02));
		assertFalse(whc.isEnabledForBuildType(sBuildType03));
	}

	@Test
	public void testGetAsElementAll() {

		Element e = msteamsnotificationAllBuilds.getAsElement();
		MsTeamsNotificationConfig whc = new MsTeamsNotificationConfig(e);
		assertTrue(whc.isEnabledForBuildType(sBuildType));
		assertTrue(whc.isEnabledForBuildType(sBuildType02));
		assertTrue(whc.isEnabledForBuildType(sBuildType03));
	}
}
