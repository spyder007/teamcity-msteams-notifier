
package msteamsnotifications.teamcity.payload;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.tests.TestName;
import msteamsnotifications.teamcity.BuildStateEnum;
import msteamsnotifications.teamcity.Loggers;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;

import java.util.Collection;

public class MsTeamsNotificationPayloadManager {

    private static final String NOBODY = "nobody";
	SBuildServer server;

    public MsTeamsNotificationPayloadManager(SBuildServer server){
        this.server = server;
        Loggers.SERVER.info("MsTeamsNotificationPayloadManager :: Starting");
    }


    public MsTeamsNotificationPayloadContent beforeBuildFinish(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
        return content;
    }


    public MsTeamsNotificationPayloadContent buildFinished(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_FINISHED);
        return content;
    }

    public MsTeamsNotificationPayloadContent buildInterrupted(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_INTERRUPTED);
        return content;
    }

    public MsTeamsNotificationPayloadContent buildStarted(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_STARTED);
        return content;
    }

    /** Used by versions of TeamCity less than 7.0
     */
    public MsTeamsNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityInfo responsibilityInfoOld,
                                     ResponsibilityInfo responsibilityInfoNew, boolean isUserAction) {

        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED);
        String oldUser = NOBODY;
        String newUser = NOBODY;
        try {
            oldUser = responsibilityInfoOld.getResponsibleUser().getDescriptiveName();
        } catch (Exception e) {}
        try {
            newUser = responsibilityInfoNew.getResponsibleUser().getDescriptiveName();
        } catch (Exception e) {}

        content.setText(buildType.getFullName() 
                        + " changed responsibility from "
                        + oldUser
                        + " to "
                        + newUser
                        + " with comment '"
                        + responsibilityInfoNew.getComment().trim()
                        + "'"
        );

        return content;
    }

    /** Used by versions of TeamCity 7.0 and above
     */
    public MsTeamsNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityEntry responsibilityEntryOld,
                                     ResponsibilityEntry responsibilityEntryNew) {

        MsTeamsNotificationPayloadContent content = new MsTeamsNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED);
        String oldUser = NOBODY;
        String newUser = NOBODY;
        if (responsibilityEntryOld.getState() != ResponsibilityEntry.State.NONE) {
            oldUser = responsibilityEntryOld.getResponsibleUser().getDescriptiveName();
        }
        if (responsibilityEntryNew.getState() != ResponsibilityEntry.State.NONE) {
            newUser = responsibilityEntryNew.getResponsibleUser().getDescriptiveName();
        }


        content.setText(buildType.getFullName().trim()
                        + " changed responsibility from "
                        + oldUser
                        + " to "
                        + newUser
                        + " with comment '"
                        + responsibilityEntryNew.getComment().trim()
                        + "'"
        );

        return content;
    }

    public MsTeamsNotificationPayloadContent responsibleChanged(SProject project,
                                     TestNameResponsibilityEntry oldTestNameResponsibilityEntry,
                                     TestNameResponsibilityEntry newTestNameResponsibilityEntry,
                                     boolean isUserAction) {
        // TODO Auto-generated method stub
        return null;
    }

    public MsTeamsNotificationPayloadContent responsibleChanged(SProject project,
                                     Collection<TestName> testNames, ResponsibilityEntry entry,
                                     boolean isUserAction) {
        // TODO Auto-generated method stub
        return null;
    }

/*
	HashMap<String, MsTeamsNotificationPayload> formats = new HashMap<String,MsTeamsNotificationPayload>();
	Comparator<MsTeamsNotificationPayload> rankComparator = new MsTeamsNotificationPayloadRankingComparator();
	List<MsTeamsNotificationPayload> orderedFormatCollection = new ArrayList<MsTeamsNotificationPayload>();
	SBuildServer server;
	
	public MsTeamsNotificationPayloadManager(SBuildServer server){
		this.server = server;
		Loggers.SERVER.info("MsTeamsNotificationPayloadManager :: Starting");
	}
	
	public void registerPayloadFormat(MsTeamsNotificationPayload payloadFormat){
		Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering payload " 
				+ payloadFormat.getFormatShortName() 
				+ " with rank of " + payloadFormat.getRank());
		formats.put(payloadFormat.getFormatShortName(),payloadFormat);
		this.orderedFormatCollection.add(payloadFormat);
		
		Collections.sort(this.orderedFormatCollection, rankComparator);
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payloads list is " + this.orderedFormatCollection.size() + " items long. Payloads are ranked in the following order..");
		for (MsTeamsNotificationPayload pl : this.orderedFormatCollection){
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payload Name: " + pl.getFormatShortName() + " Rank: " + pl.getRank());
		}
	}

	public MsTeamsNotificationPayload getFormat(String formatShortname){
		if (formats.containsKey(formatShortname)){
			return formats.get(formatShortname);
		}
		return null;
	}
	
	public Boolean isRegisteredFormat(String format){
		return formats.containsKey(format);
	}
	
	public Set<String> getRegisteredFormats(){
		return formats.keySet();
	}
	
	public Collection<MsTeamsNotificationPayload> getRegisteredFormatsAsCollection(){
		return orderedFormatCollection;
	}

	public SBuildServer getServer() {
		return server;
	}	
*/
	
}
