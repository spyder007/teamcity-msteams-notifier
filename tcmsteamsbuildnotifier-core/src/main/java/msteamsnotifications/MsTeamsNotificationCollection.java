package msteamsnotifications;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MsTeamsNotificationCollection {
	private Map<Integer, MsTeamsNotification> msteamsNotifications;
	private static final Integer SYSTEM = 0;
	private static final Integer SLACKNOTIFICATION = 1;
	private static final Integer SLACKNOTIFICATION_ID = 2;
	private static final Integer SLACKNOTIFICATION_KEY = 3;
	private static final Integer SLACKNOTIFICATION_PARAMETER_ID = 4;
	private static final Integer SLACKNOTIFICATION_PARAMETER_KEY = 5;
	private Map <String, String> origParams; 
	
	public MsTeamsNotificationCollection(Map<String, String> params) {
		msteamsNotifications = new HashMap<Integer, MsTeamsNotification>();
		this.origParams = params;
		this.parseParams(params);
	}
	
	private String getValue(String paramKey) throws MsTeamsNotificationParameterReferenceException {
		if (this.origParams.containsKey(paramKey)){
			String value = this.origParams.get(paramKey);
			if (value.startsWith("%") && value.endsWith("%")){
				return this.getValue(value.substring(1,value.length() - 1));
			} 
			return value;
		} else {
			throw new MsTeamsNotificationParameterReferenceException(paramKey);
		}
	}
	private void parseParams(Map <String, String> params) {
		//msteamsNotifications.add(new MsTeamsNotification("blah"));
        for (Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,String> entry = (Map.Entry<String,String>) iterator.next();
            String name = (String)entry.getKey();
            String val = (String)entry.getValue();
            System.out.println(name + " .. " + val);
            String tokens[] = name.toLowerCase().split("\\.");
            // First check if it's one of our tokens.
            if ("system".equals(tokens[SYSTEM]) && "msteamsNotification".equals(tokens[SLACKNOTIFICATION])
            		&& this.canConvertToInt(tokens[SLACKNOTIFICATION_ID])) {
            	// Check if we have already created a msteamsnotifications instance
            	if (msteamsNotifications.containsKey(this.convertToInt(tokens[SLACKNOTIFICATION_ID]))){
            		if ("enabled".equals(tokens[SLACKNOTIFICATION_KEY])) {
            			msteamsNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setEnabled(val);
            		} else if ("parameter".equals(tokens[SLACKNOTIFICATION_KEY])
            				&& (this.canConvertToInt(tokens[SLACKNOTIFICATION_PARAMETER_ID]))
            				&& ("name".equals(tokens[SLACKNOTIFICATION_PARAMETER_KEY])))
            		{
            			try {
							String myVal = this.getValue("system.msteamsnotifications." + tokens[SLACKNOTIFICATION_ID] + ".parameter."
									+ tokens[SLACKNOTIFICATION_PARAMETER_ID] + ".value");

							msteamsNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).addParam(val, myVal);
						} catch (MsTeamsNotificationParameterReferenceException e) {
							msteamsNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setErrored(true);
							msteamsNotifications.get(this.convertToInt(tokens[SLACKNOTIFICATION_ID])).setErrorReason(
									"MsTeamsNotification Listener: The configured msteamsnotifications parameter ("
									+ name + ") references an alternate non-existant parameter");
						}

            		}
            	} else {
	            	if ("enabled".equals(tokens[SLACKNOTIFICATION_KEY])){
	            		MsTeamsNotification wh = new MsTeamsNotificationImpl();
            			wh.setEnabled(true);
            			this.msteamsNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
            		} else if ("parameter".equals(tokens[SLACKNOTIFICATION_KEY])
            				&& (this.canConvertToInt(tokens[SLACKNOTIFICATION_PARAMETER_ID]))
            				&& ("name".equals(tokens[SLACKNOTIFICATION_PARAMETER_KEY])))
            		{
            			try {
							String myVal = this.getValue("system.msteamsnotifications." + tokens[SLACKNOTIFICATION_ID] + ".parameter."
									+ tokens[SLACKNOTIFICATION_PARAMETER_ID] + ".value");
							MsTeamsNotification wh = new MsTeamsNotificationImpl();
							wh.addParam(val, myVal);
							this.msteamsNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
							
						} catch (MsTeamsNotificationParameterReferenceException e) {
							MsTeamsNotification wh = new MsTeamsNotificationImpl();
							wh.setErrored(true);
							wh.setErrorReason("MsTeamsNotification Listener: The configured msteamsnotifications parameter ("
									+ name + ") references an alternate non-existant parameter");
							this.msteamsNotifications.put(this.convertToInt(tokens[SLACKNOTIFICATION_ID]), wh);
						}
            		}            			
            	}
            }
        }
	}
	
	public Map<Integer, MsTeamsNotification> getMsTeamsNotifications(){
		return this.msteamsNotifications;
	}
	
	public Collection<MsTeamsNotification> getMsTeamsNotificationsAsCollection(){
		return this.msteamsNotifications.values();
	}

	private Boolean canConvertToInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	private Integer convertToInt(String s){
		try{
			int myInt = Integer.parseInt(s);
			return myInt;
		} catch (NumberFormatException e){
			return null;
		}		
	}
}
