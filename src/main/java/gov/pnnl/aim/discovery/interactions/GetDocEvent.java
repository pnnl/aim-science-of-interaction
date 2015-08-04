package gov.pnnl.aim.discovery.interactions;

public class GetDocEvent extends SemanticInteractionEvent {

	private final String docId;
	
	  private final String text;


	  public GetDocEvent(final String docId, final String text) {
		this.docId = docId;
	    this.text = text;

	    this.type = SemanticInteractions.GET_DOCUMENT;
	  }


	  /**
	   * @return the text
	   */
	  public String getText() {
	    return text;
	  }
	  
	  public String getDocId() {
		return docId;
	}


	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docId + DELIMITER + text;
	  }

}
