package gov.pnnl.aim.discovery.interactions;

public class StartReadingEvent extends SemanticInteractionEvent{

		private String docId;
		
		public StartReadingEvent(String docId) {
		    if (docId == null) {
		        throw new NullPointerException();
		    }
		    
		    this.type = SemanticInteractions.START_READING;
		    this.docId = docId;
		    docIdBeingRead = docId;
		    isReading = true;
		  }
		  
		  @Override
		  public String toString() {
		    return type + DELIMITER + timestamp + DELIMITER + docId;
		  }
}
