package msteamsnotifications.teamcity;


import org.apache.http.client.HttpClient;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.MsTeamsNotificationImpl;
import msteamsnotifications.MsTeamsNotificationProxyConfig;

public class MsTeamsNotificationFactoryImpl implements MsTeamsNotificationFactory {
	public MsTeamsNotification getMsTeamsNotification(){
		return new MsTeamsNotificationImpl();
	}

	@Override
	public MsTeamsNotification getMsTeamsNotification(String proxy, Integer proxyPort) {
			return new MsTeamsNotificationImpl(proxy, proxyPort);
	}

    @Override
    public MsTeamsNotification getMsTeamsNotification(HttpClient httpClient) {
        return new MsTeamsNotificationImpl(httpClient);
    }

    public MsTeamsNotification getMsTeamsNotification(String proxy, String proxyPort) {
		return new MsTeamsNotificationImpl(proxy, proxyPort);
	}

	public MsTeamsNotification getMsTeamsNotification(MsTeamsNotificationProxyConfig proxyConfig) {
		return new MsTeamsNotificationImpl(proxyConfig);
	}
}
