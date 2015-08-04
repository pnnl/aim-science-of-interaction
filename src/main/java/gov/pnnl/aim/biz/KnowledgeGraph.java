package gov.pnnl.aim.biz;

import static gov.pnnl.aim.af.client.ws.AimAfWsClientFactory.AVRO_MAPPER;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aristotle.aiminterface.NousAnswerStreamRecord;
import gov.pnnl.aristotle.aiminterface.NousPathAnswerStreamRecord;
import gov.pnnl.aristotle.aiminterface.NousPathQuestionStreamRecord;
import gov.pnnl.aristotle.aiminterface.NousProfileAnswerStreamRecord;
import gov.pnnl.aristotle.aiminterface.NousProfileQuestionStreamRecord;

public class KnowledgeGraph {

	public KnowledgeGraph() {
	}

	public static void main(String[] args) throws Exception {
		
		KnowledgeGraph knowGraph = new KnowledgeGraph();
		knowGraph.readStream();
		
//		knowGraph.readPaths();
//		knowGraph.path("honda", "cotton");
//		knowGraph.path("honda", "steel");
//		knowGraph.path("toyota", "long beach");
//		knowGraph.path("toyota", "los angeles");
//		knowGraph.path("toyota", "batteries");
//		knowGraph.path("toyota", "hydrogen");
//		knowGraph.profile("honda");
	}
		
	public void readStream() throws IOException, Exception {

		StreamSEI streamWS = ASIConnector.initConnector(BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);
		Response response = null;
		
		response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_STREAM, true);

		final InputStream in = (InputStream) response.getEntity();
		final byte[] resultBytes = IOUtils.toByteArray(in);

		if (resultBytes.length > 0) {
			
			System.out.println("Response received from NOUS");
			final NousAnswerStreamRecord answer = AVRO_MAPPER
					.reader(NousPathAnswerStreamRecord.class)
					.with(AVRO_MAPPER.schemaFor(NousAnswerStreamRecord.class))
					.readValue(resultBytes);
			
			System.out.println("Response: " + answer.getCompany() + ": " + answer.getTimestamp());
			if (answer.getPaths() != null) {
				List<String> values = new ArrayList<String>();
				for(String value : answer.getPaths()) {
					System.out.println("Value: " + value);
					value = value.replaceAll("\\t", "|");
					values.add(value);
				}
			}
		} else {
			System.out.println("No response from NOUS");
		}
	}
	
	public void readPaths() throws Exception {

		StreamSEI streamWS = ASIConnector.initConnector(BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);
		Response response = null;
		
		for (int i = 0; i < BusinessConfig.ASI_NOUS_RETRIEVE_ATTEMPTS; i++) {

			System.out.println("Retrieving from NOUS answer topic");
			response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_profileAnswerTopicName, true);

			final InputStream in = (InputStream) response.getEntity();
			final byte[] resultBytes = IOUtils.toByteArray(in);

			if (resultBytes.length > 0) {
				
				System.out.println("Response received from NOUS");
				final NousProfileAnswerStreamRecord answer = AVRO_MAPPER
						.reader(NousProfileAnswerStreamRecord.class)
						.with(AVRO_MAPPER.schemaFor(NousProfileAnswerStreamRecord.class))
						.readValue(resultBytes);
				
				System.out.println("Response: " + answer.getSource() + ": " + answer.getTimestamp());
				if (answer.getProfile() != null) {
					List<String> values = new ArrayList<String>();
					for(String value : answer.getProfile()) {
						System.out.println("Value: " + value);
						value = value.replaceAll("\\t", "|");
						values.add(value);
					}
				}
			}
		}
		

		for (int i = 0; i < BusinessConfig.ASI_NOUS_RETRIEVE_ATTEMPTS; i++) {

			System.out.println("Retrieving from NOUS answer topic");
			response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_pathAnswerTopicName, true);

			final InputStream in = (InputStream) response.getEntity();
			final byte[] resultBytes = IOUtils.toByteArray(in);

			if (resultBytes.length > 0) {
				
				System.out.println("Response received from NOUS");
				final NousPathAnswerStreamRecord answer = AVRO_MAPPER
						.reader(NousPathAnswerStreamRecord.class)
						.with(AVRO_MAPPER.schemaFor(NousPathAnswerStreamRecord.class))
						.readValue(resultBytes);
				
				System.out.println("Response: " + answer.getSource() + ": " + answer.getTimestamp());
				if (answer.getPaths() != null) {
					List<String> values = new ArrayList<String>();
					for(String value : answer.getPaths()) {
						System.out.println("Value: " + value);
						value = value.replaceAll("\\t", "|");
						values.add(value);
					}
				}
			}
		}
	}
	
	public List<String> profile(String name) throws IOException, Exception {
		
		name = name.toLowerCase();
		
		System.out.println("Starting profile: " + name);
		
		StreamSEI streamWS = ASIConnector.initConnector(BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);

		final NousProfileQuestionStreamRecord question = new NousProfileQuestionStreamRecord();
		question.setSource(name);

		final byte[] bytes = AVRO_MAPPER
				.writerFor(NousProfileQuestionStreamRecord.class)
				.with(AVRO_MAPPER
						.schemaFor(NousProfileQuestionStreamRecord.class))
				.writeValueAsBytes(question);
		
		Response response = null;
		response = streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_NOUS_profileQuestionTopicName, bytes);
		
		for (int i = 0; i < BusinessConfig.ASI_NOUS_RETRIEVE_ATTEMPTS; i++) {

			System.out.println("Retrieving from NOUS answer topic");
			response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_profileAnswerTopicName, true);

			final InputStream in = (InputStream) response.getEntity();
			final byte[] resultBytes = IOUtils.toByteArray(in);

			if (resultBytes.length > 0) {
				
				System.out.println("Response received from NOUS");
				final NousProfileAnswerStreamRecord answer = AVRO_MAPPER
						.reader(NousProfileAnswerStreamRecord.class)
						.with(AVRO_MAPPER.schemaFor(NousProfileAnswerStreamRecord.class))
						.readValue(resultBytes);
				
				System.out.println("Response: " + answer.getSource() + ": " + answer.getTimestamp());
				if (answer.getProfile() != null) {
					List<String> values = new ArrayList<String>();
					for(String value : answer.getProfile()) {
						System.out.println("Value: " + value);
						value = value.replaceAll("\\t", "|");
						values.add(value);
					}
					return values;
				}
				break;
			}
		}

		return new ArrayList<String>();
	}

	public List<String> path(String entity1, String entity2) throws IOException, Exception {
		
		entity1 = entity1.toLowerCase();
		entity2 = entity2.toLowerCase();
		
		System.out.println("Starting path: " + entity1 + " -> " + entity2);
		
		StreamSEI streamWS = ASIConnector.initConnector(BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);

		final NousPathQuestionStreamRecord question = new NousPathQuestionStreamRecord();
		question.setSource(entity1);
		question.setDestination(entity2);

		final byte[] bytes = AVRO_MAPPER
				.writerFor(NousPathQuestionStreamRecord.class)
				.with(AVRO_MAPPER
						.schemaFor(NousPathQuestionStreamRecord.class))
				.writeValueAsBytes(question);
		
		Response response = null;
		response = streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_NOUS_pathQuestionTopicName, bytes);
		
		for (int i = 0; i < BusinessConfig.ASI_NOUS_RETRIEVE_ATTEMPTS; i++) {

			System.out.println("Retrieving from NOUS answer topic");
			response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_pathAnswerTopicName, true);

			final InputStream in = (InputStream) response.getEntity();
			final byte[] resultBytes = IOUtils.toByteArray(in);

			if (resultBytes.length > 0) {
				
				System.out.println("Response received from NOUS");
				final NousPathAnswerStreamRecord answer = AVRO_MAPPER
						.reader(NousPathAnswerStreamRecord.class)
						.with(AVRO_MAPPER.schemaFor(NousPathAnswerStreamRecord.class))
						.readValue(resultBytes);
				
				System.out.println("Response: " + answer.getSource() + ": " + answer.getTimestamp());
				if (answer.getPaths() != null) {
					List<String> values = new ArrayList<String>();
					for(String value : answer.getPaths()) {
						System.out.println("Value: " + value);
						value = value.replaceAll("\\t", "|");
						values.add(value);
					}
					return values;
				}
				break;
			}
		}

		return new ArrayList<String>();
	}
}
