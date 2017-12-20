package msteamsnotifications.testframework.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ConfigLoaderUtil {
	
	public static Element getFullConfigElement(File file) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		Document doc = builder.build(file);
		return doc.getRootElement();
	}

	public static MsTeamsNotificationConfig getFirstMsTeamsNotificationInConfig(File f) throws JDOMException, IOException{
		Element fileAsElement = ConfigLoaderUtil.getFullConfigElement(f);
		assertTrue("One and only one msteamsNotifications expected when loading test config from file : " + f.getName(), fileAsElement.getChild("msteamsNotifications").getChildren("msteamsNotification").size() == 1);
		return new MsTeamsNotificationConfig((Element) fileAsElement.getChild("msteamsNotifications").getChildren("msteamsNotification").get(0));
	}
	
}
