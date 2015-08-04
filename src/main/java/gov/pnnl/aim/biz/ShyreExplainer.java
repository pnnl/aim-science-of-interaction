package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class ShyreExplainer {

	private String lineOfBusiness = "";
	private String companyName = "";
	private int codeCount = 0;
	private int totalCount = 0;
	private List<String> codes = new ArrayList<String>();
	private List<String> descriptions = new ArrayList<String>();
	
	public ShyreExplainer() {
		
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public int getCodeCount() {
		return codeCount;
	}
	
	public void setCodeCount(int codeCount) {
		this.codeCount = codeCount;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public List<String> getCodes() {
		return codes;
	}
	
	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public void addCode(String code, String description) {
		this.codes.add(code);
		this.descriptions.add(description);
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}
}
