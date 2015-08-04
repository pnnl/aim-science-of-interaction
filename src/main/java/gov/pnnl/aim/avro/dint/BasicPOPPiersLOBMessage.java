package gov.pnnl.aim.avro.dint;

public class BasicPOPPiersLOBMessage {
	public String company;
	public String linesofbusiness;
	public long date;
	public double score;
	public String usermsg;
	
	public BasicPOPPiersLOBMessage(String company, String linesofbusiness, long date, double score, String usermsg) {
		this.company = company;
		this.linesofbusiness = linesofbusiness;
		this.date = date;
		this.score = score;
		this.usermsg = usermsg;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLinesofbusiness() {
		return linesofbusiness;
	}
	public void setLinesofbusiness(String linesofbusiness) {
		this.linesofbusiness = linesofbusiness;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getUsermsg() {
		return usermsg;
	}
	public void setUsermsg(String usermsg) {
		this.usermsg = usermsg;
	}
	
}
