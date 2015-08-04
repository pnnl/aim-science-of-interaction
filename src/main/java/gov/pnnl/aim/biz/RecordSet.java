package gov.pnnl.aim.biz;

import java.util.ArrayList;
import java.util.List;

public class RecordSet {

	private List<String> columns = new ArrayList<String>();
	private List<Record> records = new ArrayList<Record>();

	public List<String> getColumns() {
	  return columns;
  }

	public void setColumns(List<String> columns) {
	  this.columns = columns;
  }

	public List<Record> getRecords() {
	  return records;
  }

	public void setRecords(List<Record> records) {
	  this.records = records;
  }

	public void addRecord(List<String> values) {
	  this.records.add(new Record(values));
  }	
	
}
