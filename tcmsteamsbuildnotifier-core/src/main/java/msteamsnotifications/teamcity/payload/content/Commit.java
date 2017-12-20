package msteamsnotifications.teamcity.payload.content;

import jetbrains.buildServer.util.StringUtil;

/**
 * Created by Peter on 4/06/2014.
 */
public class Commit {

    public Commit(String revision, String description, String userName, String msteamsUserName) {
        this.description = description;
        this.userName = userName;
        this.revision = revision;

        if(msteamsUserName != null && msteamsUserName.startsWith("@")){
            msteamsUserName = msteamsUserName.substring(1);
        }
        this.msteamsUserName = msteamsUserName;
    }

    private String description;
    private String userName;
    private String revision;
    private String msteamsUserName;

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsTeamsUserName() {
        return msteamsUserName;
    }

    public void setMsTeamsUserName(String msteamsUserName) {
        if(msteamsUserName != null && msteamsUserName.startsWith("@")){
            msteamsUserName = msteamsUserName.substring(1);
        }
        this.msteamsUserName = msteamsUserName;
    }

    public boolean hasMsTeamsUsername(){
        return msteamsUserName != null && StringUtil.isNotEmpty(msteamsUserName);
    }
}
