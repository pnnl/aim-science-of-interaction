package gov.pnnl.aim.biz;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class HSCodeLookup {

	static Map<String, String> hscodeLookup = new HashMap<String, String>();
	
	static {
		try {
			final String path = "/gov/pnnl/aim/biz/hscode-definitions.csv";
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

					hscodeLookup.put(record.get(2), record.get(3));
				}
	
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String lookup(String hscode) {
		String def = hscodeLookup.get(hscode);
		
		if (def == null || def.trim().length() == 0) {
			def = "(no description)";
		}
		
		return def;
	}
	
	public static List<String> lookup(List<String> hscodes) {
		List<String> descriptions = new ArrayList<String>();
		for (String code : hscodes) {
			descriptions.add(lookup(code));
		}
		return descriptions;
	}
}
