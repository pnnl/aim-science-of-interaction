package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.data.StatefulProjectLoader;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;
import gov.pnnl.aim.discovery.pojo.JsonClusterMemberHome;
import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;

import java.util.ArrayList;
import java.util.List;


public class DocumentQueue {
  private String username = null;
  private final List<JsonClusterMemberHome> data = new ArrayList<JsonClusterMemberHome>();
  private RecommendationStreamMonitor monitor = null;
  private Thread monitorThread = null;

  public DocumentQueue(final String username, final boolean useASI, final int incNumber, final StatefulProjectLoader loader) {
    this.setUsername(username);
    this.monitor = new StudyStreamMonitor(this, incNumber, loader);

    //    if (useASI) {
    //        this.monitor = new SimDocRecommendationStreamMonitor(this); //new ASIDocRecommendationStreamMonitor(this);
    //    } else {
    //        this.monitor = new PopDocRecommendationStreamMonitor(this); // new SimDocRecommendationStreamMonitor(this);
    //    }
    this.monitorThread = new Thread(monitor);
    this.monitorThread.start();
  }

  private final String []  levels = new String [] {"low", "medium", "high"};

  public void queueData(final LabeledDocumentMessage doc) {

    int level = 0;
    double r = Math.random();
    if (r < .6) {
      level = 0;
    } else if (r < .9) {
      level = 1;
    } else {
      level = 2;
    }
    level = 1;

    try {
      String docID = doc.getFile().toString();
      JsonClusterMember member = new JsonClusterMember(docID, StatefulProjectLoader.getDocTitle(docID), doc.getContent().toString(), Integer.parseInt(doc.getLabel().toString()));
      JsonClusterMemberHome home = new JsonClusterMemberHome(member, Integer.parseInt(doc.getLabel().toString()), levels[level]);

      String snippet = StatefulProjectLoader.getDocTextSnippet(docID);
      home.setSnippet(snippet);

      synchronized (this) {
        data.add(home);
        System.out.println("queueData docID = " + docID);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public List<JsonClusterMemberHome> getDataAndClearQueue() {
    List<JsonClusterMemberHome> membersReturn = new ArrayList<JsonClusterMemberHome>();
    synchronized (this) {
      membersReturn.addAll(data);
      data.clear();
      System.out.println("getDataAndClearQueue length = " + membersReturn.size());
    }
    return membersReturn;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public void start() {
    this.monitor.startMonitoring();
  }

  public void stop() {
    this.monitor.stopMonitoring();
  }

  public int size() {
    return data.size();
  }

}
