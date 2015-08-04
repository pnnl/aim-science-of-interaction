package gov.pnnl.aim.avro.dint;

import java.util.List;

public class BasicOPAPiersLOBMessage {

	public String company;
  public long startdate;
  public long enddate;
  public List<String> linesofbusiness;
  public List<Double> probabilities;
  
  public BasicOPAPiersLOBMessage(String company, long startdate, long enddate, List<String> linesofbusiness, List<Double> probabilities) {
  	this.company = company;
  	this.startdate = startdate;
  	this.enddate = enddate;
  	this.linesofbusiness = linesofbusiness;
  	this.probabilities = probabilities;  	
  }
  
  public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public long getStartdate() {
		return startdate;
	}
	public void setStartdate(long startdate) {
		this.startdate = startdate;
	}
	public long getEnddate() {
		return enddate;
	}
	public void setEnddate(long enddate) {
		this.enddate = enddate;
	}
	public List<String> getLinesofbusiness() {
		return linesofbusiness;
	}
	public void setLinesofbusiness(List<String> linesofbusiness) {
		this.linesofbusiness = linesofbusiness;
	}
	public List<java.lang.Double> getProbabilities() {
		return probabilities;
	}
	public void setProbabilities(List<java.lang.Double> probabilities) {
		this.probabilities = probabilities;
	}

}
