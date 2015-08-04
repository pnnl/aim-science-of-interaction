/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery;

import gov.pnnl.aim.discovery.data.StatefulProjectLoader;
import gov.pnnl.aim.discovery.interactions.AddDocToClusterEvent;
import gov.pnnl.aim.discovery.interactions.BookmarkDocumentEvent;
import gov.pnnl.aim.discovery.interactions.ChangeFeatureWeightsEvent;
import gov.pnnl.aim.discovery.interactions.CreateClusterEvent;
import gov.pnnl.aim.discovery.interactions.CreateClusterWithDocEvent;
import gov.pnnl.aim.discovery.interactions.DocumentHighlightEvent;
import gov.pnnl.aim.discovery.interactions.DocumentReadEvent;
import gov.pnnl.aim.discovery.interactions.Event;
import gov.pnnl.aim.discovery.interactions.GetAnnotationEvent;
import gov.pnnl.aim.discovery.interactions.GetClusterEvent;
import gov.pnnl.aim.discovery.interactions.GetDocEvent;
import gov.pnnl.aim.discovery.interactions.GetDocWeightsEvent;
import gov.pnnl.aim.discovery.interactions.InitialEvent;
import gov.pnnl.aim.discovery.interactions.LabelClusterEvent;
import gov.pnnl.aim.discovery.interactions.LoginEvent;
import gov.pnnl.aim.discovery.interactions.MoveClusterEvent;
import gov.pnnl.aim.discovery.interactions.MoveToClusterEvent;
import gov.pnnl.aim.discovery.interactions.NewDocEvent;
import gov.pnnl.aim.discovery.interactions.NextIncrementEvent;
import gov.pnnl.aim.discovery.interactions.RemoveClusterEvent;
import gov.pnnl.aim.discovery.interactions.RemoveFromClusterEvent;
import gov.pnnl.aim.discovery.interactions.SearchEvent;
import gov.pnnl.aim.discovery.interactions.SemanticInteractionEvent;
import gov.pnnl.aim.discovery.interactions.SemanticInteractions;
import gov.pnnl.aim.discovery.interactions.UnloadDocEvent;
import gov.pnnl.aim.discovery.pojo.Annotation;
import gov.pnnl.aim.discovery.pojo.DocReadWrapper;
import gov.pnnl.aim.discovery.pojo.DocumentHighlight;
import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;
import gov.pnnl.aim.discovery.pojo.JsonClusterMemberHome;
import gov.pnnl.aim.discovery.pojo.LabelClusterWrapper;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.DocumentQueue;
import gov.pnnl.aim.discovery.streams.SpectraQueue;
import gov.pnnl.aim.discovery.streams.avro.CompoundsMessage;
import gov.pnnl.aim.discovery.util.SemanticEventLogger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author hamp645
 */
@Controller
public class CanvasRestService {

  /** Default data set name */
  public static DocumentQueue queue = null;

  public static SpectraQueue spectraQueue = null;

  /** Project loader (currently use the stateful loader */
  private StatefulProjectLoader loader;

  /** Created when the page a new page is loaded */
  private SemanticEventLogger eventLogger;

  /** Where to put the log files */
  @Value("${catalina.home}/data/canvas/logs")
  private String logDirectoryLocation;

  /** Where to put the log files */
  @Value("${catalina.home}/data/canvas/storage")
  private String serializationDirectoryLocation;


  /**
   * @return the thing
   */
  @RequestMapping(value = "/web")
  public String index() {
    return "redirect:index.html";
  }

  /** Logs one or more events, creating the event logger if necessary. */
  private void log(final SemanticInteractionEvent... events) {
    if (eventLogger == null) {
      // No logger, so create one. This case can arise when refreshing the
      // page after the server restarts, most likely in developer testing.
      File logDirectory = new File(logDirectoryLocation);
      logDirectory.mkdirs();
      System.out.println("Logging to directory: " + logDirectory.getAbsolutePath());
      eventLogger = new SemanticEventLogger(logDirectory);
    }

    for (SemanticInteractionEvent event : events) {
      eventLogger.log(event);
    }
  }

  /**
   * Logs the current feature weights and document weights. This is intended
   * to be called after an interaction that causes a feature re-weighting.
   */
  private void logFeatureAndDocumentWeights() {
    double[] featureWts = loader.getFeatureWeights();
    Map<String, Integer> docWts = loader.getDocumentWeights();
    SemanticInteractionEvent cfwe = new ChangeFeatureWeightsEvent(featureWts);
    SemanticInteractionEvent gdwe = new GetDocWeightsEvent(docWts);
    log(cfwe, gdwe);
  }

  /** Search using Lucene. */
  @RequestMapping(method = RequestMethod.POST, value = "/search")
  @ResponseBody
  public List<String> search(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String queryStr = input.get("id").getAsString();
    int maxHits = input.get("count").getAsInt();
    System.out.println("Query: " + queryStr);
    List<String> docIdList = loader.search(queryStr, maxHits);
    System.out.println("Response: " + docIdList);

    SearchEvent se = new SearchEvent(queryStr, docIdList);
    //    SemanticInteractionEvent read = se.checkIsReading();
    //    log(read, se);
    log(se);
    return docIdList;
  }

  /**
   * @param wrapper
   * @return the new json
   */
  @RequestMapping(method = RequestMethod.POST, value = "/labelCluster")
  @ResponseBody
  public List<JsonCluster> labelCluster(@RequestBody final LabelClusterWrapper wrapper) {
    int clusterId = wrapper.getClusterId();
    String label = wrapper.getLabel();

    LabelClusterEvent lce = new LabelClusterEvent(clusterId, label);
    //    SemanticInteractionEvent read = lce.checkIsReading();
    //    log(read, lce);
    log(lce);

    loader.labelCluster(clusterId, label);

    return loader.getClusterMembership();
  }

  /**
   * @param json
   * @return the new json
   */
  @RequestMapping(method = RequestMethod.POST, value = "/getCluster")
  @ResponseBody
  public JsonCluster getCluster(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    int id = input.get("id").getAsInt();

    GetClusterEvent gce = new GetClusterEvent(id);
    //    SemanticInteractionEvent read = gce.checkIsReading();
    //    log(read, gce);
    log(gce);

    return loader.getCluster(id);
  }

  // UNUSED
  // @RequestMapping(method = RequestMethod.POST, value = "/createCluster")
  // @ResponseBody
  // public int createCluster(@RequestBody final String json) {
  // JsonObject input = new JsonParser().parse(json).getAsJsonObject();
  // int id = input.get("clusterId").getAsInt();
  // String label = input.get("label").getAsString();
  // int x = input.get("x").getAsInt();
  // int y = input.get("y").getAsInt();
  //
  // CreateClusterEvent cce = new CreateClusterEvent(id, label, x, y);
  // SemanticInteractionEvent read = cce.checkIsReading();
  // if(read != null){
  // log(read);
  // }
  // log(cce);
  //
  // return loader.createNewCluster(id, label, x, y);
  // }

  /**
   * @param id
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/createNewCluster")
  @ResponseBody
  public List<JsonCluster> createNewCluster() {
    int clusterId = loader.createNewCluster("Unlabelled Group", Math.random(), Math.random());

    CreateClusterEvent ccwde = new CreateClusterEvent(clusterId);
    //    SemanticInteractionEvent read = ccwde.checkIsReading();
    //    log(read, ccwde);
    log(ccwde);

    return loader.getClusterMembership();
  }

  @RequestMapping(method = RequestMethod.POST, value = "/createClusterWithDoc")
  @ResponseBody
  public List<JsonCluster> createClusterWithDoc(@RequestBody final String id) {
    CreateClusterWithDocEvent ccwde = new CreateClusterWithDocEvent(id);
    //    SemanticInteractionEvent read = ccwde.checkIsReading();
    //    log(read, ccwde);
    log(ccwde);

    JsonClusterMember member = loader.findDoc(id);
    loader.createClusterWithDoc("Unlabelled Group", Math.random(), Math.random(), member);

    return loader.getClusterMembership();
  }


  /**
   * Highlights a range within a document.
   *
   * @return true if successful
   */
  @RequestMapping(method = RequestMethod.POST, value = "/highlightDocument")
  @ResponseBody
  public JsonClusterMember highlightDocument(@RequestBody final DocumentHighlight highlight) {
    DocumentHighlightEvent ae = new DocumentHighlightEvent(highlight);
    //    SemanticInteractionEvent read = ae.checkIsReading();
    //    log(read, ae);
    log(ae);

    JsonClusterMember doc = loader.highlightDocument(highlight);
    logFeatureAndDocumentWeights();

    return doc;
  }

  /**
   * Marks a document as 'important'.
   *
   * @return true if successful
   */
  @RequestMapping(method = RequestMethod.POST, value = "/bookmarkDocument")
  @ResponseBody
  public JsonClusterMember bookmarkDocument(@RequestBody final String docId) {
    System.out.println("Bookmarking document:" + docId);

    BookmarkDocumentEvent ae = new BookmarkDocumentEvent(docId);
    //    SemanticInteractionEvent read = ae.checkIsReading();
    //    log(read, ae);
    log(ae);

    JsonClusterMember doc = loader.bookmarkDocument(docId);
    logFeatureAndDocumentWeights();

    return doc;
  }

  /**
   * Highlights a range within a document.
   *
   * @param docId
   * @param readCount
   *
   * @return true if successful
   */
  @RequestMapping(method = RequestMethod.POST, value = "/documentRead")
  @ResponseBody
  public JsonClusterMember documentRead(@RequestBody final DocReadWrapper wrapper) {
    System.out.println("Marking document as read:" + wrapper.getDocId() + " " + wrapper.getReadCount() + " times.");

    DocumentReadEvent ae = new DocumentReadEvent(wrapper.getDocId(), wrapper.getReadCount());
    //    SemanticInteractionEvent read = ae.checkIsReading();
    //    log(read, ae);
    log(ae);

    JsonClusterMember doc = loader.readDocument(wrapper.getDocId(), wrapper.getReadCount());
    logFeatureAndDocumentWeights();

    return doc;
  }

  /**
   * Gets a document's annotation.
   *
   * @return Annotation (possibly empty, but never null)
   */
  @RequestMapping(method = RequestMethod.POST, value = "/getAnnotation")
  @ResponseBody
  public List<Annotation> getAnnotation(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String docId = input.get("docId").getAsString();

    GetAnnotationEvent gae = new GetAnnotationEvent(docId);
    //    SemanticInteractionEvent read = gae.checkIsReading();
    //    log(read, gae);
    log(gae);

    return loader.getAnnotations(docId);
  }

  /** Moves a document from one cluster to another. */
  @RequestMapping(method = RequestMethod.POST, value = "/moveToCluster")
  @ResponseBody
  public boolean moveToCluster(@RequestBody final String json) {
    System.out.println("MoveToCluster" + json);
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String docId = input.get("docId").getAsString();
    int oldClusterId = input.get("oldClusterId").getAsInt();
    int newClusterId = input.get("newClusterId").getAsInt();

    MoveToClusterEvent mtce = new MoveToClusterEvent(docId, oldClusterId, newClusterId);
    //    SemanticInteractionEvent read = mtce.checkIsReading();
    //    log(read, mtce);
    log(mtce);

    boolean moveMade = loader.moveToCluster_ASI(docId, oldClusterId, newClusterId);
    if (moveMade) {
      logFeatureAndDocumentWeights();
    }

    return moveMade;
  }

  /** Records the repositioning of a cluster. */
  @RequestMapping(method = RequestMethod.POST, value = "/moveCluster")
  @ResponseBody
  public void moveCluster(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    int clusterId = input.get("clusterId").getAsInt();
    double x = input.get("x").getAsDouble();
    double y = input.get("y").getAsDouble();

    MoveClusterEvent mce = new MoveClusterEvent(clusterId, x, y);
    //    SemanticInteractionEvent read = mce.checkIsReading();
    //    log(read, mce);
    log(mce);

    loader.moveCluster(clusterId, x, y);
  }

  /** Creates a new document object and adds it to a cluster. */
  @RequestMapping(method = RequestMethod.POST, value = "/addDocToCluster")
  @ResponseBody
  public boolean addDocToCluster(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String docId = input.get("docId").getAsString();
    String title = input.get("title").getAsString();
    String docText = input.get("docText").getAsString();
    int clusterId = input.get("clusterId").getAsInt();

    AddDocToClusterEvent adtce = new AddDocToClusterEvent(docId, docText, title, clusterId);
    //    SemanticInteractionEvent read = adtce.checkIsReading();
    //    log(read, adtce);
    log(adtce);

    JsonClusterMember doc = new JsonClusterMember(docId, title, docText, clusterId);
    return loader.addDocToCluster_ASI(doc, clusterId);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/removeFromCluster")
  @ResponseBody
  public List<JsonCluster> removeFromCluster(@RequestBody final String id) {
    System.out.println("Remove from cluster input: " + id);
    JsonObject input = new JsonParser().parse(id).getAsJsonObject();
    String idString = input.get("id").getAsString();
    loader.moveDocToUnused_ASI(idString);

    RemoveFromClusterEvent rfce = new RemoveFromClusterEvent(idString);
    //    SemanticInteractionEvent read = rfce.checkIsReading();
    //    log(read, rfce);
    log(rfce);

    return loader.getClusterMembership();
  }

  /**
   * Gets all document weights based on the current boosted weighting.
   *
   * @return Map of document ID to weight (0=smallest to 3=largest)
   */
  @RequestMapping(method = RequestMethod.POST, value = "/getDocumentWeights")
  @ResponseBody
  public Map<String, Integer> getDocumentWeights() {

    Map<String, Integer> map = loader.getDocumentWeights();
    GetDocWeightsEvent gdwe = new GetDocWeightsEvent(map);
    //    SemanticInteractionEvent read = gdwe.checkIsReading();
    //    log(read, gdwe);
    log(gdwe);

    return map;
  }

  /**
   * Gets all document weights based on the current boosted weighting.
   *
   * @return Map of document ID to weight (0=smallest to 3=largest)
   */
  @RequestMapping(method = RequestMethod.POST, value = "/updateDocumentWeights")
  @ResponseBody
  public List<JsonCluster> updateDocumentWeights() {
    loader.updateDocumentWeights();

    return loader.getClusterMembership();
  }

  // UNUSED
  @RequestMapping(method = RequestMethod.POST, value = "/removeCluster")
  @ResponseBody
  public boolean removeCluster(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    int clusterID = input.get("clusterID").getAsInt();

    RemoveClusterEvent rce = new RemoveClusterEvent(clusterID);
    //    SemanticInteractionEvent read = rce.checkIsReading();
    //    log(read, rce);
    log(rce);

    return loader.removeCluster_ASI(clusterID);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/scroll")
  @ResponseBody
  public void logScrollEvent(@RequestParam(value = "location") final String location) {
    Event se;
    if (location.equals("list")) {
      se = new Event(SemanticInteractions.SCROLL_LIST);
      log(se);
    } else if (location.equals("doc")) {
      se = new Event(SemanticInteractions.SCROLL_DOC);
      log(se);
    }
    System.out.println("Scrolled " + location);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/siteClick")
  @ResponseBody
  public void logClickEvent(@RequestParam(value = "area") final String area) {
    Event ce;
    if (area.equals("list")) {
      ce = new Event(SemanticInteractions.LIST_CLICK);
      log(ce);
    } else if (area.equals("doc")) {
      ce = new Event(SemanticInteractions.DOC_CLICK);
      log(ce);
    } else if (area.equals("canvas")) {
      ce = new Event(SemanticInteractions.CANVAS_CLICK);
      log(ce);
    }
    //    System.out.println("Clicked " + area);
  }

  /**
   * @param json
   *          JSON containing the id of the document
   * @return the document text
   */
  @RequestMapping(method = RequestMethod.POST, value = "/fullDocument")
  @ResponseBody
  public String getFullDocument(@RequestBody final String json) {
    System.out.println("GetFullDocument: " + json);
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String docId = input.get("id").getAsString();
    System.out.println("ID: " + docId);

    String docText = "";
    try {
      docText = loader.getFullDocument(docId);
    } catch (IOException e) {
      docText = e.getMessage();
      e.printStackTrace();
    }

    // Highlight the features in the document text
    // List<String> features = loader.getFeatureLabels();
    // docText = Highlighter.highlight(docText, features);

    GetDocEvent gde = new GetDocEvent(docId, docText);
    //    SemanticInteractionEvent read = gde.checkIsReading();
    //    StartReadingEvent sre = new StartReadingEvent(docId);
    //    log(read, gde, sre);
    log(gde);

    Gson gson = new Gson();
    JsonObject o = new JsonObject();
    o.addProperty("data", "<div>" + docText + "</div>");
    o.addProperty("annotation", loader.getAnnotations(docId).toString());
    return gson.toJson(o);
  }

  /**
   * @param id
   *          the id of the document
   * @return the document text
   */
  @RequestMapping(method = RequestMethod.POST, value = "/getDocument")
  @ResponseBody
  public JsonClusterMember getDocument(@RequestBody final String id) {
    System.out.println("Input: " + id);
    return loader.findDoc(id);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/unloadDocument")
  @ResponseBody
  public void unloadDocument(@RequestBody final String name) {
    System.out.println(name + " document unloaded");

    UnloadDocEvent ude = new UnloadDocEvent(name);
    //    SemanticInteractionEvent read = ude.checkIsReading();
    //    log(read, ude);
    log(ude);
  }

  /**
   * @return the json for the initial data
   */
  @RequestMapping(method = RequestMethod.GET, value = "/latest")
  @ResponseBody
  public List<JsonCluster> getLatestData() {
    return loader.getClusterMembership();
  }

  /**
   * Requests the next increment of data.
   *
   * @return The updated state of the data
   */
  @RequestMapping(method = RequestMethod.GET, value = "/next")
  @ResponseBody
  public List<JsonCluster> getNextIncrement() {
    List<JsonCluster> clusters = loader.loadNextIncrement();

    int incrementNumber = loader.getCurrentIncrement();
    Map<String, Integer> incrementalClusterAssignments = loader.getIncrementalClusterAssignments();
    SemanticInteractionEvent nie = new NextIncrementEvent(incrementNumber, incrementalClusterAssignments);
    Map<String, Integer> docWts = loader.getDocumentWeights();
    GetDocWeightsEvent gdwe = new GetDocWeightsEvent(docWts);
    log(nie, gdwe);

    return clusters;
  }

  /**
   * @return the json for the initial data
   */
  @RequestMapping(method = RequestMethod.GET, value = "/initial")
  @ResponseBody
  public List<JsonCluster> getInitialJson(@RequestParam("username") final String username, final HttpServletRequest request, final HttpServletResponse response) {
    File logDirectory = new File(logDirectoryLocation);
    logDirectory.mkdirs();

    File serializationDirectory = new File(serializationDirectoryLocation);
    serializationDirectory.mkdirs();

    StatefulProjectLoader.setSerializationDir(serializationDirectoryLocation);

    System.out.println("Logging to directory: " + logDirectory.getAbsolutePath());
    System.out.println("Serialization directory: " + serializationDirectory.getAbsolutePath());

    String uid = username;

    if (uid == null || uid.length() == 0) {
      uid = "USER";
    }

    System.out.println("uid = " + uid);

    eventLogger = new SemanticEventLogger(logDirectory);

    loader = StatefulProjectLoader.loadProject(CanvasConfig.PROJECT_NAME, uid);
    Map<String, Integer> map = loader.getDocumentWeights();
    GetDocWeightsEvent gdwe = new GetDocWeightsEvent(map);

    InitialEvent bse = new InitialEvent(loader.getClusterMembership());
    log(bse, gdwe);
    System.out.println(loader.getClusterMembership());
    return loader.getClusterMembership();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/exit")
  @ResponseBody
  public void setEndSession() {
    // Not currently in a session
    if (loader == null)
      return;

    try {
      System.out.println("Ending Session");

      Event ese = new Event(SemanticInteractions.END_SESSION);
      //      SemanticInteractionEvent read = ese.checkIsReading();
      //      log(read, ese);
      log(ese);
      stopStreamMonitor();
      loader.endSession();
    } catch (NullPointerException e) {
      System.out.print("Object could not be created from REST call before Java initialized.");
      e.printStackTrace();
    }
  }

  /**
   * This series of services supports interaction with a document stream
   */

  /**
   * Retrieve new documents queued up from monitoring the stream
   *
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = "/newdata", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String getNewDataJson() {
    ensureQueue();
    List<JsonClusterMemberHome> o = CanvasRestService.queue.getDataAndClearQueue();
    Gson gson = new Gson();

    NewDocEvent nde = new NewDocEvent(o);
    //    SemanticInteractionEvent read = nde.checkIsReading();
    Map<String, Integer> map = loader.updateDocumentWeights();
    GetDocWeightsEvent gdwe = new GetDocWeightsEvent(map);
    //    log(read, nde, gdwe);
    log(nde, gdwe);

    return gson.toJson(o);
  }

  /**
   * Start monitoring the stream to populate the queue
   *
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String startStreamMonitor() {
    ensureQueue();
    CanvasRestService.queue.start();

    Event ste = new Event(SemanticInteractions.START_STREAM);
    //    SemanticInteractionEvent read = ste.checkIsReading();
    //    log(read, ste);
    log(ste);

    return "";
  }

  /**
   * Stop monitoring the stream so the queue doesn't keep accumulating records
   *
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String stopStreamMonitor() {
    ensureQueue();
    CanvasRestService.queue.stop();

    Event stopse = new Event(SemanticInteractions.END_STREAM);
    //    SemanticInteractionEvent read = stopse.checkIsReading();
    //    log(read, stopse);
    log(stopse);

    return "";
  }

  /**
   * Redirect style service to support for authentication to connect to the ASI
   *
   * @param username
   * @param password
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/auth")
  public String auth(@RequestParam("username") final String username, @RequestParam("password") final String password) {
    try {
      ASIConnector.initConnector(username, password);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // CanvasRestService.queue = new DocumentQueue(username, false);

    return "redirect:index.html";
  }

  /**
   * AJAX style service to support for authentication to connect to the ASI
   *
   * @param username
   * @param password
   * @return
   */
  @RequestMapping(method = RequestMethod.POST, value = "/login")
  @ResponseBody
  public String login(@RequestBody final String json) {
    JsonObject input = new JsonParser().parse(json).getAsJsonObject();
    String username = input.get("username").getAsString();
    String password = input.get("password").getAsString();
    System.out.println(username + " logged in");
    try {
      ASIConnector.initConnector(username, password);
      loader.setUser(username);
      CanvasConfig.STREAM_TO_ASI = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    CanvasRestService.queue = new DocumentQueue(username, false, loader.getCurrentIncrement(), loader);

    LoginEvent le = new LoginEvent(username);
    //    SemanticInteractionEvent read = le.checkIsReading();
    //    log(read, le);
    log(le);

    return "{\"message\":\"OK\"}";
  }

  /**
   *
   */

  @RequestMapping(method = RequestMethod.GET, value = "/nmr")
  public String nmr() {
    return "redirect:spectra.html";
  }

  @RequestMapping(method = RequestMethod.GET, value = "/newspectra", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String getNewDataSpectra() {
    ensureSpectraQueue();
    List<CompoundsMessage> o = CanvasRestService.spectraQueue.getNextData();
    Gson gson = new Gson();
    return gson.toJson(o);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/compoundnames", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<String> getCompoundDictionary() {
    String[] values = new String[] { "sucrose", "hexadecane", "L-asparagine", "1,4-Cyclohexanedione", "creatinine", "beta-Mannosylglycerate", "Ala-Ala", "D-Ala-D-Ala", "nicotinamide (Nicotinacid-amide)", "Acylcarnitine C18:3", "B-glycerolphosphate (beta-Glycerophosphoric acid)", "glycine-d5 deuterated", "glycine", "mono(2-ethylhexyl)phthalate", "4-hydroxy-3-methoxycinnamaldehyde", "2-Butyne-1,4-diol", "prunin", "melibiose", "isomaltose", "Melibiose", "3-indoleacetonitrile", "ergosterol", "estrone", "PC(20:4(5Z,8Z,11Z,14Z)/18:0)", "PE(18:2(9Z,12Z)/18:1(9Z))", "melatonin", "uridine", "D-saccharic acid", "N-epsilon-Acetyl-L-lysine", "linolenic acid", "phenylacetaldehyde" };
    return Lists.newArrayList(values);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/sampletypes", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<String> getSampleTypesDictionary() {
    String[] values = new String[] { "Urine", "Blood", "Soil" };
    return Lists.newArrayList(values);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/confidentcompound", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String receiveConfidentCompound(@RequestParam("c") final String confCompound) {
    System.out.println("receiveConfidentCompound = " + confCompound);
    return "";
  }

  /**
   *
   */

  private void ensureQueue() {
    if (CanvasRestService.queue == null) {
      System.out.println("Creating queue...");
      CanvasRestService.queue = new DocumentQueue("demo", true, loader.getCurrentIncrement(), loader);
    }
  }

  private void ensureSpectraQueue() {
    if (CanvasRestService.spectraQueue == null) {
      System.out.println("Creating spectra weqqueue...");
      CanvasRestService.spectraQueue = new SpectraQueue("demo");
    }
  }

}
