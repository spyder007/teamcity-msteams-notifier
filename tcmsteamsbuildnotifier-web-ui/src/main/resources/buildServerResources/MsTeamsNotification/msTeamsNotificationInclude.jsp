		<script>
        var ProjectBuilds = ${projectMsTeamsNotificationsAsJson};
        </script>

	    <!-- <p><label for="msTeamsNotificationEnabled" style="width:30em;"><input id="msTeamsNotificationEnabled" type="checkbox" ${msteamsNotificationsEnabledAsChecked}/> Process MsTeamsNotifications for this project</label></p>-->
	    <br/>
	    <table id="msTeamsNotificationTable" class="settings">
	   		<thead>
		   		<tr style="background-color: rgb(245, 245, 245);">
					<th class="name">Build Events</th>
					<th class="value" style="width:20%;" colspan="3">Enabled Builds</th>
			</tr>
			</thead>
			<tbody>
				<tr id="viewRow_template" class="msTeamsNotificationRowTemplate">
					<td class="value highlight msTeamsNotificationRowItemEvents" style="width:15%;">Events</td>
					<td class="value highlight msTeamsNotificationRowItemBuilds" style="width:15%;">Builds</td>
					<td class="edit highlight msTeamsNotificationRowItemEdit"><a href="javascript://">edit</a></td>
					<td class="edit highlight msTeamsNotificationRowItemDelete"><a ref="javascript://">delete</a></td>
				</tr> 	
	
			<c:forEach items="${msTeamsNotificationList}" var="notification">

				<tr id="viewRow_${notification.uniqueKey}" class="msTeamsNotificationRow">
					<td class="value highlight" style="width:15%;" onclick="BS.EditMsTeamsNotificationDialog.showDialog('${notification.uniqueKey}','#hookPane');"><c:out value="${notification.enabledListAsString}" /></td>
					<td class="value highlight" style="width:15%;" onclick="BS.EditMsTeamsNotificationDialog.showDialog('${notification.uniqueKey}','#buildPane');"><c:out value="${notification.buildTypeCountAsFriendlyString}" /></td>
					<td class="edit highlight"><a onclick="BS.EditMsTeamsNotificationDialog.showDialog('${notification.uniqueKey}','#hookPane');" href="javascript://">edit</a></td>
					<td class="edit highlight"><a onclick="BS.MsTeamsNotificationForm.removeMsTeamsNotification('${notification.uniqueKey}','#hookPane');" href="javascript://">delete</a></td>
				</tr> 
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
		<c:choose>  
    		<c:when test="${haveBuild}"> 
					<td colspan="6" class="highlight newMsTeamsNotificationRow"><p onclick="BS.EditMsTeamsNotificationDialog.showDialog('new');" class="addNew">Click to create new MsTeamsNotification for this build</p></td>
         	</c:when>  
         	<c:otherwise>  
					<td colspan="6" class="highlight newMsTeamsNotificationRow"><p onclick="BS.EditMsTeamsNotificationDialog.showDialog('new');" class="addNew">Click to create new MsTeamsNotification for this project</p></td>
         	</c:otherwise>  
		</c:choose> 
				</tr>
			</tfoot>
		</table>
      <div id="editMsTeamsNotificationDialog" class="editParameterDialog modalDialog"  style="width:50em;">
        <div class="dialogHeader">
          <div class="closeWindow">
            <a title="Close dialog window" href="javascript://" showdiscardchangesmessage="false"
               onclick="BS.EditMsTeamsNotificationDialog.cancelDialog()">close</a>
          </div>
          <h3 id="msTeamsNotificationDialogTitle" class="dialogTitle"></h3>

        </div>

        <div class="modalDialogBody">
          <form id='MsTeamsNotificationForm' action="ajaxEdit.html?projectId=${projectId}"
                method="post" onsubmit="return BS.MsTeamsNotificationForm.saveMsTeamsNotification();">
            <div id='msTeamsNotificationFormContents'>
            
            		<div id="tab-container" class="tab-container">
								  <ul class='etabs'>
												   <li class='tab'><a href="#hookPane" class="active">General</a></li>
												   <li class='tab'><a href="#payloadPane" class="active">Content</a></li>
												   <li class='tab'><a href="#buildPane">Builds (<span id="selectedBuildCount">all</span>)</a></li>
								  </ul>
						 <div class='panel-container'>
									<div id='hookPane'>
											<table style="border:none;">
												
												<tr style="border:none;">
													<td>Webhook URL:</td>
													<td colspan=2><input id="msTeamsNotificationToken" name="token" type=text maxlength=512 style="margin: 0pt; padding: 0pt; width: 36em;"/></td>
												</tr>
												<tr>
													<td></td>
													<td colspan=2><span class="error" id="error_msTeamsNotificationToken" style="margin-left: 0.5em;"></span></td>
												</tr>
												<tr style="border:none">
                                                  <td></td>
                                                  <td colspan="2"><span class="smallNote" style="margin-left:0px;">Enter the Webhook URL from the "Incoming WebHooks" connector for the target channel.</span></td>
                                                </tr>
												<tr style="border:none">
                                                  <td></td>
                                                  <td colspan="2"><span class="smallNote" style="margin-left:0px;">Make sure you include the leading # if you are posting to a channel e.g. #my-channel</span></td>
                                                </tr>
												<tr style="border:none;">
													<td><label for="msTeamsNotificationsEnabled">Enabled:</label></td>
													<td style="padding-left:3px;" colspan=2><input id="msTeamsNotificationsEnabled" type=checkbox name="msTeamsNotificationsEnabled"/></td>
												</tr>
												<tr style="border:none;">
													<td>Trigger on Events:</td>
													<td style="padding-left:3px;"><label style='white-space:nowrap;'>
														<input onclick='selectBuildState();' class="buildState" id="buildStarted" name="BuildStarted"  type=checkbox />
														 Build Started</label>
													</td>
													<td><label style='white-space:nowrap;'>
														<input onclick='selectBuildState();' class="buildState" id="buildInterrupted" name="BuildInterrupted" type=checkbox />
														 Build Interrupted</label>
													</td>
												</tr>
												<tr style="border:none;"><td>&nbsp;</td>
													<td style="padding-left:3px;"><label style='white-space:nowrap;'>
														<input onclick='selectBuildState();' class="buildState" id="beforeBuildFinish" name="BeforeFinished" type=checkbox />
														 Build Almost Completed</label>
													</td>
													<td><label style='white-space:nowrap;'>
														<input onclick='selectBuildState();' class="buildState" id="responsibilityChanged" name="ResponsibilityChanged" type=checkbox />
														 Build Responsibility Changed</label>
													</td>
												</tr>
					
												<tr style="border:none;" class="onCompletion"><td style="vertical-align:text-top; padding-top:0.33em;">On Completion:</td>
													<td colspan=2 >
														<table style="padding:0; margin:0; left: 0px;"><tbody style="padding:0; margin:0; left: 0px;">
																<tr style="padding:0; margin:0; left: 0px;"><td style="padding:0; margin:0; left: 0px;"><label style='white-space:nowrap;'>
																	<input onclick='doExtraCompleted();' class="buildState" id="buildSuccessful" name="BuildSuccessful" type=checkbox />
																	 Trigger when build is Successful</label>
																	</td></tr>
																<tr class="onBuildFixed" style="padding:0; margin:0; left: 0px;"><td style="padding:0; margin:0; padding-left: 2em; left: 0px;"><label style='white-space:nowrap;'>
																	<input class="buildStateFixed" id="buildFixed" name="BuildFixed" type=checkbox />
																	 Only trigger when build changes from Failure to Success</label>
																	</td></tr>
																<tr style="padding:0; margin:0; left: 0px;"><td style="padding:0; margin:0; left: 0px;"><label style='white-space:nowrap;'>
																	<input onclick='doExtraCompleted();' class="buildState" id="buildFailed" name="BuildFailed" type=checkbox />
																	 Trigger when build Fails</label>
																	</td></tr>
																<tr class="onBuildFailed" style="padding:0; margin:0; left: 0px;"><td style="padding:0; margin:0; padding-left: 2em; left: 0px;"><label style='white-space:nowrap;'>
																	<input class="buildStateBroken" id="buildBroken" name="BuildBroken" type=checkbox />
																	 Only trigger when build changes from Success to Failure</label>
																	</td></tr>
														</tbody></table>
													</td>
												</tr>
												<tr style="border:none;">
													<td>Mention on first failure:</td>
													<td style="padding-left:3px;"><label style='white-space:nowrap;'>
														<input class="buildState" id="mentionChannelEnabled" name="mentionChannelEnabled" type=checkbox />
														 @channel</label>
													</td>
													<td><label style='white-space:nowrap;'>
														<input class="buildState" id="mentionMsTeamsUserEnabled" name="mentionMsTeamsUserEnabled" type=checkbox />
														 MsTeams User (if known)</label>
													</td>
												</tr>
					    					</table>     
					    					
					    			</div><!--hookPane -->
					    			<div id='payloadPane'>
                                        <table style="border:none;">
                                            <tr style="border:none;">
                                                <td><label for="msTeamsNotificationsEnabled">Customize contents:</label></td>
                                                <td style="padding-left:3px;" colspan=2><input id="customContentEnabled" type=checkbox name="customContentEnabled" onclick="toggleCustomContentEnabled();"/></td>
                                            </tr>
                                           <tr class="onCustomContentEnabled">
                                               <td>
                                                   <label for="showBuildAgent">Show build agent: </label>
                                               </td>
                                               <td>
                                                   <input type="checkbox" name="showBuildAgent" id="showBuildAgent" />
                                               </td>
                                               <td>
                                                   <span style="color: #888; font-size: 90%;">When checked, the name of the build agent will be shown in the notification.</span>
                                               </td>
                                           </tr>
                                           <tr class="onCustomContentEnabled">
                                               <td>
                                                   <label for="showElapsedBuildTime">Show elapsed build time: </label>
                                               </td>
                                               <td>
                                                   <input type="checkbox" name="showElapsedBuildTime" id="showElapsedBuildTime" />
                                               </td>
                                               <td>
                                                   <span style="color: #888; font-size: 90%;">When checked, the elapsed time taken to complete the build is displayed in the notification.</span>
                                               </td>
                                           </tr>
                                           <tr class="onCustomContentEnabled">
                                               <td>
                                                   <label for="showCommitters">Show committers: </label>
                                               </td>
                                               <td>
                                                   <input type="checkbox" name="showCommitters" id="showCommitters"/>
                                               </td>
                                               <td>
                                                   <span style="color: #888; font-size: 90%;">When checked, the committers responsible for the changes in the build will be displayed in the notification.</span>
                                               </td>
                                           </tr>
                                           <tr class="onCustomContentEnabled">
                                               <td>
                                                   <label for="showCommits">Show commits: </label>
                                               </td>
                                               <td>
                                                   <input type="checkbox" name="showCommits" id="showCommits" />
                                               </td>
                                               <td>
                                                   <span style="color: #888; font-size: 90%;">When checked, the commits, the username and the commit message for each change will be displayed in the notification.</span>
                                               </td>
                                           </tr>
                                           <tr class="onCustomContentEnabled">
                                               <td>
                                                   <label for="maxCommitsToDisplay">Max commits to display: <l:star /></label>
                                               </td>
                                               <td colspan="2">
                                                   <input type="text" name="maxCommitsToDisplay" id="maxCommitsToDisplay" />
                                               </td>
                                           </tr>
                                           <tr class="onCustomContentEnabled">
                                              <td>
                                                  <label for="showFailureReason">Show failure reason: </label>
                                              </td>
                                              <td>
                                                  <input type="checkbox" name="showFailureReason" id="showFailureReason" />
                                              </td>
                                              <td>
                                                  <span style="color: #888; font-size: 90%;">When checked, the reason for the build failure including failed tests is displayed.</span>
                                              </td>
                                          </tr>
                                           <tr class="onCustomContentEnabled">
                                              <td>
                                                  <label for="botName">Bot name: <l:star /></label>
                                              </td>
                                              <td colspan="2">
                                                  <input type="text" name="botName" id="botName" />
                                              </td>
                                          </tr>
                                          <tr class="onCustomContentEnabled">
                                                <td>
                                                    <label for="iconUrl">Icon Url: <l:star /></label>
                                                </td>
                                                <td colspan="2">
                                                    <input type="text" name="iconUrl" id="iconUrl" />
                                                </td>
                                            </tr>
                                        </table>

                                </div>
					    			
					    			<div id='buildPane'>
					    				<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input name="buildTypeAll" onclick="toggleAllBuildTypesSelected();" type=checkbox style="padding-right: 1em;" class="buildType_all"><strong>All Project Builds</strong></label></p>
					    				<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input id="buildTypeSubProjects" name="buildTypeSubProjects" onclick="updateSelectedBuildTypes();" type=checkbox style="padding-right: 1em;" class="buildType_subprojects"><strong>All Sub-Project Builds</strong></label></p>
					            		<div id='buildList' style="padding:0;">
						            	</div>
						            </div>
					    	</div><!-- panel-container  -->
					</div>    <!-- tab-container -->   
		            
		            <!--
		            <label class="editParameterLabel" for="parameterName">Name: <span class="mandatoryAsterix" title="Mandatory field">*</span></label>
					<input type="text" name="parameterName" id="parameterName" size="" maxlength="512" value="" class="textfield" style="margin:0; padding:0; width:22em;"   >
		
		            <span class="error" id="error_parameterName" style="margin-left: 5.5em;"></span>
		
		            <div class="clr" style="height:3px;"></div>
		            <label class="editParameterLabel" for="parameterValue">Value:</label>
					<input type="text" name="parameterValue" id="parameterValue" size="" maxlength="512" value="" class="textfield" style="margin:0; padding:0; width:22em;"   >
					-->
			</div> <!-- msteamsNotificationFormContents -->

            <div class="popupSaveButtonsBlock">
              <a href="javascript://" showdiscardchangesmessage="false" onclick="BS.EditMsTeamsNotificationDialog.cancelDialog()"
                 class="cancel">Cancel</a>
              <input class="submitButton" type="submit" value="Save"/>
			<img id="msteamsNotificationSaving" style="display: none; padding-top: 0.1em; float: right;" src="../img/ajax-loader.gif" width="16" height="16" alt="Please wait..." title="Please wait..."/>

              <br clear="all"/>
            </div>

            <input type="hidden" id="msTeamsNotificationId" name="msTeamsNotificationId" value=""/>
            <input type="hidden" id="submitAction" name="submitAction" value=""/>


          </form>
	    </div>
    </div>
          
