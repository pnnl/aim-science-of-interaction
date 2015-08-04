package gov.pnnl.aim.discovery.interactions;

public class RemoveClusterEvent extends SemanticInteractionEvent {

	private final int clusterId;

	  /**
	   * @param id
	   */
	  public RemoveClusterEvent(final int id) {
	    this.clusterId = id;

	    this.type = SemanticInteractions.REMOVE_GROUP;
	  }

	  /**
	   * @return the id
	   */
	  public int getClusterId() {
	    return clusterId;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + clusterId;
	  }
}
