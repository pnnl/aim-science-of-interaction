package gov.pnnl.aim.discovery.interactions;

public class MoveClusterEvent extends SemanticInteractionEvent {

	private final int id;

	  private final double x;

	  private final double y;


	  /**
	   * @param id
	   * @param x
	   * @param y
	   */
	  public MoveClusterEvent(final int id, final double x, final double y) {
	    this.id = id;
	    this.x = x;
	    this.y = y;

	    this.type = SemanticInteractions.MOVED_CLUSTER;
	  }

	  /**
	   * @return the id
	   */
	  public int getid() {
	    return id;
	  }
	  
	  public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + id + DELIMITER + x + DELIMITER + y;
	  }
}
