package msteamsnotifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 3/06/2014.
 */
public class MessageSection {
    private String title;
    private String activityImage;
    private String activityTitle;
    private String activitySubtitle;
    private String activityText;
    private String text;
    private boolean startGroup;

    private List<Field> facts;

    public MessageSection(String title, String text) {
        this.title = title;
        this.text = text;

        this.facts = new ArrayList<Field>();
    }

    public void addFact(String title, String value) {
        this.facts.add(new Field(title, value));
    }

    public String getText() {
        return text;
    }

    public List<Field> getFacts() {
        return facts;
    }

    public String getActivityImage() { return activityImage; }

    public void setActivityImage(String activityImage) {
        this.activityImage = activityImage;
    }
}
