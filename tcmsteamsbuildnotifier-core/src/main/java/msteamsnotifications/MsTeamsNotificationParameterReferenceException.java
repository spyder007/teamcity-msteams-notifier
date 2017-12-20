package msteamsnotifications;

@SuppressWarnings("serial")
public class MsTeamsNotificationParameterReferenceException extends Exception {
	String key;

	public MsTeamsNotificationParameterReferenceException(String key){
		super();
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}
}
