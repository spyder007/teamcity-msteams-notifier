<%@ include file="/include.jsp" %>
	<div>
	
	<c:forEach items="${projectAndParents}" var="project"> 
		<h3 class="title">MsTeams notifications configured for ${project.project.fullName}</h3>
		<c:if test="${project.projectMsTeamsnotificationCount == 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<p>There are no MsTeams notifications configured for this project.</p>
				<a href="./msteamsnotifications/index.html?projectId=${project.externalProjectId}">Add project MsTeams notifications</a>.
				</div>
		</c:if>
		<c:if test="${project.projectMsTeamsnotificationCount > 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<c:if test="${not project.msteamsNotificationProjectSettings.isEnabled()}" >
					<div><strong>WARNING: MsTeams notification processing is currently disabled for this project</strong></div>
				</c:if>
				<p>There are <strong>${project.projectMsTeamsnotificationCount}</strong> MsTeams notifications configured for all builds in this project.
					<a href="./msteamsnotifications/index.html?projectId=${project.externalProjectId}">Edit project MsTeams notifications</a>.</p>
				<table class="testList dark borderBottom">
					<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
					<tbody>
					<c:forEach items="${project.projectMsTeamsnotifications}" var="hook">
						<tr><td>MS Teams</td><td><c:out value="${hook.enabledListAsString}" /></td></tr>
					</c:forEach>
					</tbody>
				</table>
				</div>
		</c:if>

			<c:forEach items="${project.buildMsTeamsnotifications}" var="config">

				<div style='margin-top: 2.5em;'><h3 class="title">MsTeams notifications configured for ${projectName} &gt; ${config.buildName}</h3>
				
				<c:if test="${config.hasNoBuildMsTeamsNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are no MsTeams notifications configured for this specific build.</p>
						<a href="./msteamsnotifications/index.html?buildTypeId=${config.buildExternalId}">Add build MsTeams notifications</a>.
						</div>
					</div>
				</c:if>
				<c:if test="${config.hasBuildMsTeamsNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are <strong>${config.buildCount}</strong> MsTeams notifications for this specific build.
							<a href="./msteamsnotifications/index.html?buildTypeId=${config.buildExternalId}">Edit build MsTeams notifications</a>.</p>
						<table class="testList dark borderBottom">
							<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
							<tbody>
							<c:forEach items="${config.buildMsTeamsNotificationList}" var="hook">
								<tr><td>MS Teams</td><td><c:out value="${hook.enabledListAsString}" /></td></tr>
							</c:forEach>
							</tbody>
						</table>
						</div>
					</div>
				</c:if>
				
			</c:forEach>
			
		</c:forEach>
		</div>