package gov.pnnl.aim.discovery.interactions;

import java.util.Arrays;

/** Event for reporting feature weight changes. */
public class ChangeFeatureWeightsEvent extends SemanticInteractionEvent {

    private double[] weights;

    
    public ChangeFeatureWeightsEvent(double[] weights) {
        this.type = SemanticInteractions.CHANGE_FEATURE_WEIGHTS;
        this.weights = weights;
    }
    
    @Override
    public String toString() {
        return type + DELIMITER + timestamp + DELIMITER + Arrays.toString(weights);
    }
}
