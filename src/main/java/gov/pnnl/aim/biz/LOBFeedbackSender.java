package gov.pnnl.aim.biz;

import java.io.IOException;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.AimUtils;

public class LOBFeedbackSender {

	private StreamSEI streamWS = null;

	public LOBFeedbackSender() {
		streamWS = ASIConnector.getConnector(BusinessConfig.ASI_USER);
		streamWS.createDynamicTopic(BusinessConfig.ASI_OPA_FEEDBACK_TOPIC);
	}

	public void sendAddToModel(String company, String value) throws IOException {
		
		OPAPiersLOBFeedbackMessage msg = new OPAPiersLOBFeedbackMessage();
		msg.setCompany(company);
		msg.setLob("AUTOMOTIVE");
		msg.setFeature(value);
		msg.setAdd(true);
		msg.setRemove(false);
		
		streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_OPA_FEEDBACK_TOPIC,
				AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		
		System.out.println("Sending add: " + company + " " + value);
	}

	public void sendRemoveFromModel(String company, String value) throws IOException {

		OPAPiersLOBFeedbackMessage msg = new OPAPiersLOBFeedbackMessage();
		msg.setCompany(company);
		msg.setLob("AUTOMOTIVE");
		msg.setFeature(value);
		msg.setAdd(false);
		msg.setRemove(true);
		
		streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_OPA_FEEDBACK_TOPIC,
				AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		
		System.out.println("Sending remove: " + company + " " + value);
	}
}
