package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.avro.DocumentMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DocumentStreamer {

	public static void main(String[] args) throws Exception {

		// Find the path to the files
		String path = DocumentStreamer.class.getResource("/data/GasTech/content/articles").getPath();
		File dir = new File(path);

		// Setup the connection to ASI
		final StreamSEI streamWS = AimAfWsClientFactory.generateAimAfStreamClientV2("https", "aim-af.pnl.gov", 8443, AimUtils.readUsername(), AimUtils.readPassword());

		// Remove the old topic if it exists
		Thread.sleep(2000);
		streamWS.removeDynamicTopic(StreamsConfig.DOCUMENTS_TOPIC); 
		Thread.sleep(2000);

		// Create a new topic
		streamWS.createDynamicTopic(StreamsConfig.DOCUMENTS_TOPIC);

		for (File file : dir.listFiles()) {

			// Read in the file contents
			String content = FileUtils.readFileToString(file);
			System.out.println(file.getName() + " = " + content.length());

			// Create a Document object for sending to AIM
			DocumentMessage record = new DocumentMessage("gastech", file.getName(), content);

			// Send it
			System.out.println("Starting send: " + file.getName());
			streamWS.submitDynamicTopicRecord(StreamsConfig.DOCUMENTS_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(record).array());
			System.out.println("Sent! " + file.getName());
			Thread.sleep(1000);

			break;
		}

		streamWS.removeDynamicTopic(StreamsConfig.DOCUMENTS_TOPIC);
	}

	public static DocumentMessage getRecord(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final DocumentMessage record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, DocumentMessage.class);

		return record;
	}
}
