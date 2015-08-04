package gov.pnnl.aim.nmr;

public class FeedbackErrorCondition {

	private int errorCondition = 0;
	private String sampleID;
	private boolean errorCorrect;

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	public boolean isErrorCorrect() {
		return errorCorrect;
	}

	public void setErrorCorrect(boolean errorCorrect) {
		this.errorCorrect = errorCorrect;
	}

	public int getErrorCondition() {
	  return errorCondition;
  }

	public void setErrorCondition(int errorCondition) {
	  this.errorCondition = errorCondition;
  }
}
