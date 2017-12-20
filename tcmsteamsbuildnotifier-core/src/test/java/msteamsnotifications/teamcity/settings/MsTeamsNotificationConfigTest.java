package msteamsnotifications.teamcity.settings;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import msteamsnotifications.testframework.util.ConfigLoaderUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MsTeamsNotificationConfigTest {
	
	private static final String EMPTY_STRING = "";
	private static final String CHECKED = "checked ";
	MsTeamsNotificationConfig msteamsnotificationAllEnabled;
	MsTeamsNotificationConfig msteamsnotificationAllDisabled;
	MsTeamsNotificationConfig msteamsnotificationDisabled;
	MsTeamsNotificationConfig msteamsnotificationMostEnabled;
    MsTeamsNotificationConfig msteamsnotificationCustomContent;


    @Before
	public void setup() throws JDOMException, IOException{
		
		msteamsnotificationAllEnabled  = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled.xml"));
		msteamsnotificationAllDisabled = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-disabled.xml"));
		msteamsnotificationDisabled    = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-msteamsnotifications-disabled.xml"));
		msteamsnotificationMostEnabled = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-all-but-respchange-states-enabled.xml"));
        msteamsnotificationCustomContent = ConfigLoaderUtil.getFirstMsTeamsNotificationInConfig(new File("src/test/resources/project-settings-test-custom-content.xml"));
	}
	
//	private MsTeamsNotificationConfig getFirstMsTeamsNotificationInConfig(File f) throws JDOMException, IOException{
//		Element fileAsElement = ConfigLoaderUtil.getFullConfigElement(f);
//		assertTrue("One and only one msteamsnotifications expected when loading test config from file : " + f.getName(), fileAsElement.getChild("msteamsNotifications").getChildren("msteamsnotifications").size() == 1);
//		return new MsTeamsNotificationConfig((Element) fileAsElement.getChild("msteamsNotifications").getChildren("msteamsnotifications").get(0));
//	}

	@Test
	public void testGetEnabled() {
		assertTrue(msteamsnotificationAllEnabled.getEnabled());
		assertTrue(msteamsnotificationAllDisabled.getEnabled());
		assertFalse(msteamsnotificationDisabled.getEnabled());
	}

	@Test
	public void testSetEnabled() {
		assertTrue(msteamsnotificationAllEnabled.getEnabled());
		msteamsnotificationAllEnabled.setEnabled(false);
		assertFalse(msteamsnotificationAllEnabled.getEnabled());
	}

	@Test
	public void testGetBuildStates() {
		assertTrue(msteamsnotificationAllEnabled.getBuildStates().allEnabled());
		assertFalse(msteamsnotificationAllDisabled.getBuildStates().allEnabled());
		assertFalse(msteamsnotificationDisabled.getBuildStates().allEnabled());
	}

	@Test
	public void testGetToken() {
		assertTrue(msteamsnotificationAllEnabled.getToken().equals("hook.msteams.com"));
	}

	@Test
	public void testGetUniqueKey() {
		assertFalse(msteamsnotificationAllEnabled.getUniqueKey().equals(EMPTY_STRING));
	}

	@Test
	public void testSetUniqueKey() {
		String s = msteamsnotificationAllEnabled.getUniqueKey();
		msteamsnotificationAllEnabled.setUniqueKey("SomethingElse");
		assertFalse(msteamsnotificationAllEnabled.getUniqueKey().equals(s));
		assertTrue(msteamsnotificationAllEnabled.getUniqueKey().equals("SomethingElse"));
	}

	@Test
	public void testGetEnabledListAsString() {
		assertTrue(msteamsnotificationAllEnabled.getEnabledListAsString().equals("All Build Events"));
		assertTrue(msteamsnotificationAllDisabled.getEnabledListAsString().equals("None"));
		assertTrue(msteamsnotificationMostEnabled.getEnabledListAsString().equals(" Build Started, Build Interrupted, Build Almost Completed, Build Failed, Build Successful"));
	}

	@Test
	public void testGetMsTeamsNotificationEnabledAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getMsTeamsNotificationEnabledAsChecked().equals(CHECKED));
		assertTrue(msteamsnotificationAllDisabled.getMsTeamsNotificationEnabledAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateAllAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateAllAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateAllAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildStartedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBuildStartedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildStartedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFinishedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBeforeFinishedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateResponsibilityChangedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateResponsibilityChangedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateResponsibilityChangedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildInterruptedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildSuccessfulAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFixedAsChecked() {
		assertFalse(msteamsnotificationAllEnabled.getStateBuildFixedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildFixedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFailedAsChecked() {
		assertTrue(msteamsnotificationAllEnabled.getStateBuildFailedAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildFailedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildBrokenAsChecked() {
		assertFalse(msteamsnotificationAllEnabled.getStateBuildBrokenAsChecked().equals(CHECKED));
		assertFalse(msteamsnotificationAllDisabled.getStateBuildBrokenAsChecked().equals(CHECKED));
	}

    @Test
    public void loading_config_when_custom_content_section_is_present_sets_customContentEnabled(){
        assertTrue(msteamsnotificationCustomContent.hasCustomContent());
    }

    @Test
    public void loading_config_when_custom_content_section_is_not_present_does_not_set_customContentEnabled(){
        assertFalse(msteamsnotificationMostEnabled.hasCustomContent());
    }

    @Test
    public void loading_config_when_custom_content_section_is_present_sets_customContent(){
        assertNotSame(MsTeamsNotificationMainConfig.DEFAULT_BOTNAME, msteamsnotificationCustomContent.getContent().getBotName());
        assertNotSame(MsTeamsNotificationMainConfig.DEFAULT_ICONURL, msteamsnotificationCustomContent.getContent().getIconUrl());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowBuildAgent());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowElapsedBuildTime());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowCommits());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowElapsedBuildTime());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowFailureReason());
        assertEquals(20, msteamsnotificationCustomContent.getContent().getMaxCommitsToDisplay());
    }

    @Test
    public void getAsElement_when_custom_content_sets_customContent(){
        Element e = msteamsnotificationCustomContent.getAsElement();
        MsTeamsNotificationConfig config = new MsTeamsNotificationConfig(e);
        assertTrue(config.hasCustomContent());
        assertNotSame(MsTeamsNotificationMainConfig.DEFAULT_BOTNAME, msteamsnotificationCustomContent.getContent().getBotName());
        assertNotSame(MsTeamsNotificationMainConfig.DEFAULT_ICONURL, msteamsnotificationCustomContent.getContent().getIconUrl());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowBuildAgent());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowElapsedBuildTime());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowCommits());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowElapsedBuildTime());
        assertTrue(msteamsnotificationCustomContent.getContent().getShowFailureReason());
        assertEquals(20, msteamsnotificationCustomContent.getContent().getMaxCommitsToDisplay());
    }

}
