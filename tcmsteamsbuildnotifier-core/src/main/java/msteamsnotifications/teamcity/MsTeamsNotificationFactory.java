package msteamsnotifications.teamcity;

import org.apache.http.client.HttpClient;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.MsTeamsNotificationProxyConfig;

public interface MsTeamsNotificationFactory {
	public abstract MsTeamsNotification getMsTeamsNotification();
	public abstract MsTeamsNotification getMsTeamsNotification(String proxy,
                                                           Integer proxyPort);

    public abstract MsTeamsNotification getMsTeamsNotification(HttpClient httpClient);

//	public abstract MsTeamsNotification getMsTeamsNotification(String proxy, String proxyPortString);
//	public abstract MsTeamsNotification getMsTeamsNotification(MsTeamsNotificationProxyConfig pc);
}
