package gov.pnnl.aim.discovery.interactions;

public class MoveToClusterEvent extends SemanticInteractionEvent {

	private final String docId;

	  private final int oldId;

	  private final int newId;

	  /**
	   * @param id
	   * @param label
	   * @param x
	   * @param y
	   */
	  public MoveToClusterEvent(final String docId, final int oldId, final int newId) {
	    this.docId = docId;
	    this.oldId = oldId;
	    this.newId = newId;

	    this.type = SemanticInteractions.MOVE_TO_GROUP;
	  }

	  /**
	   * @return the id
	   */
	  public String getDocId() {
	    return docId;
	  }
	  
	  public int getOldId() {
		return oldId;
	}

	public int getNewId() {
		return newId;
	}

	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + docId + DELIMITER + oldId + DELIMITER + newId;
	  }
}
