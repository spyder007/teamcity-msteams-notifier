package msteamsnotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import msteamsnotifications.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MsTeamsNotificationSettingsTest {

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
	
    @Ignore
	@Test
	public void test_AuthFailWrongCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		String expectedConfigDirectory = ".";
		ServerPaths serverPaths = mock(ServerPaths.class);
		when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

		MsTeamsNotificationTest test = new MsTeamsNotificationTest();
		MsTeamsNotificationMainConfig mainConfig = new MsTeamsNotificationMainConfig(serverPaths);
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		MsTeamsNotification w = new MsTeamsNotificationImpl(mainConfig.getProxyConfig());
		// w.setProxyUserAndPass("somethingIncorrect", "somethingIncorrect");
		MsTeamsNotificationTestServer s = test.startWebServer();
		MsTeamsNotificationTestProxyServer p = test.startProxyServerAuth("somthingCorrect", "somethingCorrect");
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
	}

    @Ignore
	@Test
	public void test_AuthFailNoCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		String expectedConfigDirectory = ".";
		ServerPaths serverPaths = mock(ServerPaths.class);
		when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

		MsTeamsNotificationTest test = new MsTeamsNotificationTest();
		MsTeamsNotificationMainConfig mainConfig = new MsTeamsNotificationMainConfig(serverPaths);
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		MsTeamsNotification w = new MsTeamsNotificationImpl(mainConfig.getProxyConfig());
		// w.setProxyUserAndPass("somethingIncorrect", "somethingIncorrect");
		MsTeamsNotificationTestServer s = test.startWebServer();
		MsTeamsNotificationTestProxyServer p = test.startProxyServerAuth("somethingCorrect", "somethingCorrect");
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
	}

    /*
    @Ignore
	@Test
	public void test_AuthPassNoCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		MsTeamsNotificationTest test = new MsTeamsNotificationTest();
		MsTeamsNotificationMainConfig mainConfig = new MsTeamsNotificationMainConfig();
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		MsTeamsNotification w = new MsTeamsNotificationImpl(url, mainConfig.getProxyConfigForUrl(url));
		MsTeamsNotificationTestServer s = test.startWebServer();
		MsTeamsNotificationTestProxyServer p = test.startProxyServer();
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_WebookConfig() throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		List<MsTeamsNotificationConfig> configs = new ArrayList<MsTeamsNotificationConfig>();
		builder.setIgnoringElementContentWhitespace(true);
			Document doc = builder.build("src/test/resources/testdoc2.xml");
			Element root = doc.getRootElement();
			if(root.getChild("msteamsNotifications") != null){
				Element child = root.getChild("msteamsNotifications");
				if ((child.getAttribute("enabled") != null) && (child.getAttribute("enabled").equals("true"))){
					List<Element> namedChildren = child.getChildren("msteamsNotification");
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
		            {
						Element e = i.next();
						MsTeamsNotificationConfig whConfig = new MsTeamsNotificationConfig(e);
						configs.add(whConfig);
		            }
				}
			}

		
		for (MsTeamsNotificationConfig c : configs){
			MsTeamsNotification wh = new MsTeamsNotificationImpl();
			wh.setEnabled(c.getEnabled());
			//msteamsNotification.addParams(c.getParams());
			System.out.println(wh.isEnabled().toString());

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_ReadXml() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		//builder.setValidation(true);
		builder.setIgnoringElementContentWhitespace(true);
		
			Document doc = builder.build("src/test/resources/testdoc1.xml");
			Element root = doc.getRootElement();
			System.out.println(root.toString());
			if(root.getChild("msteamsNotifications") != null){
				Element child = root.getChild("msteamsNotifications");
				if ((child.getAttribute("enabled") != null) && (child.getAttribute("enabled").equals("true"))){
					List<Element> namedChildren = child.getChildren("msteamsNotification");
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
		            {
						Element e = i.next();
						System.out.println(e.toString() + e.getAttributeValue("url"));
						//assertTrue(e.getAttributeValue("url").equals("http://something"));
						if(e.getChild("parameters") != null){
							Element eParams = e.getChild("parameters");
							List<Element> paramsList = eParams.getChildren("param");
							for(Iterator<Element> j = paramsList.iterator(); j.hasNext();)
							{
								Element eParam = j.next();
								System.out.println(eParam.toString() + eParam.getAttributeValue("name"));
								System.out.println(eParam.toString() + eParam.getAttributeValue("value"));
							}
						}
		            }
				}
			}

	}
}
