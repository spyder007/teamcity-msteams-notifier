<%@ include file="/include.jsp"%>

<c:url value="/msteamsNotifier/adminSettings.html" var="actionUrl" />
<bs:linkCSS dynamic="${true}">
  ${jspHome}MsTeamsNotification/css/adminStyles.css
</bs:linkCSS>



<div id="settingsContainer">
  <form action="${actionUrl}" id="msTeamsNotifierAdminForm" method="post" onsubmit="return MsTeamsNotifierAdmin.save()" >
    <div class="editNotificatorSettingsPage">
          <div>
             <span class="msteamsNotifierVersionInfo">Version: <c:out value='${pluginVersion}'/>&nbsp;<a href="https://github.com/spyder007/tcMsTeamsBuildNotifier" class="helpIcon" style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></span>
          </div>
          <c:choose>
            <c:when test="${disabled}">
              <div class="pauseNote" style="margin-bottom: 1em;">
                The notifier is <strong>disabled</strong>. All msteams notifications are suspended&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="enable-btn">Enable</a>
              </div>
            </c:when>
            <c:otherwise>
              <div style="margin-left: 0.6em;">
                The notifier is <strong>enabled</strong>&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="disable-btn">Disable</a>
              </div>
            </c:otherwise>
          </c:choose>

          <bs:messages key="message" />
          <br/>
          <div style="msteams-config-errors" id="msTeamsNotificationErrors">
          </div>

          <table class="runnerFormTable">
                <tr class="groupingTitle">
                        <td colspan="2">General Configuration&nbsp;<a href="https://github.com/spyder007/tcMsTeamsBuildNotifier" class="helpIcon" style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></td>
                </tr>
                <tr>
                    <th>
                        <label for="token">Webhook Url: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="token" value="${token}" style="width: 300px;" />
                        <span class="smallNote">Enter the Webhook URL from the "Incoming WebHooks" connector for the target channel.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="botName">Bot name: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="botName" value="${botName}" style="width: 300px;" />
                        <span class="smallNote">The name that will be displayed by your bot messages. Mostly you can leave this as <b>TeamCity</b></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="iconUrl">Icon url: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="iconUrl" value="${iconUrl}" style="width: 300px;" />
                        <span class="smallNote">The url of the icon to appear beside your bot. You can leave this as the default or customize it to a png file.</span>
                        <span class="smallNote">The default is <b>https://raw.githubusercontent.com/spyder007/teamcity-msteams-notifier/master/docs/TeamCity72x72.png</b>
                    </td>
                </tr>
                <tr class="groupingTitle">
                        <td colspan="2">Notification Features</td>
                </tr>
                <tr>
                    <th>
                        <label for="showBuildAgent">Show build agent: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showBuildAgent" checked="${showBuildAgent}" value="${showBuildAgent}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the name of the build agent will be shown in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showElapsedBuildTime">Show elapsed build time: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showElapsedBuildTime" checked="${showElapsedBuildTime}" value="${showElapsedBuildTime}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the elapsed time taken to complete the build is displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showCommitters">Show committers: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showCommitters" checked="${showCommitters}" value="${showCommitters}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the committers responsible for the changes in the build will be displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showCommits">Show commits: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showCommits" checked="${showCommits}" value="${showCommits}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the commits, the username and the commit message for each change will be displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="maxCommitsToDisplay">Max commits to display: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="maxCommitsToDisplay" value="${maxCommitsToDisplay}" style="width: 70px;" />
                        <span class="smallNote">The maximum number of commits to display.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showFailureReason">Show failure reason: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showFailureReason" checked="${showFailureReason}" value="${showFailureReason}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the reason for the build failure, including failed tests are displayed.</span>
                    </td>
                </tr>
                <tr class="groupingTitle">
                        <td colspan="2">Proxy Configuration</td>
                </tr>
<tr>
                    <th>
                        <label for="proxyHost">Proxy host:</label>
                    </th>
                    <td>
                        <forms:textField name="proxyHost" value="${proxyHost}" style="width: 300px;" />
                        <span class="smallNote">The hostname of the proxy server. e.g. myproxy.mycompany.com</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="proxyPort">Proxy port:</label>
                    </th>
                    <td>
                        <forms:textField name="proxyPort" value="${proxyPort}" style="width: 70px;" />
                        <span class="smallNote">The port of the proxy server.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="botName">Proxy username:</label>
                    </th>
                    <td>
                        <forms:textField name="proxyUser" value="${proxyUser}" style="width: 300px;" />
                        <span class="smallNote">An optional username to use for proxy authentication.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="proxyPassword">Proxy password:</label>
                    </th>
                    <td>
                        <forms:passwordField name="proxyPassword" encryptedPassword="${encryptedProxyPassword}" style="width: 300px;" />
                        <span class="smallNote">An optional password to use for proxy authentication.</span>
                    </td>
                </tr>
           </table>
            <div class="saveButtonsBlock">
                <input type="hidden" id="publicKey" name="publicKey" value="<c:out value='${hexEncodedPublicKey}'/>"/>
                <forms:submit label="Save" />
                <forms:submit id="testConnection" type="button" label="Send test notification" onclick="return MsTeamsNotifierAdmin.sendTestNotification()"/>
                <forms:saving />
            </div>
    </div>
  </form>

  <bs:linkScript>
    ${jspHome}MsTeamsNotification/js/msTeamsNotifierAdmin.js
  </bs:linkScript>
</div>

<script type="text/javascript">
	(function($) {
		var sendAction = function(enable) {
			$.post("${actionUrl}?action=" + (enable ? 'enable' : 'disable'),
					function() {
						BS.reload(true);
					});
			return false;
		};
		$("#enable-btn").click(function() {
			return sendAction(true);
		});
		$("#disable-btn")
            .click(
                function() {
                    if (!confirm("MsTeams notifications will not be sent until enabled. Disable the notifier?"))
                        return false;
                    return sendAction(false);
                });
	})(jQuery);
</script>