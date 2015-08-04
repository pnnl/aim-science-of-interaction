package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeContext {

	private String infobox = "";
	private List<String> paths = new ArrayList<String>();
	private List<String> profile = new ArrayList<String>();

	public String getInfobox() {
		return infobox;
	}

	public void setInfobox(String infobox) {
		this.infobox = infobox.replaceAll("\\t", "|");
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		List<String> fixed = new ArrayList<String>();
		for (String value : paths) {
			fixed.add(value.replaceAll("\\t", "|"));
		}
		this.paths = fixed;
	}

	public List<String> getProfile() {
		return profile;
	}

	public void setProfile(List<String> profile) { 
		List<String> fixed = new ArrayList<String>();
		for (String value : profile) {
			fixed.add(value.replaceAll("\\t", "|"));
		}
		this.profile = fixed;
	}
}
