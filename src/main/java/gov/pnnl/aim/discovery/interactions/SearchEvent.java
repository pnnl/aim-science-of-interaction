package gov.pnnl.aim.discovery.interactions;

import java.util.List;


public class SearchEvent extends SemanticInteractionEvent{

	private final String query;
	
	private final List<String> docs;

	  public SearchEvent(final String query, final List<String> docs) {
	    this.query = query;
	    this.docs = docs;
	    this.type = SemanticInteractions.SEARCH;
	  }


	  /**
	   * @return the query
	   */
	  public String getQuery() {
	    return query;
	  }

	  
	  public List<String> getDocs() {
		return docs;
	}


	@Override
	  public String toString() {
	    return type + DELIMITER + timestamp + DELIMITER + query + DELIMITER + docs.toString();
	  }
}
