package gov.pnnl.aim.discovery.interactions;

public class RemoveFromClusterEvent extends SemanticInteractionEvent {

	private final String id;

	  /**
	   * @param id
	   */
	  public RemoveFromClusterEvent(final String id) {
	    this.id = id;

	    this.type = SemanticInteractions.REMOVE_FROM_GROUP;
	  }

	  /**
	   * @return the id
	   */
	  public String getId() {
	    return id;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + id;
	  }
}
