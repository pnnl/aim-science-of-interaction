package gov.pnnl.aim.discovery.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.linear.RealMatrix;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.data.MatrixProperties;
import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;
import gov.pnnl.aim.discovery.streams.avro.CanvasStateMessage;
import gov.pnnl.aim.discovery.streams.avro.DocVector;

public class CanvasStreamer {

	public static void runCanvasStream(String username, String canvas, String interaction, List<JsonCluster> clusters, MatrixProperties props) throws IOException, Exception {

		// Setup the connection to ASI
		//final StreamSEI streamWS = ASIConnector.initConnector(AimUtils.readUsername(), AimUtils.readPassword());
		StreamSEI streamWS = ASIConnector.getConnector(username);

		//Set Topic
		streamWS.createDynamicTopic(StreamsConfig.CANVAS_TOPIC);

		//Get Matrix
		RealMatrix matrix = props.getMatrix();
		
		//Create DocVectors for CanvasStateMessage
		List<DocVector> vectors = new ArrayList<DocVector>();
		
		for (JsonCluster cluster : clusters) {
			List<JsonClusterMember> clusterMembers = cluster.getMembers();

			for (JsonClusterMember clusterMember : clusterMembers) {
				String docId = clusterMember.getDocID();
				String label = cluster.getClusterLabel();
				double[] vector = matrix.getRow(props.getRowIndex(docId));
				Double[] doubleVector = ArrayUtils.toObject(vector);
				vectors.add(new DocVector(docId, label, Arrays.asList(doubleVector)));
			}
		}

		//Create CanvasStateMessage
		CanvasStateMessage canvasMessage = new CanvasStateMessage(canvas, interaction, vectors);
		System.out.println("CanvasMessage: Canvas: " + canvas + ", Interaction: " + interaction + ", " + vectors.size() + " Vectors");
		
		//Send CanvasStateMessage
		streamWS.submitDynamicTopicRecord(StreamsConfig.CANVAS_TOPIC , AimUtils.serializeAvroRecordToByteBuffer(canvasMessage).array());
		
		//Get Response Message
		Response response = streamWS.retrieveDynamicTopicRecord(StreamsConfig.CANVAS_TOPIC, true);
		
		// De-serialize it as a document
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final CanvasStateMessage record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, CanvasStateMessage.class);

		System.out.println("Document Recieved!");
		System.out.println("---------------------------------------------------------");
		System.out.println(record.getCanvas());
		System.out.println(record.getInteraction());
		List<DocVector> receivedVectors = record.getDocs();
		int i = 1;
		for(DocVector rVector : receivedVectors){
			System.out.println(i + ": " + rVector.getDocid() + ", " + rVector.getLabel());
			i++;
		}
		System.out.println("---------------------------------------------------------");
		
	}

}
