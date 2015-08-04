package gov.pnnl.aim.discovery.interactions;

public class EndReadEvent extends SemanticInteractionEvent {

	private String docId;
	
	public EndReadEvent(String docId) {
	    this.type = SemanticInteractions.END_READING;
	    this.docId = docId;
	    isReading = false;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docId;
	  }
}
