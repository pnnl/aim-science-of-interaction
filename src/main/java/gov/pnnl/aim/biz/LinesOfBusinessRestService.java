package gov.pnnl.aim.biz;

import gov.pnnl.aim.avro.dint.BasicOPAPiersLOBMessage;
import gov.pnnl.aim.avro.dint.BasicPOPPiersLOBMessage;
import gov.pnnl.aim.avro.dint.BasicShyrePiersLOBMessage;
import gov.pnnl.aim.biz.simulate.SimulateBizInput;
import gov.pnnl.aim.biz.simulate.SimulatedData;
import gov.pnnl.aim.discovery.streams.ASIConnector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LinesOfBusinessRestService {

	public static List<String> linesOfBusiness = new ArrayList<String>();
	public static List<String> companies = new ArrayList<String>();
	public static List<String> rsColumns = new ArrayList<String>();

	private BusinessMonitor monitor = null;
	private LOBFeedbackSender feedbackSender = null;
	private LOBModelState modelState = new LOBModelState();
	private BusinessQueue queue = new BusinessQueue(modelState);
	private SimulateBizInput simulator = null;

	static {
		linesOfBusiness.add("Clothing");
		linesOfBusiness.add("Automotive");
		linesOfBusiness.add("Appliances");
		linesOfBusiness.add("Superstores");
		linesOfBusiness.add("Department Stores");
		linesOfBusiness.add("Athletic Stores");

		companies.add("adidas");
		companies.add("bmw");
		companies.add("bosch");
		companies.add("chrysler");
		companies.add("costco");
		companies.add("electrolux");
		companies.add("ford");
		companies.add("fredmeyer");
		companies.add("ge");
		companies.add("guess");
		companies.add("gymboree");
		companies.add("hm");
		companies.add("honda");
		companies.add("hyundai");
		companies.add("jcpenney");
		companies.add("jcrew");
		companies.add("kmart");
		companies.add("levi");
		companies.add("lg");
		companies.add("macys");
		companies.add("merged");
		companies.add("nike");
		companies.add("nissan");
		companies.add("oldnavy");
		companies.add("ralphlauren");
		companies.add("sears");
		companies.add("target");
		companies.add("toyota");
		companies.add("vw");
		companies.add("walmart");
		companies.add("whirlpool");

		rsColumns.add("Company");
		rsColumns.add("Date");
		rsColumns.add("Shipper");
		rsColumns.add("Consignee");
		rsColumns.add("Weight");
		rsColumns.add("Weight Unit");
		rsColumns.add("Measure");
		rsColumns.add("Country of Origin");
		rsColumns.add("Port of Arrival");
		rsColumns.add("Port of Departure");
		rsColumns.add("U.S. Destination");
		rsColumns.add("Arrival Date");
		rsColumns.add("HS Code");
		rsColumns.add("Commodity Short Description");
	}

	@RequestMapping(value = "/lob/init", method = RequestMethod.GET)
	public @ResponseBody String initStreams(final HttpServletRequest request, final HttpServletResponse response) {

		System.out.println("Init ASI connection");
		try {
			if (this.monitor == null) {
				ASIConnector.initConnector(BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);
				this.monitor = new BusinessMonitor(queue);
				this.feedbackSender = new LOBFeedbackSender();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	@RequestMapping(value = "/lob/simulate/start", method = RequestMethod.GET)
	public @ResponseBody String startSimulateStreams(final HttpServletRequest request,
			final HttpServletResponse response) {
		simulator = new SimulateBizInput();
		simulator.start();
		return "";
	}

	@RequestMapping(value = "/lob/simulate/stop", method = RequestMethod.GET)
	public @ResponseBody String stopSimulateStreams(final HttpServletRequest request,
			final HttpServletResponse response) {
		simulator.stopSimulation();
		return "";
	}

	@RequestMapping(value = "/lob/stream/start", method = RequestMethod.GET)
	public @ResponseBody String startProdStreams(final HttpServletRequest request, final HttpServletResponse response) {
		monitor.start();
		return "";
	}

	@RequestMapping(value = "/lob/stream/stop", method = RequestMethod.GET)
	public @ResponseBody String stopProdStreams(final HttpServletRequest request, final HttpServletResponse response) {
		monitor.stop();
		return "";
	}

	@RequestMapping(value = "/lob/list", method = RequestMethod.GET)
	public @ResponseBody List<String> getLOBList(final HttpServletRequest request, final HttpServletResponse response) {
		return linesOfBusiness;
	}

	@RequestMapping(value = "/lob/company/stream", method = RequestMethod.GET)
	public @ResponseBody List<CompanyProbabilities> getLOBStream(final HttpServletRequest request,
			final HttpServletResponse response) {

		List<CompanyProbabilities> probs = new ArrayList<CompanyProbabilities>();

		long millis = System.currentTimeMillis();
		for (String company : companies) {
			double[] values = new double[] { .1 + (.1 * Math.random()), .8 + (.1 * Math.random()),
					.2 + (.1 * Math.random()), .3 + (.1 * Math.random()) };
			probs.add(new CompanyProbabilities(company, millis, values));
		}

		return probs;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/lob/state", method = RequestMethod.GET)
	public @ResponseBody LOBModelState getState(final HttpServletRequest request, final HttpServletResponse response) {
		return this.modelState;
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/shyre/stream", method = RequestMethod.GET)
	public @ResponseBody List<BasicShyrePiersLOBMessage> getShyreProbabilityStream(final HttpServletRequest request,
			final HttpServletResponse response) {
		return this.monitor.getQueue().getShyreDataAndClearQueue();
	}

	@RequestMapping(value = "/lob/company/opa/stream", method = RequestMethod.GET)
	public @ResponseBody List<BasicOPAPiersLOBMessage> getOPAProbabilityStream(final HttpServletRequest request,
			final HttpServletResponse response) {
		return this.monitor.getQueue().getOPADataAndClearQueue();
	}

	@RequestMapping(value = "/lob/company/pop/stream", method = RequestMethod.GET)
	public @ResponseBody List<BasicPOPPiersLOBMessage> getPOPAlertStream(final HttpServletRequest request,
			final HttpServletResponse response) {
		return this.monitor.getQueue().getPOPDataAndClearQueue();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/ranks", method = RequestMethod.GET)
	public @ResponseBody Map<String, Integer> getRanks(final HttpServletRequest request,
			final HttpServletResponse response) {

		Map<String, Integer> ranks = new HashMap<String, Integer>();

		for (int i = 0; i < companies.size(); i++) {
			ranks.put(companies.get(i), i + 1);
		}
		return ranks;
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/context", method = RequestMethod.GET)
	public @ResponseBody KnowledgeContext getContext(
			@RequestParam(value = "company", required = false, defaultValue = "") final String company,
			@RequestParam(value = "lob", required = false, defaultValue = "") final String lob,
			@RequestParam(value = "timeid", required = false, defaultValue = "0") final String timeID,
			final HttpServletRequest request, final HttpServletResponse response) {

		if (company != null && company.trim().length() > 0) {

			CompanyState compState = modelState.getCompanyState(company);
			
			return compState.getContext();
			
			
//			KnowledgeGraph kg = new KnowledgeGraph();
//			try {
//				contextValues = kg.profile(company);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	
//			if (contextValues.size() == 0) {
//				contextValues.add("toyota|hasURL|http://toyota.com");
//				contextValues.add("toyota|LocatedIn|Minato_Tokyo");
//				contextValues.add("toyota|installed|solar_canopy");
//				contextValues.add("toyota|paid|24M");
//				contextValues.add("toyota|imported|Steel");
//				contextValues.add("toyota|imported|Iron ");
//			}
		}

		return null;
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/emerging", method = RequestMethod.GET)
	public @ResponseBody RecordSet getEmergingFeatures(
			@RequestParam(value = "company", required = false, defaultValue = "Ford") final String company,
			@RequestParam(value = "lob", required = false, defaultValue = "auto") final String lob,
			@RequestParam(value = "timeid", required = false, defaultValue = "1") final String timeID,
			final HttpServletRequest request, final HttpServletResponse response) {

		List<String> columns = new ArrayList<String>(Arrays.asList(new String[] { "Type", "Emerging Value" }));

		RecordSet rs = new RecordSet();
		rs.setColumns(columns);

		rs.addRecord(Arrays.asList(new String[] { "port", "long beach" }));
		rs.addRecord(Arrays.asList(new String[] { "port", "wilmington nc" }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "injector" }));
		rs.addRecord(Arrays.asList(new String[] { "port", "norfolk" }));
		rs.addRecord(Arrays.asList(new String[] { "port", "los angeles" }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "pump" }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "disc" }));
		rs.addRecord(Arrays.asList(new String[] { "port", "miami" }));
		rs.addRecord(Arrays.asList(new String[] { "port", "new york" }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "wheel", }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "brake" }));
		rs.addRecord(Arrays.asList(new String[] { "commodity", "battery" }));

		return rs;
	}

	/**
	 * 
	 * @param company
	 * @param lob
	 * @param timeID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/lob/company/explainers/shyre", method = RequestMethod.GET)
	public @ResponseBody List<ShyreExplainer> getExplainersShyre(@RequestParam("company") final String company,
			final HttpServletRequest request, final HttpServletResponse response) {

		CompanyState compState = modelState.getCompanyState(company);

		ShyreExplainer ex = compState.getTimeToShyreExplainer().get("0");
		List<ShyreExplainer> explainers = new ArrayList<ShyreExplainer>();
		if (ex != null) {
			explainers.add(ex);
		}

		return explainers;
	}

	/**
	 * 
	 * @param company
	 * @param lob
	 * @param timeID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/lob/company/explainers/opa", method = RequestMethod.GET)
	public @ResponseBody List<OPAExplainer> getExplainersOPA(@RequestParam("company") final String company,
			final HttpServletRequest request, final HttpServletResponse response) {

		CompanyState compState = modelState.getCompanyState(company);
		Map<String, OPAExplainer> opaExplainers = compState.getTimeToOPAExplainer();

		List<OPAExplainer> explainers = new ArrayList<OPAExplainer>();
		if (opaExplainers != null && opaExplainers.size() > 0) {

			List<OPAFeature> features = new ArrayList<OPAFeature>();
			
			for (Entry<String, OPAExplainer> entry : opaExplainers.entrySet()) {
				OPAExplainer ex = entry.getValue();	
				features.addAll(ex.getFeatures());
			}

			OPAExplainer ex = new OPAExplainer();
			ex.setFeatures(features);
			
			explainers.add(ex);
			
		} else {

			List<OPAFeature> features = new ArrayList<OPAFeature>();
			
			try {
				final String path = "/gov/pnnl/aim/biz/opamodel.csv";
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

						String keyword = record.get(0);
						double yesMean = Double.parseDouble(record.get(1));
						double noMean = Double.parseDouble(record.get(2));
						double yesStdev = Double.parseDouble(record.get(3));
						double noStdev = Double.parseDouble(record.get(4));
						double importance = Double.parseDouble(record.get(5));
						features.add(new OPAFeature(keyword, "automotive", importance, yesMean, yesStdev, noMean, noStdev, Math.random()));
					}					
					parser.close();		
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			OPAExplainer ex = new OPAExplainer();
			ex.setFeatures(features);
			
			explainers.add(ex);
		}

		return explainers;
	}
	
	/**
	 * 
	 * @param company
	 * @param lob
	 * @param timeID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/lob/company/proportions/opa", method = RequestMethod.GET)
	public @ResponseBody Map<String, Double> getProportionsOPA(@RequestParam("company") final String company,
			final HttpServletRequest request, final HttpServletResponse response) {

		Map<String, Double> featProportions = new HashMap<String, Double>();
		
		CompanyState compState = modelState.getCompanyState(company);
		Map<String, OPAExplainer> opaExplainers = compState.getTimeToOPAExplainer();
				
		if (opaExplainers != null && opaExplainers.size() > 0) {
			
			for (Entry<String, OPAExplainer> entry : opaExplainers.entrySet()) {
				OPAExplainer ex = entry.getValue();	
				List<OPAFeature> features = ex.getFeatures();
				for (OPAFeature feature : features) {
					featProportions.put(feature.getKey(), feature.getCurrentValue());
				}
			}		
		} 
//		else {
//
//			try {
//				final String path = "/gov/pnnl/aim/biz/opamodel.csv";
//				final URL resURL = LinesOfBusinessRestService.class.getResource(path);
//		
//				if (resURL != null) {
//		
//					final File file = new File(resURL.getFile());
//					CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
//		
//					// For each line in the CSV
//					for (CSVRecord record : parser.getRecords()) {
//		
//						// Skip header record
//						if (record.getRecordNumber() == 1) {
//							continue;
//						}
//
//						String keyword = record.get(0);
//						String lob = (Math.random() < .5) ? "automotive" : "clothing";
//						featProportions.put(keyword + "-" + lob,  Math.random());
//					}					
//					parser.close();		
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}

		return featProportions;
	}

	/**
	 * 
	 * @param company
	 * @param lob
	 * @param nodeID
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/lob/company/records", method = RequestMethod.GET)
	public @ResponseBody RecordSet getRecords(
			@RequestParam(value = "company", required = false, defaultValue = "Ford") final String company,
			@RequestParam(value = "lob", required = false, defaultValue = "auto") final String lob,
			@RequestParam(value = "timeid", required = false, defaultValue = "1") final String timeID,
			@RequestParam(value = "id", required = false, defaultValue = "1") final String nodeID,
			final HttpServletRequest request, final HttpServletResponse response) {

		RecordSet rs = null;
		try {
			String companyName = company;
			rs = PiersRecordRetriever.getRecordSet(companyName);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (rs == null || rs.getRecords().size() == 0) {
			SimulatedData.getSimRecordSet();
		}

		return rs;
	}


	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/model/add", method = RequestMethod.GET)
	public @ResponseBody String addToModel(
			@RequestParam(value = "company", required = false, defaultValue = "") final String company,
			@RequestParam(value = "value", required = false, defaultValue = "") final String value,
			final HttpServletRequest request, final HttpServletResponse response) {

		// Send a message to ASI
		System.out.println("Received add: " + company + " -> " + value);
		
		try {
			feedbackSender.sendAddToModel(company, value);
		} catch (IOException e) {
			e.printStackTrace();
			return "Problems";
		}
		
		return "Thanks";
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/lob/company/model/remove", method = RequestMethod.GET)
	public @ResponseBody String removeFromModel(
			@RequestParam(value = "company", required = false, defaultValue = "") final String company,
			@RequestParam(value = "value", required = false, defaultValue = "") final String value,
			final HttpServletRequest request, final HttpServletResponse response) {

		// Send a message to ASI
		System.out.println("Received remove: " + company + " -> " + value);
		
		try {
			feedbackSender.sendRemoveFromModel(company, value);
		} catch (IOException e) {
			e.printStackTrace();
			return "Problems";
		}

		return "Thanks";
	}
}
