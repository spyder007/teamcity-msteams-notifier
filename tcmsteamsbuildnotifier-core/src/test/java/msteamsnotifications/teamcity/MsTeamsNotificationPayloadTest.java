package msteamsnotifications.teamcity;

import msteamsnotifications.MessageSection;
import org.junit.Test;
import msteamsnotifications.MsTeamsNotificationImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class MsTeamsNotificationPayloadTest {


    @Test
    public void TestAttachmentListToJson()
    {
        List<MessageSection> messageSectionList = new ArrayList<MessageSection>();
        MessageSection messageSection = new MessageSection("title", "text");
        messageSectionList.add(messageSection);

        String json = MsTeamsNotificationImpl.convertAttachmentsToJson(messageSectionList);

        assertNotNull(json);
        assertNotSame("", json);

        assertTrue(json.startsWith("["));

        System.out.println(json);
    }

}
