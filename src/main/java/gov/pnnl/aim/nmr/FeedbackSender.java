package gov.pnnl.aim.nmr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.AimUtils;
import gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage;
import gov.pnnl.aim.nmr.avro.NMRSampleFeedbackMessage;

public class FeedbackSender {

	private StreamSEI streamWS = null;

	public FeedbackSender() {
		streamWS = ASIConnector.getConnector(NMRConfig.ASI_USER);
		streamWS.createDynamicTopic(NMRConfig.ASI_COMPOUND_FEEDBACK_TOPIC);
		streamWS.createDynamicTopic(NMRConfig.ASI_STATUS_FEEDBACK_TOPIC);
	}

	public void sendCompoundFeedback(String sampleID, String compoundName, boolean compoundPresence, NMRSample sample)
			throws IOException {

		NMRSampleFeedbackMessage msg = new NMRSampleFeedbackMessage();
		msg.setSampleID(sampleID);
		msg.setCompoundName(compoundName);
		msg.setPresence(compoundPresence);
		if (sample != null) {
			List<Double> spectrum = new ArrayList<Double>();
			for (NMRPoint point : sample.getSpectra().getPoints()) {
				spectrum.add(point.getY());
			}
			msg.setSpectrum(spectrum);
		}

		streamWS.submitDynamicTopicRecord(NMRConfig.ASI_COMPOUND_FEEDBACK_TOPIC,
				AimUtils.serializeAvroRecordToByteBuffer(msg).array());
	}

	public void sendErrorStatusFeedback(String sampleID, int errorCondition, boolean errorCorrect) throws IOException {

		NMRQualityStatusFeedbackMessage msg = new NMRQualityStatusFeedbackMessage();
		msg.setSampleID(sampleID);
		msg.setUserStatedqualityStatus(errorCondition);
		msg.setTimestamp(System.currentTimeMillis());

		streamWS.submitDynamicTopicRecord(NMRConfig.ASI_STATUS_FEEDBACK_TOPIC,
				AimUtils.serializeAvroRecordToByteBuffer(msg).array());
	}

	public static NMRSpectra createNewSpectra() {
		NMRSpectra s = new NMRSpectra();
		s.setSpectraId("SP-0");
		s.setPoints(createSpectraPoints());
		s.setTimestamp(System.currentTimeMillis());
		return s;
	}

	/**
	 * @return
	 */
	public static List<NMRPoint> createSpectraPoints() {
		List<NMRPoint> points = new ArrayList<NMRPoint>();
		for (int i = 0; i < 1024; i++) {
			points.add(new NMRPoint(i, Math.random()));
		}
		return points;
	}

	public static void main(String[] args) throws IOException, Exception {

		ASIConnector.initConnector(NMRConfig.ASI_USER, NMRConfig.ASI_PWD);
		FeedbackSender sender = new FeedbackSender();

		for (int i = 0; i < 100; i++) {
			NMRSample sample = new NMRSample();
			sample.setSpectra(FeedbackSender.createNewSpectra());
			String compoundName = NewNMRSampleStreamMonitor.COMPOUNDS[(int) (Math.round((NewNMRSampleStreamMonitor.COMPOUNDS.length - 1) * Math.random()))];
			boolean present = Math.random() < .5;
			
			System.out.println("Sending feedback for:  \"" + compoundName + "\"   Is Present? "+ present);
			sender.sendCompoundFeedback("3", compoundName, present, sample);
		}
	}
}
