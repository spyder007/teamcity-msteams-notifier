package msteamsnotifications;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import jetbrains.buildServer.util.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpHost;
import msteamsnotifications.teamcity.BuildState;
import msteamsnotifications.teamcity.Loggers;
import msteamsnotifications.teamcity.payload.content.Commit;
import msteamsnotifications.teamcity.payload.content.PostMessageResponse;
import msteamsnotifications.teamcity.payload.content.MsTeamsNotificationPayloadContent;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;



public class MsTeamsNotificationImpl implements MsTeamsNotification {
	
	private static final String UTF8 = "UTF-8";

    private String proxyHost;
    private Integer proxyPort = 0;
    private String proxyUsername;
    private String proxyPassword;
    private String token;
    private String iconUrl;
    private String content;
    private MsTeamsNotificationPayloadContent payload;
    private Integer resultCode;
    private HttpClient client;
    private String filename = "";
    private Boolean enabled = false;
    private Boolean errored = false;
    private String errorReason = "";
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private BuildState states;
    private String botName;
    private final static String CONTENT_TYPE = "application/json";
    private PostMessageResponse response;
    private Boolean showBuildAgent;
    private Boolean showElapsedBuildTime;
    private boolean showCommits;
    private boolean showCommitters;
    private int maxCommitsToDisplay;
    private boolean mentionChannelEnabled;
    private boolean mentionMsTeamsUserEnabled;
    private boolean showFailureReason;
	
/*	This is a bit mask of states that should trigger a MsTeamsNotification.
 *  All ones (11111111) means that all states will trigger the msteamsnotifications
 *  We'll set that as the default, and then override if we get a more specific bit mask. */
    //private Integer EventListBitMask = BuildState.ALL_ENABLED;
    //private Integer EventListBitMask = Integer.parseInt("0",2);


    public MsTeamsNotificationImpl() {
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
    }

    public MsTeamsNotificationImpl(String proxyHost, String proxyPort) {
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        if (proxyPort.length() != 0) {
            try {
                this.proxyPort = Integer.parseInt(proxyPort);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        this.setProxy(proxyHost, this.proxyPort, null);
    }

    public MsTeamsNotificationImpl(String proxyHost, Integer proxyPort) {
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        this.setProxy(proxyHost, proxyPort, null);
    }

    public MsTeamsNotificationImpl(HttpClient httpClient) {
        this.client = httpClient;
    }

    public MsTeamsNotificationImpl(MsTeamsNotificationProxyConfig proxyConfig) {
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        setProxy(proxyConfig);
    }

    public void setProxy(MsTeamsNotificationProxyConfig proxyConfig) {
        if ((proxyConfig != null) && (proxyConfig.getProxyHost() != null) && (proxyConfig.getProxyPort() != null)) {
            this.setProxy(proxyConfig.getProxyHost(), proxyConfig.getProxyPort(), proxyConfig.getCreds());
        }
    }

    public void setProxy(String proxyHost, Integer proxyPort, Credentials credentials) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        
		if (this.proxyHost.length() > 0 && !this.proxyPort.equals(0)) {
            HttpClientBuilder clientBuilder = HttpClients.custom()
                .useSystemProperties()
                .setProxy(new HttpHost(proxyHost, proxyPort, "http"));
                
            if (credentials != null) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);
                clientBuilder.setDefaultCredentialsProvider(credsProvider);
                Loggers.SERVER.debug("MsTeamsNotification ::using proxy credentials " + credentials.getUserPrincipal().getName());
            }
            
            this.client = clientBuilder.build();
		}
    }

    public void post() throws IOException {
        postViaWebHook();
    }

    private void postViaWebHook() throws IOException {
        if ((this.enabled) && (!this.errored)) {

            String url = "";
            if(this.token != null && this.token.startsWith("http")){
                url = this.token;
            }

            Loggers.SERVER.info("MsTeamsNotificationListener :: Preparing message for URL " + url);

            WebHookPayload requestBody = new WebHookPayload();

            HttpPost httppost = new HttpPost(url);

            if (this.payload != null) {
                requestBody.setText(payload.getBuildDescriptionWithLinkSyntax());
                requestBody.themeColor = this.payload.getColor();
                requestBody.setMessageSections(getAttachments());
            }

            String bodyParam = requestBody.toJson();

            Loggers.SERVER.info("MsTeamsNotificationListener :: Body message will be " + bodyParam);

            httppost.setEntity(new StringEntity(bodyParam));
            httppost.setHeader("Content-Type", CONTENT_TYPE);

            try {
                HttpResponse response = client.execute(httppost);
                this.resultCode = response.getStatusLine().getStatusCode();

                PostMessageResponse resp = new PostMessageResponse();

                if (this.resultCode != HttpStatus.SC_OK) {
                    String error = EntityUtils.toString(response.getEntity());
                    resp.setOk(error == "ok");
                    resp.setError(error);
                }
                else{
                    resp.setOk(true);
                }

                this.response = resp;
          //      this.content = EntityUtils.toString(response.getEntity());

            } finally {
                httppost.releaseConnection();
            }
        }
    }

    private List<MessageSection> getAttachments() {
        List<MessageSection> messageSections = new ArrayList<MessageSection>();
        MessageSection generalMessageSection = new MessageSection(this.payload.getBuildName(), null);
        generalMessageSection.setActivityImage(this.iconUrl);

        generalMessageSection.addFact("Branch", this.payload.getBranchDisplayName());
        generalMessageSection.addFact("Triggered by", this.payload.getTriggeredByText());

        if(showBuildAgent == null || showBuildAgent){
            generalMessageSection.addFact("Agent", this.payload.getAgentName());
        }
        if(this.payload.getIsComplete() && (showElapsedBuildTime == null || showElapsedBuildTime)){
            generalMessageSection.addFact("Elapsed", formatTime(this.payload.getElapsedTime()));
        }

        if(showFailureReason && this.payload.getBuildResult() == MsTeamsNotificationPayloadContent.BUILD_STATUS_FAILURE){
            if(this.payload.getFailedBuildMessages().size() > 0) {
                generalMessageSection.addFact("Reason", StringUtil.join(", ", payload.getFailedBuildMessages()));
            }
            if(this.payload.getFailedTestNames().size() > 0){
                ArrayList<String> failedTestNames = payload.getFailedTestNames();
                String truncated = "";
                if(failedTestNames.size() > 10){
                    failedTestNames = new ArrayList<String>( failedTestNames.subList(0, 9));
                    truncated = " (+ " + Integer.toString(payload.getFailedBuildMessages().size() - 10) + " more)";
                }
                payload.getFailedTestNames().size();
                generalMessageSection.addFact("Failed Tests", StringUtil.join(", ", failedTestNames) + truncated);
            }
        }

        messageSections.add(generalMessageSection);

        StringBuilder sbCommits = new StringBuilder();

        List<Commit> commits = this.payload.getCommits();

        List<Commit> commitsToDisplay = new ArrayList<Commit>(commits);

        if(showCommits || showCommitters) {

            String sectionText = null;

            if(showCommitters) {
                Set<String> committers = new HashSet<String>();
                for (Commit commit : commits) {
                    committers.add(commit.getUserName());
                }

                String committersString = StringUtil.join(", ", committers);

                if (!commits.isEmpty()) {
                    sectionText = String.format("Changes By %s", committersString);
                }
            }

            MessageSection commitSection = new MessageSection("Commits", sectionText);

            if (showCommits) {

                boolean truncated = false;
                int totalCommits = commitsToDisplay.size();
                if (commitsToDisplay.size() > maxCommitsToDisplay) {
                    commitsToDisplay = commitsToDisplay.subList(0, maxCommitsToDisplay > commitsToDisplay.size() ? commitsToDisplay.size() : 5);
                    truncated = true;
                }

                for (Commit commit : commitsToDisplay) {
                    String revision = commit.getRevision();
                    revision = revision == null ? "" : revision;
                    commitSection.addFact(revision.substring(0, Math.min(revision.length(), 10)), String.format("%s :: %s", commit.getUserName(), commit.getDescription()));
                }

                if (truncated) {
                    commitSection.addFact("More", String.format("(+ %d)\n", totalCommits - 5));
                }
            }
            messageSections.add(commitSection);
        }

        List<String> msteamsUsers = new ArrayList<String>();

        for(Commit commit : commits){
            if(commit.hasMsTeamsUsername()){
                msteamsUsers.add("@" + commit.getMsTeamsUserName());
            }
        }
//        HashSet<String> tempHash = new HashSet<String>(msteamsUsers);
//        msteamsUsers = new ArrayList<String>(tempHash);
//


//        // Mention the channel and/or the MsTeams Username of any committers if known
//        if(payload.getIsFirstFailedBuild() && (mentionChannelEnabled || (mentionMsTeamsUserEnabled && !msteamsUsers.isEmpty()))){
//            String mentionContent = ":arrow_up: \"" + this.payload.getBuildName() + "\" Failed ";
//            if(mentionChannelEnabled){
//                mentionContent += "<!channel> ";
//            }
//            if(mentionMsTeamsUserEnabled && !msteamsUsers.isEmpty() && !this.payload.isMergeBranch()) {
//                mentionContent += StringUtil.join(" ", msteamsUsers);
//            }
//            messageSection.addField("", mentionContent, true);
//        }

        return messageSections;
    }

    private class WebHookPayload {

        @SerializedName("@type") String messageType = "MessageCard";
        @SerializedName("@context") String context = "http://schema.org/extensions";

        private String summary;
        private String title;
        private String text;
        private String themeColor;
        private List<MessageSection> sections;

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThemeColor() {
            return themeColor;
        }

        public void setThemeColor(String themeColor) {
            this.themeColor = themeColor;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<MessageSection> getSections() {
            return sections;
        }

        public void setMessageSections(List<MessageSection> messageSections) {
            this.sections = messageSections;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    public static String convertAttachmentsToJson(List<MessageSection> messageSections) {
        Gson gson = new Gson();
        return gson.toJson(messageSections);
//        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
//        xstream.setMode(XStream.NO_REFERENCES);
//        xstream.alias("build", MessageSection.class);
//        /* For some reason, the items are coming back as "@name" and "@value"
//         * so strip those out with a regex.
//         */
//        return xstream.toXML(messageSections).replaceAll("\"@(fallback|text|pretext|color|fields|title|value|short)\": \"(.*)\"", "\"$1\": \"$2\"");
    }

    public Integer getStatus() {
        return this.resultCode;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBotName() {
        return this.botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String parametersAsQueryString() {
        String s = "";
        for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext(); ) {
            NameValuePair nv = i.next();
            s += "&" + nv.getName() + "=" + nv.getValue();
        }
        if (s.length() > 0) {
            return "?" + s.substring(1);
        }
        return s;
    }

    public void addParam(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
    }

    public void addParams(List<NameValuePair> paramsList) {
        for (Iterator<NameValuePair> i = paramsList.iterator(); i.hasNext(); ) {
            this.params.add(i.next());
        }
    }

    public String getParam(String key) {
        for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext(); ) {
            NameValuePair nv = i.next();
            if (nv.getName().equals(key)) {
                return nv.getValue();
            }
        }
        return "";
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(String enabled) {
        if ("true".equals(enabled.toLowerCase())) {
            this.enabled = true;
        } else {
            this.enabled = false;
        }
    }

    public Boolean isErrored() {
        return errored;
    }

    public void setErrored(Boolean errored) {
        this.errored = errored;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

//	public Integer getEventListBitMask() {
//		return EventListBitMask;
//	}
//
//	public void setTriggerStateBitMask(Integer triggerStateBitMask) {
//		EventListBitMask = triggerStateBitMask;
//	}

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public MsTeamsNotificationPayloadContent getPayload() {
        return payload;
    }

    public void setPayload(MsTeamsNotificationPayloadContent payloadContent) {
        this.payload = payloadContent;
    }

    @Override
    public BuildState getBuildStates() {
        return states;
    }

    @Override
    public void setBuildStates(BuildState states) {
        this.states = states;
    }

    public PostMessageResponse getResponse() {
        return response;
    }

    @Override
    public void setShowBuildAgent(Boolean showBuildAgent) {
        this.showBuildAgent = showBuildAgent;
    }

    @Override
    public void setShowElapsedBuildTime(Boolean showElapsedBuildTime) {
        this.showElapsedBuildTime = showElapsedBuildTime;
    }

    @Override
    public void setShowCommits(boolean showCommits) {
        this.showCommits = showCommits;
    }
	
    @Override
    public void setShowCommitters(boolean showCommitters) {
        this.showCommitters = showCommitters;
    }

    @Override
    public void setMaxCommitsToDisplay(int maxCommitsToDisplay) {
        this.maxCommitsToDisplay = maxCommitsToDisplay;
    }

    @Override
    public void setMentionChannelEnabled(boolean mentionChannelEnabled) {
        this.mentionChannelEnabled = mentionChannelEnabled;
    }

    @Override
    public void setMentionMsTeamsUserEnabled(boolean mentionMsTeamsUserEnabled) {
        this.mentionMsTeamsUserEnabled = mentionMsTeamsUserEnabled;
    }


    @Override
    public void setShowFailureReason(boolean showFailureReason) {
        this.showFailureReason = showFailureReason;
    }

    public boolean getIsApiToken() {
        if(this.token != null && this.token.startsWith("http")){
            // We now accept a webhook url.
            return false;
        }
        return this.token == null || this.token.split("-").length > 1;
    }

    private String formatTime(long seconds){
        if(seconds < 60){
            return seconds + "s";
        }
        return String.format("%dm:%ds",
                TimeUnit.SECONDS.toMinutes(seconds),
                TimeUnit.SECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );
    }
}
