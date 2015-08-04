package gov.pnnl.aim.biz;

public class CompanyProbabilities {

	private String company;
	private long datetime;
	private double[] probabilities;

	public CompanyProbabilities(String company, long datetime, double [] probabilities) {
		this.company = company;
		this.datetime = datetime;
		this.probabilities = probabilities;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	public double[] getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(double[] probabilities) {
		this.probabilities = probabilities;
	}

}
