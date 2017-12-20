<%@ include file="/include.jsp" %>
	<div><h3 class="title">MsTeams notifications configured for ${projectName}</h3>
	
<c:if test="${noProjectMsTeamsNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no MsTeams notifications configured for this project.</p>
		<a href="./msteamsnotifications/index.html?projectId=${projectExternalId}">Add project MsTeamsNotifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${projectMsTeamsNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<c:if test="${projectMsTeamsNotificationsDisabled}" >
			<div><strong>WARNING: MsTeams notification processing is currently disabled for this project</strong></div>
		</c:if>
		<p>There are <strong>${projectMsTeamsNotificationCount}</strong> MsTeams notifications configured for all builds in this project.
			<a href="./msteamsnotifications/index.html?projectId=${projectExternalId}">Edit project MsTeams notifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${projectMsTeamsNotificationList}" var="notification">
				<tr><td>MS Teams</td><td><c:out value="${notification.enabledListAsString}" /></td></tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>

<div style='margin-top: 2.5em;'><h3 class="title">MsTeams notifications configured for ${projectName} &gt; ${buildName}</h3>

<c:if test="${noBuildMsTeamsNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no MsTeams notifications configured for this specific build.</p>
		<a href="./msteamsnotifications/index.html?buildTypeId=${buildExternalId}">Add build MsTeams notifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${buildMsTeamsNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are <strong>${buildMsTeamsNotificationCount}</strong> MsTeams notifications for this specific build.
			<a href="./msteamsnotifications/index.html?buildTypeId=${buildExternalId}">Edit build MsTeams notifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${buildMsTeamsNotificationList}" var="notification">
				<tr><td>MS Teams</td><td><c:out value="${channel.enabledListAsString}" /></td></tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>