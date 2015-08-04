package gov.pnnl.aim.discovery.interactions;

public class UnloadDocEvent extends SemanticInteractionEvent {

	private final String docName;

	  /**
	   * @param id
	   */
	  public UnloadDocEvent(final String name) {
	    this.docName = name;

	    this.type = SemanticInteractions.UNLOAD_DOC;
	  }

	  /**
	   * @return the id
	   */
	  public String getDocName() {
	    return docName;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docName;
	  }
}
