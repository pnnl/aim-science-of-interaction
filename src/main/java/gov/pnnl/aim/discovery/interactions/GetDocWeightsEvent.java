package gov.pnnl.aim.discovery.interactions;

import java.util.Map;

public class GetDocWeightsEvent extends SemanticInteractionEvent {
		
		private final Map<String, Integer> weights;

		public GetDocWeightsEvent(Map<String, Integer> weights) {
		    this.type = SemanticInteractions.GET_DOC_WEIGHTS;
		    this.weights = weights;
		  }
		  
		  @Override
		  public String toString() {
		    return type + DELIMITER + timestamp + DELIMITER + weights;
		  }
}
