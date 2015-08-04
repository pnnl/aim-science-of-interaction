package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class Record {

	private List<String> values = new ArrayList<String>();

	public Record(List<String> values) {
		this.values = values;
	}
	
	public List<String> getValues() {
	  return values;
  }

	public void setValues(List<String> values) {
	  this.values = values;
  }
}
