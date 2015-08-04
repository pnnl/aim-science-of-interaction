package gov.pnnl.aim.biz;

public class OPAFeature {

	private String name = "";
	private String lineOfBusiness = "";
	private double importance = 0.0;
	
	private double inMean= 0.0;
	private double inStdDev = 0.0;

	private double outMean = 0.0;
	private double outStdDev = 0.0;
	
	private double currentValue= 0.0; 
	
	public OPAFeature(String name, String lineOfBusiness, double importance, double inMean, double inStdDev, double outMean, double outStdDev, double currentValue) {
		
		setName(name);
		setLineOfBusiness(lineOfBusiness);
		setInMean(inMean);
		setInStdDev(inStdDev);
		setOutMean(outMean);
		setOutStdDev(outStdDev);
		setCurrentValue(currentValue);
		
	}
	
	public String getKey() {
		return name + "-" + getLineOfBusiness();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public double getInMean() {
		return inMean;
	}

	public void setInMean(double inMean) {
		this.inMean = inMean;
	}

	public double getInStdDev() {
		return inStdDev;
	}

	public void setInStdDev(double inStdDev) {
		this.inStdDev = inStdDev;
	}

	public double getOutMean() {
		return outMean;
	}

	public void setOutMean(double outMean) {
		this.outMean = outMean;
	}

	public double getOutStdDev() {
		return outStdDev;
	}

	public void setOutStdDev(double outStdDev) {
		this.outStdDev = outStdDev;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}
}
