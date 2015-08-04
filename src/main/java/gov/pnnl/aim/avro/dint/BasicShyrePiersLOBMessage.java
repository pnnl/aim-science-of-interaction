package gov.pnnl.aim.avro.dint;

import java.util.List;

public class BasicShyrePiersLOBMessage {
	
  public String company;
	public String lob;
  public double score;
  public long expectedCount;
  public String examplarCompany;
  public double probability;
  public boolean highScore;
  public List<String> hscodes;
  public List<String> recordids;
  
  public BasicShyrePiersLOBMessage(String company, String lob, double score, long expectedCount, String examplarCompany, double probability, boolean highScore, List<String> hscodes, List<String> recordids) {
  	this.company = company;
  	this.lob = lob;
  	this.score = score;
  	this.expectedCount = expectedCount;
  	this.examplarCompany = examplarCompany;
  	this.probability = probability;
  	this.highScore = highScore;
  	this.hscodes = hscodes;
  	this.recordids = recordids;
  }  
  
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLob() {
		return lob;
	}
	public void setLob(String lob) {
		this.lob = lob;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public long getExpectedCount() {
		return expectedCount;
	}
	public void setExpectedCount(long expectedCount) {
		this.expectedCount = expectedCount;
	}
	public String getExamplarCompany() {
		return examplarCompany;
	}
	public void setExamplarCompany(String examplarCompany) {
		this.examplarCompany = examplarCompany;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public boolean isHighScore() {
		return highScore;
	}
	public void setHighScore(boolean highScore) {
		this.highScore = highScore;
	}
	public List<String> getHscodes() {
		return hscodes;
	}
	public void setHscodes(List<String> hscodes) {
		this.hscodes = hscodes;
	}
	public List<String> getRecordids() {
		return recordids;
	}
	public void setRecordids(List<String> recordids) {
		this.recordids = recordids;
	}


}
