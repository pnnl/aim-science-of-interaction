package gov.pnnl.aim.discovery.interactions;

public class Event extends SemanticInteractionEvent {

	public Event(SemanticInteractions event) {
	    this.type = event;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp;
	  }
}
