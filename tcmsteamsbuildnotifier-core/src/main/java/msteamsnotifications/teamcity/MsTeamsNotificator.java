package msteamsnotifications.teamcity;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRoot;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import msteamsnotifications.MsTeamsNotification;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MsTeamsNotificator implements Notificator {

    private final MsTeamsNotificationMainSettings mainConfig;
    private final MsTeamsNotificationFactory notificationFactory;
    private final MsTeamsNotificationPayloadManager payloadManager;
    private final SBuildServer buildServer;
    private ArrayList<UserPropertyInfo> userProps;
    private NotificationUtility notificationUtility;

    private static final String SLACK_USERNAME_KEY = "tcMsTeamsNotifier.userName";
    private static final String TYPE = "tcMsTmBldNotifier";

    public static final PropertyKey USERNAME_KEY = new NotificatorPropertyKey(TYPE, SLACK_USERNAME_KEY);

    public MsTeamsNotificator(NotificatorRegistry notificatorRegistry,
                            SBuildServer sBuildServer,
                            MsTeamsNotificationMainSettings configSettings,
                            MsTeamsNotificationFactory factory,
                            MsTeamsNotificationPayloadManager manager){
        Loggers.ACTIVITIES.debug("Registering MsTeamsNotificator...");

        userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(SLACK_USERNAME_KEY, "MsTeams Username"));
        notificatorRegistry.register(this, userProps);
        mainConfig = configSettings;
        notificationFactory = factory;
        payloadManager = manager;
        buildServer = sBuildServer;
        notificationUtility = new NotificationUtility();
    }

    public void register(){

    }

    @Override
    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasMsTeamsNameConfigured(sUser)){
                continue;
            }
            MsTeamsNotification msteamsNotification = createNotification(sUser);
            msteamsNotification.setPayload(payloadManager.buildStarted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(msteamsNotification);
        }
    }

    @Override
    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasMsTeamsNameConfigured(sUser)){
                continue;
            }
            MsTeamsNotification msteamsNotification = createNotification(sUser);
            msteamsNotification.setPayload(payloadManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(msteamsNotification);
        }
    }

    @Override
    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasMsTeamsNameConfigured(sUser)){
                continue;
            }
            MsTeamsNotification msteamsNotification = createNotification(sUser);
            msteamsNotification.setPayload(payloadManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(msteamsNotification);
        }
    }

    @Override
    public void notifyBuildFailedToStart(SRunningBuild sRunningBuild, Set<SUser> set) {
    }

    @Override
    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> set) {

    }

    @Override
    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasMsTeamsNameConfigured(sUser)){
                continue;
            }
            MsTeamsNotification msteamsNotification = createNotification(sUser);
            msteamsNotification.setPayload(payloadManager.beforeBuildFinish(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(msteamsNotification);
        }
    }

    @Override
    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(SBuildType sBuildType, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(Collection<TestName> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(Collection<TestName> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleAssigned(Collection<BuildProblemInfo> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleChanged(Collection<BuildProblemInfo> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyTestsMuted(Collection<STest> collection, MuteInfo muteInfo, Set<SUser> set) {

    }

    @Override
    public void notifyTestsUnmuted(Collection<STest> collection, MuteInfo muteInfo, SUser sUser, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsMuted(Collection<BuildProblemInfo> collection, MuteInfo muteInfo, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsUnmuted(Collection<BuildProblemInfo> collection, MuteInfo muteInfo, SUser sUser, Set<SUser> set) {

    }

    @NotNull
    @Override
    public String getNotificatorType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "MsTeams Notifier";
    }


    private boolean userHasMsTeamsNameConfigured(SUser sUser){
        String userName = sUser.getPropertyValue(USERNAME_KEY);

        return userName != null && StringUtil.isNotEmpty(userName);
    }

    private MsTeamsNotification createNotification(SUser sUser){
        MsTeamsNotification notification = notificationFactory.getMsTeamsNotification();
        String userName = sUser.getPropertyValue(USERNAME_KEY);
        if(userName.substring(0,1) == "@"){
            userName = userName.substring(1);
        }
        notification.setToken(mainConfig.getToken());
        notification.setIconUrl(mainConfig.getIconUrl());
        notification.setBotName(mainConfig.getBotName());
        notification.setEnabled(mainConfig.getEnabled());
        notification.setShowBuildAgent(mainConfig.getShowBuildAgent());
        notification.setShowElapsedBuildTime(mainConfig.getShowElapsedBuildTime());
        notification.setShowCommits(mainConfig.getShowCommits());
        notification.setMaxCommitsToDisplay(mainConfig.getMaxCommitsToDisplay());
        notification.setMentionChannelEnabled(false);
        notification.setShowFailureReason(mainConfig.getShowFailureReason());

        return notification;

    }

    @Nullable
    private SFinishedBuild getPreviousNonPersonalBuild(SRunningBuild paramSRunningBuild)
    {
        List<SFinishedBuild> localList = buildServer.getHistory().getEntriesBefore(paramSRunningBuild, false);

        for (SFinishedBuild localSFinishedBuild : localList)
            if (!(localSFinishedBuild.isPersonal())) return localSFinishedBuild;
        return null;
    }

    private void doNotification(MsTeamsNotification notification) {
        notificationUtility.doPost(notification);
    }
}
