package gov.pnnl.aim.biz;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.stream.JsonReader;

public class PiersRecordRetriever {
	
	public static RecordSet getRecordSet(String compName) throws ClientProtocolException, IOException {
		
		if (compName.equalsIgnoreCase("toyota")) {
			compName = "toyota_hm";
		}
		
		RecordSet rs = new RecordSet();
		List<String> columns = new ArrayList<String>();
				
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BusinessConfig.PIERS_DB_URL + compName);
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("Accept", "application/json");
		
		CloseableHttpResponse response = httpclient.execute(httpGet);

		boolean hasRecords = false;
		try {
			HttpEntity entity1 = response.getEntity();

			String content = EntityUtils.toString(entity1);

			JsonReader reader = new JsonReader(new StringReader(content));

			reader.beginArray();
			while (reader.hasNext()) {

				boolean hasAppTag = false;
				List<String> values = new ArrayList<String>();
				
				reader.beginObject();
				while (reader.hasNext()) {
	
					hasRecords = true;

					String name = reader.nextName();
	
					if (name.equals("company")) {
						reader.nextString();
					} else if (name.equals("shipper")) {
						reader.nextString();
					} else if (name.equals("headers") && columns.size() == 0) {
						
						// read array
						reader.beginArray();
						while (reader.hasNext()) {
							String h = reader.nextString();
							h = h.replaceAll("\\.", " ");
							columns.add(h);
						}
						columns.add("Flagged");
						reader.endArray();
						
						rs.setColumns(columns);
	
					} else if (name.equals("values")) {
												
						// read array
						reader.beginArray();
						while (reader.hasNext()) {
							String v = reader.nextString();
							if (v.length() > 50) {
								v = v.substring(0, 48) + "...";
							}
							values.add(v);
						}
						reader.endArray();
						
					} else if (name.equals("applicationTags")) {

						// read array
						reader.beginArray();
						while (reader.hasNext()) {

							reader.beginObject();
							while (reader.hasNext()) {

								String innerName = reader.nextName();
								
								if (innerName.equals("application")) {
									String appName = reader.nextString();
									if (appName.equalsIgnoreCase("pop")) {
										hasAppTag = true;
									}
								} else if (innerName.equals("tag")) {
									reader.nextString();
								} else if (innerName.equals("value")) {
									reader.nextString();
								}
							}
							reader.endObject();
							
						}
						reader.endArray();
						
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();

				values.add((hasAppTag) ? "Yes" : "No");
				rs.addRecord(values);
			}
			reader.endArray();

			reader.close();

			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity1);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			response.close();
		}
		
		if (hasRecords == false) {
			columns.add("Error Message");
			List<String> values = new ArrayList<String>();
			values.add("No records found for " + compName);
			rs.setColumns(columns);
			rs.addRecord(values);
		}
		
		return rs;
	}
}
