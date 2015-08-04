package gov.pnnl.aim.discovery.interactions;

public class GetAnnotationEvent extends SemanticInteractionEvent {

	private final String docId;

	  /**
	   * @param id
	   */
	  public GetAnnotationEvent(final String id) {
	    this.docId = id;

	    this.type = SemanticInteractions.GET_ANNOTATION;
	  }

	  /**
	   * @return the id
	   */
	  public String getDocId() {
	    return docId;
	  }


	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docId;
	  }
}
