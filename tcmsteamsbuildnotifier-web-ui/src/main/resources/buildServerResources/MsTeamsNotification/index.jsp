<%@ include file="/include.jsp" %>
<c:set var="title" value="MsTeamsNotifications" scope="request"/>
<bs:page>

    <jsp:attribute name="head_include">
      <bs:linkCSS>
        /css/admin/adminMain.css
        /css/admin/projectConfig.css
        /css/forms.css
        /css/admin/vcsRootsTable.css
        
    /css/visibleProjects.css
    /css/addSidebar.css
    /css/settingsTable.css
    /css/profilePage.css
    /css/userRoles.css
    
    ${jspHome}MsTeamsNotification/css/styles.css
        
      </bs:linkCSS>
      <bs:linkScript>
        /js/bs/blocks.js
        /js/bs/blocksWithHeader.js
        /js/bs/forms.js
        /js/bs/modalDialog.js
        /js/bs/editBuildType.js
        /js/bs/editProject.js
        /js/bs/adminActions.js
      </bs:linkScript>

      <script type="text/javascript">
        BS.Navigation.items = [
		  {title: "Projects", url: '<c:url value="/overview.html"/>'},
		  <c:if test="${haveProject}"> 
		  	{title: "${projectName}", url: '<c:url value="/project.html?projectId=${projectExternalId}"/>'},
		  </c:if>
		  <c:if test="${haveBuild}"> 
		  	{title: "${buildName}", url: '<c:url value="/viewType.html?buildTypeId=${buildExternalId}"/>'},
		  </c:if>
          {title: "${title}", selected:true}
        ];
    
      </script>
    </jsp:attribute>
    
    
      
    <jsp:attribute name="body_include">
    <c:if test="${includeJquery}">
    	<script type=text/javascript src="..${jspHome}MsTeamsNotification/js/jquery-1.4.3.min.js"></script>
    </c:if>
	<script type=text/javascript src="..${jspHome}MsTeamsNotification/js/jquery.easytabs.min.js"></script>
    <script type=text/javascript>
		var jQueryMsTeamsnotification = jQuery.noConflict();
		var msTeamsnotificationDialogWidth = -1;
		jQueryMsTeamsnotification(document).ready( function() {
				jQueryMsTeamsnotification('#tab-container').easytabs({
					  animate: false,
					  updateHash: false
				});
		});

		function selectBuildState(){
			doExtraCompleted();
		}

		function doExtraCompleted(){
			if(jQueryMsTeamsnotification('#buildSuccessful').is(':checked')){
				jQueryMsTeamsnotification('.onBuildFixed').removeClass('onCompletionDisabled');
				jQueryMsTeamsnotification('tr.onBuildFixed td input').removeAttr('disabled');
			} else {
				jQueryMsTeamsnotification('.onBuildFixed').addClass('onCompletionDisabled');
				jQueryMsTeamsnotification('tr.onBuildFixed td input').attr('disabled', 'disabled');
			} 
			if(jQueryMsTeamsnotification('#buildFailed').is(':checked')){
				jQueryMsTeamsnotification('.onBuildFailed').removeClass('onCompletionDisabled');
				jQueryMsTeamsnotification('tr.onBuildFailed td input').removeAttr('disabled');
			} else {
				jQueryMsTeamsnotification('.onBuildFailed').addClass('onCompletionDisabled');
				jQueryMsTeamsnotification('tr.onBuildFailed td input').attr('disabled', 'disabled');
			}
		}

		function toggleCustomContentEnabled(){
            if(jQueryMsTeamsnotification('#customContentEnabled').is(':checked')){
                jQueryMsTeamsnotification('.onCustomContentEnabled').removeClass('onCompletionDisabled');
                jQueryMsTeamsnotification('tr.onCustomContentEnabled td input').removeAttr('disabled');
            } else {
                jQueryMsTeamsnotification('.onCustomContentEnabled').addClass('onCompletionDisabled');
                jQueryMsTeamsnotification('tr.onCustomContentEnabled td input').attr('disabled', 'disabled');
            }
		}
		
		function toggleAllBuildTypesSelected(){
			jQueryMsTeamsnotification.each(jQueryMsTeamsnotification('.buildType_single'), function(){
				jQueryMsTeamsnotification(this).attr('checked', jQueryMsTeamsnotification('input.buildType_all').is(':checked'))
			});
			updateSelectedBuildTypes();
		}
		
		function updateSelectedBuildTypes(){
			var subText = "";
		    if(jQueryMsTeamsnotification('#buildTypeSubProjects').is(':checked')){
		    	subText = " &amp; sub-projects";
		    }
		
			if(jQueryMsTeamsnotification('#msTeamsNotificationFormContents input.buildType_single:checked').length == jQueryMsTeamsnotification('#msTeamsNotificationFormContents input.buildType_single').length){
				jQueryMsTeamsnotification('input.buildType_all').attr('checked', true);
				jQueryMsTeamsnotification('span#selectedBuildCount').html("all" + subText);
			} else {
				jQueryMsTeamsnotification('input.buildType_all').attr('checked', false);
				jQueryMsTeamsnotification('span#selectedBuildCount').html(jQueryMsTeamsnotification('#msTeamsNotificationFormContents input.buildType_single:checked').length + subText);
			}

		}
		
		function populateMsTeamsNotificationDialog(id){
			jQueryMsTeamsnotification('#buildList').empty();
			jQueryMsTeamsnotification.each(ProjectBuilds.projectMsTeamsnotificationConfig.msTeamsNotificationList, function(thing, config){
				if (id === config[0]){
					var msTeamsnotification = config[1];
				
					jQueryMsTeamsnotification('#msTeamsNotificationId').val(msTeamsnotification.uniqueKey);
					jQueryMsTeamsnotification('#msTeamsNotificationToken').val(msTeamsnotification.token);
				    jQueryMsTeamsnotification('#msTeamsNotificationsEnabled').attr('checked', msTeamsnotification.enabled);
				    jQueryMsTeamsnotification.each(msTeamsnotification.states, function(name, value){
				    	jQueryMsTeamsnotification('#' + value.buildStateName).attr('checked', value.enabled);
				    });
				    
                    jQueryMsTeamsnotification('#buildTypeSubProjects').attr('checked', msTeamsnotification.subProjectsEnabled);
					jQueryMsTeamsnotification.each(msTeamsnotification.builds, function(){
						 if (this.enabled){
					 	 	jQueryMsTeamsnotification('#buildList').append('<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input checked onclick="updateSelectedBuildTypes();" type=checkbox style="padding-right: 1em;" name="buildTypeId" value="' + this.buildTypeId + '"class="buildType_single">' + this.buildTypeName + '</label></p>');
						 } else {
						 	 jQueryMsTeamsnotification('#buildList').append('<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input onclick="updateSelectedBuildTypes();" type=checkbox style="padding-right: 1em;" name="buildTypeId" value="' + this.buildTypeId + '"class="buildType_single">' + this.buildTypeName + '</label></p>');
						 }
					});
					jQueryMsTeamsnotification('#mentionChannelEnabled').attr('checked', msTeamsnotification.mentionChannelEnabled);
					jQueryMsTeamsnotification('#mentionMsTeamsUserEnabled').attr('checked', msTeamsnotification.mentionMsTeamsUserEnabled);
					jQueryMsTeamsnotification('#maxCommitsToDisplay').val(msTeamsnotification.maxCommitsToDisplay);
					jQueryMsTeamsnotification('#customContentEnabled').attr('checked', msTeamsnotification.customContentEnabled);
					jQueryMsTeamsnotification('#showBuildAgent').attr('checked', msTeamsnotification.showBuildAgent);
					jQueryMsTeamsnotification('#showCommits').attr('checked', msTeamsnotification.showCommits);
					jQueryMsTeamsnotification('#showCommitters').attr('checked', msTeamsnotification.showCommitters);
					jQueryMsTeamsnotification('#showElapsedBuildTime').attr('checked', msTeamsnotification.showElapsedBuildTime);
					jQueryMsTeamsnotification('#botName').val(msTeamsnotification.botName);
					jQueryMsTeamsnotification('#iconUrl').val(msTeamsnotification.iconUrl)
					jQueryMsTeamsnotification('#showFailureReason').attr('checked', msTeamsnotification.showFailureReason);
				}
			});
			updateSelectedBuildTypes();
		}
		
		function addMsTeamsNotificationsFromJsonCallback(){
			jQueryMsTeamsnotification.each(ProjectBuilds.projectMsTeamsnotificationConfig.msTeamsNotificationList, function(thing, config){
				if ('new' !== config[0]){
					var msTeamsnotification = config[1];
					jQueryMsTeamsnotification('.msTeamsNotificationRowTemplate')
									.clone()
									.removeAttr("id")
									.attr("id", "viewRow_" + msTeamsnotification.uniqueKey)
									.removeClass('msTeamsNotificationRowTemplate')
									.addClass('msTeamsNotificationRow')
									.appendTo('#msTeamsNotificationTable > tbody');
					jQueryMsTeamsnotification("#viewRow_" + msTeamsnotification.uniqueKey + " > td.msTeamsNotificationRowItemEvents").html(msTeamsnotification.enabledEventsListForWeb).click(function(){BS.EditMsTeamsNotificationDialog.showDialog(msTeamsnotification.uniqueKey,'#hookPane');});
					jQueryMsTeamsnotification("#viewRow_" + msTeamsnotification.uniqueKey + " > td.msTeamsNotificationRowItemBuilds").html(msTeamsnotification.enabledBuildsListForWeb).click(function(){BS.EditMsTeamsNotificationDialog.showDialog(msTeamsnotification.uniqueKey, '#buildPane');});
					jQueryMsTeamsnotification("#viewRow_" + msTeamsnotification.uniqueKey + " > td.msTeamsNotificationRowItemEdit > a").click(function(){BS.EditMsTeamsNotificationDialog.showDialog(msTeamsnotification.uniqueKey,'#hookPane');});
					jQueryMsTeamsnotification("#viewRow_" + msTeamsnotification.uniqueKey + " > td.msTeamsNotificationRowItemDelete > a").click(function(){BS.MsTeamsNotificationForm.removeMsTeamsNotification(msTeamsnotification.uniqueKey,'#hookPane');});
					
				}
			});
		}

		BS.EditMsTeamsNotificationDialog = OO.extend(BS.AbstractModalDialog, {
			  getContainer : function() {
			    return $('editMsTeamsNotificationDialog');
			  },

			  showDialog : function(id, tab) {
				BS.MsTeamsNotificationForm.clearErrors();
			    
			    populateMsTeamsNotificationDialog(id);
			    doExtraCompleted();
			    toggleCustomContentEnabled();
			    
			    var title = id == "new" ? "Add New" : "Edit";
			    title += " MsTeamsNotification";

			    $('msTeamsNotificationDialogTitle').innerHTML = title;


			    if (msTeamsnotificationDialogWidth < 0){
			    	msTeamsnotificationDialogWidth = jQueryMsTeamsnotification('#editMsTeamsNotificationDialog').innerWidth();
			    } else {
			    	jQueryMsTeamsnotification('#editMsTeamsNotificationDialog').innerWidth(msTeamsnotificationDialogWidth);
			    }
			    
			    this.showCentered();
			    jQueryMsTeamsnotification('#buildPane').innerHeight(jQueryMsTeamsnotification('#hookPane').innerHeight());
				jQueryMsTeamsnotification('#tab-container').easytabs('select', tab);
			  },

			  cancelDialog : function() {
			    this.close();
			  }
			});

		BS.BaseSaveMsTeamsNotificationListener = OO.extend(BS.SaveConfigurationListener, {
			  onBeginSave : function(form) {
			    form.clearErrors();
			    form.hideSuccessMessages();
			    form.disable();
			    form.setSaving(true);
			  }
			});

		BS.MsTeamsNotificationForm = OO.extend(BS.AbstractWebForm, {
			  setSaving : function(saving) {
			    if (saving) {
			      BS.Util.show('msTeamsNotificationSaving');
			    } else {
			      BS.Util.hide('msTeamsNotificationSaving');
			    }
			  },

			  formElement : function() {
			    return $('MsTeamsNotificationForm');
			  },

			  saveMsTeamsNotification : function() {
			    this.formElement().submitAction.value = 'updateMsTeamsNotification';
			    var that = this;

			    BS.FormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener,
			 	{

			      onCompleteSave : function(form, responseXML, err) {
			    	BS.ErrorsAwareListener.onCompleteSave(form, responseXML, err);
			        form.enable();
			        if (!err) {
			          $('systemParams').updateContainer();
			          BS.EditMsTeamsNotificationDialog.close();
			        }
			      }
			    }));

			    return false;
			  },

			  removeMsTeamsNotification : function(paramId) {
			    var that = this;

			    if (!confirm("Are you sure you want to delete this MsTeamsNotification?")) return;

			    var url = this.formElement().action + "&submitAction=removeMsTeamsNotification&removedMsTeamsNotificationId=" + paramId;

			    BS.ajaxRequest(url, {
			      onComplete: function() {
			        $('systemParams').updateContainer();
			        BS.EditMsTeamsNotificationDialog.close();
			      }
			    });
			  }
			});
	</script>
    <div class="editBuildPageGeneral" style="background-color:white; float:left; margin:0; padding:0; width:70%;">
    
        <c:choose>  
    		<c:when test="${haveBuild}"> 
			    <h2 class="noBorder">MsTeamsNotifications applicable to build ${buildName}</h2>
			    To edit all MS Teams notifications for builds in the project <a href="index.html?projectId=${projectExternalId}">edit Project msteamsnotifications</a>.
         	</c:when>  
         	<c:otherwise>  
			    <h2 class="noBorder">MsTeams notifications configured for project ${projectName}</h2>
         	</c:otherwise>  
		</c:choose>  


  		<div id="messageArea"></div>
	    <div id="systemParams"><!--  begin systemParams div -->

		<c:choose>
			<c:when test="${not haveProject}">
				<strong>${errorReason}</strong><br/>Please access this page via the MsTeamsNotifications tab on a project or build overview page.
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${hasPermission}">
					<%@ include file="msTeamsNotificationInclude.jsp" %>
					</c:when>
					<c:otherwise>
						<strong>You must have Project Administrator permission to edit MsTeamsNotifications</strong>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>

        </div><!--  end systemParams div -->
      </div>
    <div id=sidebarAdmin>
      <div class=configurationSection>
      	<h2>MsTeams Notification Information</h2>
          <p>MsTeams notifications appear in your msTeams channels when builds are started and/or completed. </p>

			<c:choose>
				<c:when test="${ShowFurtherReading == 'ALL'}">
				          <p>Further Reading:
				          <ul>${moreInfoText}
				          	<li><a href="https://github.com/spyder007/teamcity-msteams-notifier">Teamcity MSTeams Notifier plugin</a></li>
				          </ul>	
				</c:when>
		
				<c:when test="${ShowFurtherReading == 'DEFAULT'}">
				          <p>Further Reading:
				          <li><a href="https://github.com/spyder007/teamcity-msteams-notifier">Teamcity MSTeams Notifier plugin</a></li>
				          </ul>	
				</c:when>
		
				<c:when test="${ShowFurtherReading == 'SINGLE'}">
				          <p>Further Reading:
				          <ul>${moreInfoText}</ul>
				</c:when>
			</c:choose>

      </div>
    </div>
    <script type=text/javascript>
	        $('systemParams').updateContainer = function() {
        <c:choose>  
    		<c:when test="${haveBuild}"> 
	          	jQueryMsTeamsnotification.get("settingsList.html?buildTypeId=${buildExternalId}", function(data) {
         	</c:when>  
         	<c:otherwise>  
	          	jQueryMsTeamsnotification.get("settingsList.html?projectId=${projectId}", function(data) {
         	</c:otherwise>  
		</c:choose>  	        
	          		ProjectBuilds = data;
	          		jQueryMsTeamsnotification('.msTeamsNotificationRow').remove();
	          		addMsTeamsNotificationsFromJsonCallback();
				});
	        }

	</script>
    </jsp:attribute>
</bs:page>
