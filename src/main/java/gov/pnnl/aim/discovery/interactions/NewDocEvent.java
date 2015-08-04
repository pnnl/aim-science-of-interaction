package gov.pnnl.aim.discovery.interactions;

import gov.pnnl.aim.discovery.pojo.JsonClusterMemberHome;

import java.util.List;

public class NewDocEvent extends SemanticInteractionEvent {
	
	private final List<JsonClusterMemberHome> list;

	public NewDocEvent(List<JsonClusterMemberHome> list) {
	    this.type = SemanticInteractions.GET_NEW_DOCS;
	    this.list = list;
	  }
	  
	  @Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + list;
	  }
}
