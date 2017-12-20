package msteamsnotifications.teamcity;

import org.apache.http.HttpStatus;
import msteamsnotifications.MsTeamsNotification;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Lokum on 22.06.2016.
 */
public final class NotificationUtility {

    public void doPost(MsTeamsNotification notification){
        try {
            if (notification.isEnabled()){
                notification.post();
                if (notification.getResponse() != null && !notification.getResponse().getOk()) {
                    Loggers.SERVER.error(this.getClass().getSimpleName() + " :: MsTeamsNotification failed : "
                            + " returned error " + notification.getResponse().getError()
                            + " " + notification.getErrorReason());
                }
                else {
                    Loggers.SERVER.info(this.getClass().getSimpleName() + " :: MsTeamsNotification delivered : "
                            + " returned " + notification.getStatus()
                            + " " + notification.getErrorReason());
                }
                Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doPost :: content dump: " + notification.getPayload());
                if (notification.isErrored()){
                    Loggers.SERVER.error(notification.getErrorReason());
                }
                if ((notification.getStatus() == null || notification.getStatus() > HttpStatus.SC_OK))
                    Loggers.ACTIVITIES.warn("MsTeamsNotificationListener :: " + notification.getParam("projectId") + " MsTeamsNotification (url:  proxy: " + notification.getProxyHost() + ":" + notification.getProxyPort()+") returned HTTP status " + notification.getStatus().toString());

            } else {
                Loggers.SERVER.debug("MsTeamsNotification NOT triggered: "
                        + notification.getParam("buildStatus") + " ");
            }
        } catch (FileNotFoundException e) {
            Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                    + "A FileNotFoundException occurred while attempting to execute MsTeamsNotification. See the following stacktrace");
            Loggers.SERVER.warn(e);
        } catch (IOException e) {
            Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                    + "An IOException occurred while attempting to execute MsTeamsNotification. See the following stacktrace");
            Loggers.SERVER.warn(e);
        }
    }
}
