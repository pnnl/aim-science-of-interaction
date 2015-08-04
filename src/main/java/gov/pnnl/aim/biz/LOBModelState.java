package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class LOBModelState {
	
	private List<String> linesOfBusiness = new ArrayList<String>();
	private List<CompanyState> companyStates = new ArrayList<CompanyState>();
	
	public LOBModelState() {
		linesOfBusiness.add("Clothing");
		linesOfBusiness.add("Automotive");
		linesOfBusiness.add("Appliances");
		linesOfBusiness.add("Superstores");
		linesOfBusiness.add("Department Stores");
		linesOfBusiness.add("Athletic Stores");
	}
	
	public List<CompanyState> getCompanyStates() {
		return companyStates;
	}

	public void setCompanyStates(List<CompanyState> companyStates) {
		this.companyStates = companyStates;
	}

	public CompanyState getCompanyState(String company) {
		for (CompanyState state : companyStates) {
			if (state.getCompanyName().toLowerCase().equals(company.toLowerCase())) {
				return state;
			}
		}
		return createCompanyState(company);
	}

	public boolean hasCompany(String company) {
		for (CompanyState state : companyStates) {
			if (state.getCompanyName().equals(company)) {
				return true;
			}
		}
		return false;
	}

	private CompanyState createCompanyState(String company) {
		CompanyState state = new CompanyState(company, new double [linesOfBusiness.size()], new double [linesOfBusiness.size()], false);
		companyStates.add(state);
		return state;
	}

	public List<String> getLinesOfBusiness() {
	  return linesOfBusiness;
  }

	public void setLinesOfBusiness(List<String> linesOfBusiness) {
	  this.linesOfBusiness = linesOfBusiness;
  }

	public int getIndex(String lob) {
		for (int i = 0; i < linesOfBusiness.size(); i++) {
			if (linesOfBusiness.get(i).equalsIgnoreCase(lob)) {
				return i;
			}
		}
	  return -1;
  }
}
