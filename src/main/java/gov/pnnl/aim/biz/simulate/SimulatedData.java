package gov.pnnl.aim.biz.simulate;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import gov.pnnl.aim.biz.LinesOfBusinessRestService;
import gov.pnnl.aim.biz.RecordSet;

public class SimulatedData {

	public static RecordSet getSimRecordSet() {

		RecordSet rs = new RecordSet();
		rs.setColumns(LinesOfBusinessRestService.rsColumns);

		try {
			final String path = "/gov/pnnl/aim/biz/sample-jcp.csv";
			final URL resURL = LinesOfBusinessRestService.class.getResource(path);

			if (resURL != null) {

				final File file = new File(resURL.getFile());
				CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);

				// For each line in the CSV
				for (CSVRecord record : parser.getRecords()) {

					// Skip header record
					if (record.getRecordNumber() == 1) {
						continue;
					}

					// Copy the values into an array
					List<String> values = new ArrayList<String>();
					for (int i = 0; i < record.size(); i++) {
						values.add(record.get(i));
					}

					// Add the array to the recordset
					rs.addRecord(values);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return rs;
	}
}
