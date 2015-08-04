package gov.pnnl.aim.discovery.interactions;

public class AddDocToClusterEvent extends SemanticInteractionEvent {

	private final String docId;

	  private final String text;

	  private final String title;

	  private final int clusterId;

	  /**
	   * @param id
	   * @param label
	   * @param x
	   * @param y
	   */
	  public AddDocToClusterEvent(final String docId, final String text, final String title, final int clusterId) {
	    this.docId = docId;
	    this.text = text;
	    this.title = title;
	    this.clusterId = clusterId;

	    this.type = SemanticInteractions.ADD_DOC_TO_CLUSTER;
	  }

	  /**
	   * @return the id
	   */
	  public String getDocId() {
	    return docId;
	  }

	  /**
	   * @return the text
	   */
	  public String getText() {
	    return text;
	  }


	  
	  public String getTitle() {
		return title;
	}

	public int getClusterId() {
		return clusterId;
	}

	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docId + DELIMITER + title + DELIMITER + text + DELIMITER + clusterId;
	  }
}
