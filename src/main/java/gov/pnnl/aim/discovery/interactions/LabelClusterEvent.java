package gov.pnnl.aim.discovery.interactions;

public class LabelClusterEvent extends SemanticInteractionEvent {

	private final int id;

	  private final String label;

	  /**
	   * @param id
	   * @param label
	   */
	  public LabelClusterEvent(final int id, final String label) {
	    this.id = id;
	    this.label = label;

	    this.type = SemanticInteractions.LABEL_CLUSTER;
	  }

	  /**
	   * @return the id
	   */
	  public int getId() {
	    return id;
	  }

	  /**
	   * @return the label
	   */
	  public String getLabel() {
	    return label;
	  }

	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + id + DELIMITER + label;
	  }
}
