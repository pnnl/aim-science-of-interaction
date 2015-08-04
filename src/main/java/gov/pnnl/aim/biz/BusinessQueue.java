package gov.pnnl.aim.biz;

import gov.pnnl.aim.af.avro.ModelMessage;
import gov.pnnl.aim.af.avro.ProportionsMessage;
import gov.pnnl.aim.avro.dint.BasicOPAPiersLOBMessage;
import gov.pnnl.aim.avro.dint.BasicPOPPiersLOBMessage;
import gov.pnnl.aim.avro.dint.BasicShyrePiersLOBMessage;
import gov.pnnl.aim.avro.dint.OPAPiersLOBMessage;
import gov.pnnl.aim.avro.dint.POPPiersLOBMessage;
import gov.pnnl.aim.avro.dint.ShyrePiersLOBMessage;
import gov.pnnl.aristotle.aiminterface.NousAnswerStreamRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BusinessQueue {

	private final List<BasicShyrePiersLOBMessage> shyreData = new ArrayList<BasicShyrePiersLOBMessage>();
	private final List<BasicOPAPiersLOBMessage> opaData = new ArrayList<BasicOPAPiersLOBMessage>();
	private final List<BasicPOPPiersLOBMessage> popData = new ArrayList<BasicPOPPiersLOBMessage>();
	private LOBModelState modelState = null;
	private List<String> featureNames = new ArrayList<String>();
	private Map<String, ModelMessage> opaModels = new HashMap<String, ModelMessage>();
	
	public BusinessQueue(LOBModelState modelState) {
		this.modelState = modelState;
		buildFeatureNamesList();
	}
	
	/**
	 * 
	 * @param msg
	 */

	public void queueNousShyreData(final NousAnswerStreamRecord msg) {

		String companyName = fix(msg.getCompany().toString());
		CompanyState state = modelState.getCompanyState(companyName);
		
		List<String> hscodes = msg.getHscodes();
		List<String> codes = new ArrayList<String>();
		
		for (CharSequence s : hscodes) {
			codes.add(s.toString());
		}
		
		ShyreExplainer explainer = new ShyreExplainer();
		explainer.setLineOfBusiness(msg.getLob().toString());
		explainer.setCompanyName(msg.getExamplarCompany().toString());
		explainer.setCodeCount(codes.size());
		explainer.setTotalCount(msg.getExpectedCount().intValue());
		explainer.setCodes(codes);
		explainer.setDescriptions(HSCodeLookup.lookup(codes));
		
		Map<String, ShyreExplainer> explainers = new HashMap<String, ShyreExplainer>();
		explainers.put("0", explainer);
		
		state.setTimeToShyreExplainer(explainers);
		
		String lob = msg.getLob().toString();
		int lobIndex = modelState.getIndex(lob);
		state.setShyreProbability(msg.getIsHighScore(), lobIndex, msg.getProbability());

		KnowledgeContext context = new KnowledgeContext();
		context.setInfobox(msg.getInfobox());
		context.setPaths(msg.getPaths());
		context.setProfile(msg.getProfile());
		state.setContext(context); 
		
		System.out.println("SHYRE: Company: " + companyName + ", LOB: " + lob + ", Probability: " + msg.getProbability() + " Max: " + state.getShyreProbabilities()[lobIndex] + " isHigh: " + msg.getIsHighScore() + " high: " + msg.getHighScore());
		System.out.println("NOUS: Infobox: " + msg.getInfobox().replaceAll("\\n", " ") + ", Paths: " + msg.getPaths().size() + ", Profile: " + msg.getProfile().size());
	}
	
	public void queueShyreData(final ShyrePiersLOBMessage msg) {

		String companyName = fix(msg.getCompany().toString());
		CompanyState state = modelState.getCompanyState(companyName);
		
		List<CharSequence> hscodes = msg.getHscodes();
		List<String> codes = new ArrayList<String>();
		
		for (CharSequence s : hscodes) {
			codes.add(s.toString());
		}
		
		ShyreExplainer explainer = new ShyreExplainer();
		explainer.setLineOfBusiness(msg.getLob().toString());
		explainer.setCompanyName(msg.getExamplarCompany().toString());
		explainer.setCodeCount(codes.size());
		explainer.setTotalCount(msg.getExpectedCount().intValue());
		explainer.setCodes(codes);
		explainer.setDescriptions(HSCodeLookup.lookup(codes));
		
		Map<String, ShyreExplainer> explainers = new HashMap<String, ShyreExplainer>();
		explainers.put("0", explainer);
		
		state.setTimeToShyreExplainer(explainers);
		
		String lob = msg.getLob().toString();
		int lobIndex = modelState.getIndex(lob);
		state.setShyreProbability(msg.getHighScore(), lobIndex, msg.getProbability());

		System.out.println("SHYRE: Company: " + companyName + ", LOB: " + lob + ", Probability: " + msg.getProbability() + " Max: " + state.getShyreProbabilities()[lobIndex]);
	}
	
	public void queueOPAData(final OPAPiersLOBMessage msg) {

		String companyName = fix(msg.getCompany().toString());
		CompanyState state = modelState.getCompanyState(companyName);
		
		String probsList = "";
		double [] probs = new double[msg.getProbabilities().size()];
		for (int i = 0; i < probs.length; i++) {
			probs[i] = msg.getProbabilities().get(i);
			probsList += probs[i] + " ";
		}
		state.setOPAProbabilities(probs);

		System.out.println("OPA: Company: " + companyName + ", Probabilities: [" + probsList + "]");
	}

	public void queueOPAData(ModelMessage msg) {
		String lob = msg.getLOB().toString().toLowerCase();
		this.opaModels.put(lob, msg);

		System.out.println("OPA Model: LOB: " + lob + ", Size:" + msg.getInfluentialness().size());
	}

	public void queueOPAData(ProportionsMessage msg) {
		
		String companyName = fix(msg.getCompany().toString());
		CompanyState state = modelState.getCompanyState(companyName);

		System.out.println("OPA Proportions: Company: " + companyName + ", Size:" + msg.getProportions().size());
		
		Map<String, OPAExplainer> explainerMap = new HashMap<String, OPAExplainer>();
		
		for (Entry<String, ModelMessage> entry : opaModels.entrySet()) {
			
			String lob = entry.getKey();
			ModelMessage opaModel = entry.getValue();
			
			List<OPAFeature> features = new ArrayList<OPAFeature>();
			
			List<Double> influentialness = opaModel.getInfluentialness();
			List<Double> noMean = opaModel.getNoMean();
			List<Double> noStdev = opaModel.getNoStdev();
			List<Double> yesMean = opaModel.getYesMean();
			List<Double> yesStdev = opaModel.getYesStdev();
			List<Double> proportions = msg.getProportions();
			
			for (int i = 0; i < featureNames.size(); i++) {
				
				String name = featureNames.get(i);
				double importance = influentialness.get(i);
				double outMean = noMean.get(i);
				double outStdDev = noStdev.get(i);
				double inMean = yesMean.get(i);
				double inStdDev = yesStdev.get(i);
				double currentValue = proportions.get(i);
				
				OPAFeature feature = new OPAFeature(name, lob, importance, inMean, inStdDev, outMean, outStdDev, currentValue);						
				features.add(feature);
			}
			
			OPAExplainer explainer = new OPAExplainer();
			explainer.setFeatures(features);
						
			explainerMap.put(lob, explainer);
		}
		state.setTimeToOPAExplainer(explainerMap);		
    }

	public void queuePOPData(final POPPiersLOBMessage msg) {
		
		String company = fix(msg.getCompany().toString());
		CompanyState state = modelState.getCompanyState(company);
		state.setAlert(true);

		System.out.println("POP: Company: " + company + ", LOB: " + msg.getLinesofbusiness());
	}
	  
	private String fix(String company) {
		if (company.contains("_")) {
			company = company.split("_")[0];
		}
		return company;
	}
	
	
	/**
	 * 
	 * @return
	 */
  public List<BasicShyrePiersLOBMessage> getShyreDataAndClearQueue() {
    List<BasicShyrePiersLOBMessage> membersReturn = new ArrayList<BasicShyrePiersLOBMessage>();
    synchronized (this) {
      membersReturn.addAll(shyreData);
      popData.clear();
      System.out.println("getDataAndClearQueue SHYRE length = " + membersReturn.size());
    }
    return membersReturn;
  }

  public List<BasicOPAPiersLOBMessage> getOPADataAndClearQueue() {
    List<BasicOPAPiersLOBMessage> membersReturn = new ArrayList<BasicOPAPiersLOBMessage>();
    synchronized (this) {
      membersReturn.addAll(opaData);
      opaData.clear();
      System.out.println("getDataAndClearQueue OPA length = " + membersReturn.size());
    }
    return membersReturn;
  }
  
  public List<BasicPOPPiersLOBMessage> getPOPDataAndClearQueue() {
    List<BasicPOPPiersLOBMessage> membersReturn = new ArrayList<BasicPOPPiersLOBMessage>();
    synchronized (this) {
      membersReturn.addAll(popData);
      popData.clear();
      System.out.println("getDataAndClearQueue POP length = " + membersReturn.size());
    }
    return membersReturn;
  }

  private void buildFeatureNamesList() {
	  featureNames.add("EMDEN");
	  featureNames.add("YOKOSUKA");
	  featureNames.add("PARANAGUA");
	  featureNames.add("KOBE");
	  featureNames.add("NAGOYA");
	  featureNames.add("PTQASIM");
	  featureNames.add("SANTOS");
	  featureNames.add("GIOIATAURO");
	  featureNames.add("ROTTERDAM");
	  featureNames.add("SANJUAN");
	  featureNames.add("YOKOHAMA");
	  featureNames.add("TOKYO");
	  featureNames.add("STOTOMAS");
	  featureNames.add("PTSAID");
	  featureNames.add("HSINKANG");
	  featureNames.add("BALBOA");
	  featureNames.add("BREMERHAVEN");
	  featureNames.add("BUSAN");
	  featureNames.add("COLOMBO");
	  featureNames.add("HONGKONG");
	  featureNames.add("KAOHSIUNG");
	  featureNames.add("NINGPO");
	  featureNames.add("SALALAH");
	  featureNames.add("SHANGHAI");
	  featureNames.add("SINGAPORE");
	  featureNames.add("TJPELEPAS");
	  featureNames.add("YANTIAN");
	  featureNames.add("GREENVILLE-SPTBURG");
	  featureNames.add("PTHUENEME");
	  featureNames.add("WPALMBCH");
	  featureNames.add("BALTIMORE");
	  featureNames.add("NEWORLEANS");
	  featureNames.add("PHILADELPHIA");
	  featureNames.add("PORTLANDOR");
	  featureNames.add("HOUSTON");
	  featureNames.add("JACKSONVILLE");
	  featureNames.add("PTEVERGLADES");
	  featureNames.add("CHARLESTON");
	  featureNames.add("LONGBEACH");
	  featureNames.add("LOSANGELES");
	  featureNames.add("MIAMI");
	  featureNames.add("NEWYORK");
	  featureNames.add("SANJUAN");
	  featureNames.add("SAVANNAH");
	  featureNames.add("SEATTLE");
	  featureNames.add("TACOMA");
	  featureNames.add("VANCOUVERBC");
	  featureNames.add("addidas");
	  featureNames.add("apparel");
	  featureNames.add("apples");
	  featureNames.add("appliance");
	  featureNames.add("assymetrical");
	  featureNames.add("audi");
	  featureNames.add("auto");
	  featureNames.add("axle");
	  featureNames.add("ball");
	  featureNames.add("beach");
	  featureNames.add("bedding");
	  featureNames.add("belt");
	  featureNames.add("bicycle");
	  featureNames.add("blaser");
	  featureNames.add("bmw");
	  featureNames.add("brake");
	  featureNames.add("breaker");
	  featureNames.add("briefs");
	  featureNames.add("caps");
	  featureNames.add("chair");
	  featureNames.add("circuit");
	  featureNames.add("ckd");
	  featureNames.add("comforter");
	  featureNames.add("commodity");
	  featureNames.add("compressor");
	  featureNames.add("computer");
	  featureNames.add("conditinoner");
	  featureNames.add("control");
	  featureNames.add("cook");
	  featureNames.add("cotton");
	  featureNames.add("denim");
	  featureNames.add("dining");
	  featureNames.add("disc");
	  featureNames.add("dishwasher");
	  featureNames.add("dress");
	  featureNames.add("dryer");
	  featureNames.add("engine");
	  featureNames.add("eqpt");
	  featureNames.add("footwear");
	  featureNames.add("freezer");
	  featureNames.add("furniture");
	  featureNames.add("garment");
	  featureNames.add("gazebo");
	  featureNames.add("genuine");
	  featureNames.add("girl");
	  featureNames.add("golf");
	  featureNames.add("grill");
	  featureNames.add("hanging");
	  featureNames.add("hermetic");
	  featureNames.add("honda");
	  featureNames.add("house");
	  featureNames.add("infant");
	  featureNames.add("iron");
	  featureNames.add("jacket");
	  featureNames.add("jeans");
	  featureNames.add("kd");
	  featureNames.add("knit");
	  featureNames.add("linen");
	  featureNames.add("men");
	  featureNames.add("microwave");
	  featureNames.add("module");
	  featureNames.add("monitor");
	  featureNames.add("motor");
	  featureNames.add("motorbike");
	  featureNames.add("nike");
	  featureNames.add("nissan");
	  featureNames.add("outdoor");
	  featureNames.add("oven");
	  featureNames.add("pajamas");
	  featureNames.add("pant");
	  featureNames.add("panties");
	  featureNames.add("passenger");
	  featureNames.add("patio");
	  featureNames.add("pillow");
	  featureNames.add("poly");
	  featureNames.add("pool");
	  featureNames.add("pts");
	  featureNames.add("pullovers");
	  featureNames.add("recliner");
	  featureNames.add("refrigerator");
	  featureNames.add("school");
	  featureNames.add("sensor");
	  featureNames.add("set");
	  featureNames.add("shirt");
	  featureNames.add("shoe");
	  featureNames.add("short");
	  featureNames.add("soccer");
	  featureNames.add("sofa");
	  featureNames.add("sport");
	  featureNames.add("steel");
	  featureNames.add("sweats");
	  featureNames.add("tab");
	  featureNames.add("tent");
	  featureNames.add("terrain");
	  featureNames.add("terry");
	  featureNames.add("tire");
	  featureNames.add("tissue");
	  featureNames.add("toy");
	  featureNames.add("toyota");
	  featureNames.add("trouser");
	  featureNames.add("truck");
	  featureNames.add("tshirt");
	  featureNames.add("underwear");
	  featureNames.add("upholstered");
	  featureNames.add("valve");
	  featureNames.add("vehicle");
	  featureNames.add("viscose");
	  featureNames.add("volkswagen");
	  featureNames.add("washer");
	  featureNames.add("women");
	  featureNames.add("woven");
  }
}
