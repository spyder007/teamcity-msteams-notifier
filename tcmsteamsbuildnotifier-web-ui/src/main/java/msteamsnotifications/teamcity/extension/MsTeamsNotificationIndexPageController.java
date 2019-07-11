package msteamsnotifications.teamcity.extension;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import msteamsnotifications.teamcity.TeamCityIdResolver;
import msteamsnotifications.teamcity.extension.bean.ProjectMsTeamsNotificationsBean;
import msteamsnotifications.teamcity.extension.bean.ProjectMsTeamsNotificationsBeanJsonSerialiser;
import msteamsnotifications.teamcity.payload.MsTeamsNotificationPayloadManager;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationConfig;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationMainSettings;
import msteamsnotifications.teamcity.settings.MsTeamsNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;


public class MsTeamsNotificationIndexPageController extends BaseController {

		private static final String SHOW_FURTHER_READING = "ShowFurtherReading";
		private static final String SLACK_NOTIFICATIONS = "msteamsNotifications";
		private static final String NO_SLACK_NOTIFICATIONS = "noMsTeamsNotifications";
		private static final String PROJECT_ID = "projectId";
		private static final String FALSE = "false";
		private static final String ERROR_REASON = "errorReason";
		private static final String PROJECT_SLACK_NOTIFICATION_AS_JSON = "projectMsTeamsNotificationsAsJson";
		
		private final WebControllerManager myWebManager;
	    private final MsTeamsNotificationMainSettings myMainSettings;
	    private SBuildServer myServer;
	    private ProjectSettingsManager mySettings;
	    private PluginDescriptor myPluginDescriptor;
	    private final MsTeamsNotificationPayloadManager myManager;

	    public MsTeamsNotificationIndexPageController(SBuildServer server, WebControllerManager webManager,
                                                    ProjectSettingsManager settings, PluginDescriptor pluginDescriptor, MsTeamsNotificationPayloadManager manager,
                                                    MsTeamsNotificationMainSettings configSettings) {
	        super(server);
	        myWebManager = webManager;
	        myServer = server;
	        mySettings = settings;
	        myPluginDescriptor = pluginDescriptor;
	        myMainSettings = configSettings;
	        myManager = manager;
	    }

	    public void register(){
	      myWebManager.registerController("/msteamsnotifications/index.html", this);
	    }

		@Override
	    @Nullable
	    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    	
	        HashMap<String,Object> params = new HashMap<String,Object>();
	        params.put("jspHome",this.myPluginDescriptor.getPluginResourcesPath());
        	params.put("includeJquery", Boolean.toString(this.myServer.getVersion().getDisplayVersionMajor() < 7));
        	params.put("rootContext", myServer.getServerRootPath());
	        
	    	if (myMainSettings.getInfoUrl() != null && myMainSettings.getInfoUrl().length() > 0){
	    		params.put("moreInfoText", "<li><a href=\"" + myMainSettings.getInfoUrl() + "\">" + myMainSettings.getInfoText() + "</a></li>");
	    		if (myMainSettings.getMsTeamsNotificationShowFurtherReading()){
	    			params.put(SHOW_FURTHER_READING, "ALL");
	    		} else {
	    			params.put(SHOW_FURTHER_READING, "SINGLE");
	    		}
	    	} else if (myMainSettings.getMsTeamsNotificationShowFurtherReading()){
	    		params.put(SHOW_FURTHER_READING, "DEFAULT");
	    	} else {
	    		params.put(SHOW_FURTHER_READING, "NONE");
	    	}
	        
	        if(request.getParameter(PROJECT_ID) != null){
	        	
	        	SProject project = TeamCityIdResolver.findProjectById(this.myServer.getProjectManager(), request.getParameter("projectId"));
	        	if (project != null){
	        		
			    	MsTeamsNotificationProjectSettings projSettings = (MsTeamsNotificationProjectSettings)
			    			mySettings.getSettings(project.getProjectId(), SLACK_NOTIFICATIONS);
			    	
			        SUser myUser = SessionUser.getUser(request);
			        params.put("hasPermission", myUser.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT));
			    	
			    	String message = projSettings.getMsTeamsNotificationsAsString();
			    	
			    	params.put("haveProject", "true");
			    	params.put("messages", message);
			    	params.put(PROJECT_ID, project.getProjectId());
			    	params.put("buildTypeList", project.getBuildTypes());
			    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
			    	params.put("projectName", project.getName());
			    	
			    	logger.debug(myMainSettings.getInfoText() + myMainSettings.getInfoUrl() + myMainSettings.getProxySettingsAsString());
			    	
			    	params.put("msteamsNotificationCount", projSettings.getMsTeamsNotificationsCount());
			    	
			    	if (projSettings.getMsTeamsNotificationsCount() == 0){
			    		params.put(NO_SLACK_NOTIFICATIONS, "true");
			    		params.put(SLACK_NOTIFICATIONS, FALSE);
			    		params.put(PROJECT_SLACK_NOTIFICATION_AS_JSON, ProjectMsTeamsNotificationsBeanJsonSerialiser.serialise(ProjectMsTeamsNotificationsBean.build(projSettings, project, myMainSettings)));
			    	} else {
			    		params.put(NO_SLACK_NOTIFICATIONS, FALSE);
			    		params.put(SLACK_NOTIFICATIONS, "true");
			    		params.put("msTeamsNotificationList", projSettings.getMsTeamsNotificationsAsList());
			    		params.put("msteamsNotificationsDisabled", !projSettings.isEnabled());
			    		params.put("msteamsNotificationsEnabledAsChecked", projSettings.isEnabledAsChecked());
			    		params.put(PROJECT_SLACK_NOTIFICATION_AS_JSON, ProjectMsTeamsNotificationsBeanJsonSerialiser.serialise(ProjectMsTeamsNotificationsBean.build(projSettings, project, myMainSettings)));

			    	}
		    	} else {
		    		params.put("haveProject", FALSE);
		    		params.put(ERROR_REASON, "The project requested does not appear to be valid.");
		    	}
        	} else if (request.getParameter("buildTypeId") != null){
        		SBuildType sBuildType = TeamCityIdResolver.findBuildTypeById(this.myServer.getProjectManager(), request.getParameter("buildTypeId"));
        		if (sBuildType != null){
		        	SProject project = sBuildType.getProject();
		        	if (project != null){
		        		
				    	MsTeamsNotificationProjectSettings projSettings = (MsTeamsNotificationProjectSettings)
				    			mySettings.getSettings(project.getProjectId(), SLACK_NOTIFICATIONS);
				    	
				    	SUser myUser = SessionUser.getUser(request);
				        params.put("hasPermission", myUser.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT));
				    	
				    	List<MsTeamsNotificationConfig> configs = projSettings.getBuildMsTeamsNotificationsAsList(sBuildType);
				    	params.put("msTeamsNotificationList", configs);
				    	params.put("msteamsNotificationsDisabled", !projSettings.isEnabled());
				    	params.put(PROJECT_ID, project.getProjectId());
				    	params.put("haveProject", "true");
				    	params.put("projectName", project.getName());
				    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
				    	params.put("haveBuild", "true");
				    	params.put("buildName", sBuildType.getName());
				    	params.put("buildExternalId", TeamCityIdResolver.getExternalBuildId(sBuildType));
				    	params.put("buildTypeList", project.getBuildTypes());
			    		params.put(NO_SLACK_NOTIFICATIONS, configs.isEmpty());
			    		params.put(SLACK_NOTIFICATIONS, !configs.isEmpty());
				    	
			    		params.put(PROJECT_SLACK_NOTIFICATION_AS_JSON, ProjectMsTeamsNotificationsBeanJsonSerialiser.serialise(ProjectMsTeamsNotificationsBean.build(projSettings, sBuildType, project, myMainSettings)));
		        	}
        		} else {
		    		params.put("haveProject", FALSE);
		    		params.put(ERROR_REASON, "The build requested does not appear to be valid.");
        		}
	        } else {
	        	params.put("haveProject", FALSE);
	        	params.put(ERROR_REASON, "No project specified.");
	        }

	        return new ModelAndView(myPluginDescriptor.getPluginResourcesPath() + "MsTeamsNotification/index.jsp", params);
	    }
}
