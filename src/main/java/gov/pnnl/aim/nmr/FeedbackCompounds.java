package gov.pnnl.aim.nmr;

public class FeedbackCompounds {

	private String sampleID;
	private String compoundName;
	private boolean compoundPresence;

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	public boolean isCompoundPresence() {
		return compoundPresence;
	}

	public void setCompoundPresence(boolean compoundPresence) {
		this.compoundPresence = compoundPresence;
	}

}
