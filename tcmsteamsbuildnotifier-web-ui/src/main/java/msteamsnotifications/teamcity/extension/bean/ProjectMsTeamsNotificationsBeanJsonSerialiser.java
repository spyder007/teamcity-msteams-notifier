package msteamsnotifications.teamcity.extension.bean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class ProjectMsTeamsNotificationsBeanJsonSerialiser {
	private ProjectMsTeamsNotificationsBeanJsonSerialiser(){}
	public static String serialise(ProjectMsTeamsNotificationsBean project){
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("projectMsTeamsnotificationConfig", ProjectMsTeamsNotificationsBean.class);
        /* For some reason, the items are coming back as "@name" and "@value"
         * so strip those out with a regex.
         */
		return xstream.toXML(project);
	}

}
