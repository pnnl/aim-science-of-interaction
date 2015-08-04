package gov.pnnl.aim.discovery.interactions;

public class GetClusterEvent extends SemanticInteractionEvent {

	private final int id;

	  /**
	   * @param id
	   * @param label
	   */
	  public GetClusterEvent(final int id) {
	    this.id = id;

	    this.type = SemanticInteractions.GET_CLUSTER;
	  }

	  /**
	   * @return the id
	   */
	  public int getId() {
	    return id;
	  }

	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + id;
	  }
}
