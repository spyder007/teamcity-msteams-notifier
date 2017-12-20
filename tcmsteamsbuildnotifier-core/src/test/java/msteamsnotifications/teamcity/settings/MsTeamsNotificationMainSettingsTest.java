package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import msteamsnotifications.MsTeamsNotificationProxyConfig;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.auth.Credentials;

public class MsTeamsNotificationMainSettingsTest {
	SBuildServer server = mock(SBuildServer.class);
	Integer proxyPort = 8080;
	String proxyHost = "myproxy.mycompany.com";
    String token = "thisismytoken";
	String iconUrl = "http://www.myicon.com/icon.gif";
    String botName = "Team City";

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

    @Test
	public void TestFullConfig(){
        String expectedConfigDirectory = ".";
        ServerPaths serverPaths = mock(ServerPaths.class);
        when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

		MsTeamsNotificationMainSettings whms = new MsTeamsNotificationMainSettings(server, serverPaths);
		whms.register();
		whms.readFrom(getFullConfigElement());
		String proxy = whms.getProxy();
		MsTeamsNotificationProxyConfig whpc = whms.getProxyConfig();
		assertTrue(proxy.equals(this.proxyHost));
		assertTrue(whpc.getProxyHost().equals(this.proxyHost ));
		assertTrue(whpc.getProxyPort().equals(this.proxyPort));
        assertTrue(whms.getToken().equals(this.token));
        assertTrue(whms.getIconUrl().equals(this.iconUrl));
        assertTrue(whms.getBotName().equals(this.botName));
        assertTrue(whms.getShowBuildAgent());
        assertTrue(whms.getShowElapsedBuildTime());
        assertFalse(whms.getShowCommits());
        assertEquals(15, whms.getMaxCommitsToDisplay());
        assertTrue(whms.getShowFailureReason());

        Credentials credentials = whpc.getCreds();
        
		assertEquals("some-username", credentials.getUserPrincipal().getName());
		assertEquals("some-password", credentials.getPassword());
	}

    @Test
    public void TestEmptyDefaultsConfig(){
        String expectedConfigDirectory = ".";
        ServerPaths serverPaths = mock(ServerPaths.class);
        when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

        MsTeamsNotificationMainSettings whms = new MsTeamsNotificationMainSettings(server, serverPaths);
        whms.register();
        whms.readFrom(getEmptyDefaultsConfigElement());
        String proxy = whms.getProxy();
        MsTeamsNotificationProxyConfig whpc = whms.getProxyConfig();
        assertTrue(proxy.equals(this.proxyHost));
        assertTrue(whpc.getProxyHost().equals(this.proxyHost ));
        assertTrue(whpc.getProxyPort().equals(this.proxyPort));
        assertTrue(whms.getToken().equals(this.token));
        assertTrue(whms.getIconUrl().equals(this.iconUrl));
        assertTrue(whms.getBotName().equals(this.botName));
        assertNull(whms.getShowBuildAgent());
        assertNull(whms.getShowElapsedBuildTime());
        assertTrue(whms.getShowCommits());
        assertEquals(5, whms.getMaxCommitsToDisplay());
        assertNull(whms.getShowFailureReason());

    }

    private Element getFullConfigElement(){
        return getElement("src/test/resources/main-config-full.xml");
    }

    private Element getEmptyDefaultsConfigElement(){
        return getElement("src/test/resources/main-config-empty-defaults.xml");
    }

    private Element getElement(String filePath){
        SAXBuilder builder = new SAXBuilder();
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
}
