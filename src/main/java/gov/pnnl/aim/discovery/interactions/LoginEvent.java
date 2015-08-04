package gov.pnnl.aim.discovery.interactions;

public class LoginEvent extends SemanticInteractionEvent {

	  private final String username;

	  public LoginEvent(final String usrName) {
	    this.username = usrName;

	    this.type = SemanticInteractions.LOGIN;
	  }

	  
	  public String getUsername() {
		return username;
	}


	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + username;
	  }
}
