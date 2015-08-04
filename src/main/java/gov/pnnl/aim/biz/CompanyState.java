package gov.pnnl.aim.biz;

import java.util.HashMap;
import java.util.Map;

public class CompanyState {

	private String companyName = "";
	private double[] opaProbabilities = null;
	private double[] shyreProbabilities = null;
	private boolean isAlert = false;
	private Map<String, OPAExplainer> timeToOPAExplainer = new HashMap<String, OPAExplainer>();
	private Map<String, ShyreExplainer> timeToShyreExplainer = new HashMap<String, ShyreExplainer>();
	private KnowledgeContext context = null;

	public CompanyState(String companyName, double[] opaProbabilities, double[] shyreProbabilities, boolean isAlert) {
		setCompanyName(companyName);
		setOPAProbabilities(opaProbabilities);
		setShyreProbabilities(shyreProbabilities);
		setAlert(isAlert);
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public double[] getOPAProbabilities() {
		return opaProbabilities;
	}

	public void setOPAProbabilities(double[] probabilities) {
		this.opaProbabilities = probabilities;
	}

	public double[] getShyreProbabilities() {
		return shyreProbabilities;
	}

	public void setShyreProbabilities(double[] probabilities) {
		this.shyreProbabilities = probabilities;
	}

	public boolean isAlert() {
		return isAlert;
	}

	public void setAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}

	public void setOPAProbability(int index, double probability) {
		opaProbabilities[index] = probability;
	}

	public void setShyreProbability(boolean isHigh, int index, double probability) {
		// This handles stair stepping
		// shyreProbabilities[index] = Math.max(shyreProbabilities[index],
		// probability);

		if (isHigh) {
			shyreProbabilities[index] = probability;

			for (int i = 0; i < shyreProbabilities.length; i++) {
				if (i != index) {
					shyreProbabilities[i] = 0;
				}
			}
		}
	}

	public Map<String, OPAExplainer> getTimeToOPAExplainer() {
		return timeToOPAExplainer;
	}

	public void setTimeToOPAExplainer(Map<String, OPAExplainer> timeToOPAExplainer) {
		this.timeToOPAExplainer = timeToOPAExplainer;
	}

	public Map<String, ShyreExplainer> getTimeToShyreExplainer() {
		return timeToShyreExplainer;
	}

	public void setTimeToShyreExplainer(Map<String, ShyreExplainer> timeToShyreExplainer) {
		this.timeToShyreExplainer = timeToShyreExplainer;
	}

	public KnowledgeContext getContext() {
		return context;
	}

	public void setContext(KnowledgeContext context) {
		this.context = context;
	}

}
