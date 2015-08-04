package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.af.avro.NousProfileAnswerStreamRecord;
import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.avro.dint.OPAPiersLOBMessage;
import gov.pnnl.aim.avro.dint.POPPiersLOBMessage;
import gov.pnnl.aim.avro.dint.ShyrePiersLOBMessage;
import gov.pnnl.aim.discovery.streams.avro.CompoundsMessage;
import gov.pnnl.aim.discovery.streams.avro.DocumentMessage;
import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;
import gov.pnnl.aim.streams.avro.CompoundPredictMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

public class StreamUtils {

	public static String toString(List<Double> values) {
		
		String strValues = "";
		int size = values.size();
		if (values != null && size > 0) {
			strValues += values.get(0);
			for (int i = 1; i < size; i++) {
				strValues += ",";
				strValues += values.get(i);
			}
		}
		return strValues;
	}
	
	public static DocumentMessage getDocumentMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final DocumentMessage record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, DocumentMessage.class);

		return record;
	}

	
	public static LabeledDocumentMessage getLabeledDocumentMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final LabeledDocumentMessage record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, LabeledDocumentMessage.class);

		return record;
	}


	
	public static CompoundsMessage getCompoundsMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final CompoundsMessage record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, CompoundsMessage.class);

		return record;
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	public static ShyrePiersLOBMessage getShyreMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		ShyrePiersLOBMessage record = null;
		if (result.length > 0) {
			record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, ShyrePiersLOBMessage.class);
		}

		return record;
	}
	
	public static OPAPiersLOBMessage getOPAMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);


		OPAPiersLOBMessage record = null;
		if (result.length > 0) {
			record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, OPAPiersLOBMessage.class);
		}
		return record;
	}
	
	public static POPPiersLOBMessage getPOPMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		POPPiersLOBMessage record = null;
		if (result.length > 0) {
			record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, POPPiersLOBMessage.class);
		}
		
		return record;
	}

	public static CompoundPredictMessage getOPANMRMessage(Response response) throws IOException {

		// De-serialize and display message
		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		CompoundPredictMessage record = null;
		if (result.length > 0) {
			record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, CompoundPredictMessage.class);
		}
		return record;
	}

//	public static NousPathAnswerStreamRecord getNousPathAnswerMessage(Response response) throws IOException {
//
//		final InputStream in = (InputStream) response.getEntity();
//		final byte[] result = IOUtils.toByteArray(in);
//
//		final NousPathAnswerStreamRecord record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, NousPathAnswerStreamRecord.class);
//
//		return record;
//	}
	
	public static gov.pnnl.aim.af.avro.NousProfileAnswerStreamRecord getNousProfileAnswerMessage(Response response) throws IOException {

		final InputStream in = (InputStream) response.getEntity();
		final byte[] result = IOUtils.toByteArray(in);

		final NousProfileAnswerStreamRecord record = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, NousProfileAnswerStreamRecord.class);

		return record;
	}
}
