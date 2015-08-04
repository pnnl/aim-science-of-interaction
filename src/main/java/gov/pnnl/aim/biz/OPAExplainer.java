package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class OPAExplainer {

	private List<OPAFeature> features = new ArrayList<OPAFeature>();
	
	public OPAExplainer() {
	}

	public List<OPAFeature> getFeatures() {
	  return features;
  }

	public void setFeatures(List<OPAFeature> features) {
	  this.features = features;
  }
}
