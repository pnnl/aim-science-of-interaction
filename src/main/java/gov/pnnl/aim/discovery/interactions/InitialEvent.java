package gov.pnnl.aim.discovery.interactions;

import gov.pnnl.aim.discovery.pojo.JsonCluster;

import java.util.List;

public class InitialEvent extends SemanticInteractionEvent {

	private final List<JsonCluster> list;

	  /**
	   * @param id
	   */
	  public InitialEvent(final List<JsonCluster> list) {
	    this.list = list;

	    this.type = SemanticInteractions.INITIALIZED;
	  }

	  /**
	   * @return the list
	   */
	  public List<JsonCluster> getList() {
	    return list;
	  }


	  
	  @Override
	  public String toString() {
		  String objData = type + DELIMITER + timestamp;
		  for(int i = 0; i < list.size(); i++){
			  if(i == 0){
			  objData += DELIMITER + list.get(i) + "\n";
			  }
			  else{
				  objData += DELIMITER + DELIMITER + list.get(i) + "\n";
			  }
		  }
		  return objData;
	  }
}
