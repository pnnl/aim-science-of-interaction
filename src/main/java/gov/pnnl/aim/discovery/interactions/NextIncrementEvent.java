package gov.pnnl.aim.discovery.interactions;

import java.util.Map;


/** Interaction event for reporting a new increment of data. */
public class NextIncrementEvent extends SemanticInteractionEvent {
	/** Numeric identifier of the new increment. */
	private int incrementNumber;
	
	/** Map of new doc IDs to cluster IDs. */
    private Map<String, Integer> clusterAssignments;

    
    public NextIncrementEvent(int incrementNumber, Map<String, Integer> clusterAssignments) {
        this.type = SemanticInteractions.NEXT_INCREMENT;
        this.incrementNumber = incrementNumber;
        this.clusterAssignments = clusterAssignments;
    }

    @Override
    public String toString() {
        return type + DELIMITER + timestamp + DELIMITER + incrementNumber + DELIMITER + clusterAssignments;
    }
}
