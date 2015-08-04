/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hamp645
 *
 */
public class JsonCluster implements Serializable {
    
  // For Serializable
  private static final long serialVersionUID = 4903534556675866199L;

  private final int clusterId;

  private double x;

  private double y;

  private final List<JsonClusterMember> members = new ArrayList<>();

  /** Feature vector for this cluster's centroid. */
  private double[] centroid;

  private String clusterLabel;
  

  /**
   * @param clusterId
   * @param x
   * @param y
   */
  public JsonCluster(final int clusterId, final double x, final double y) {
    this.clusterId = clusterId;
    this.x = x;
    this.y = y;
    this.clusterLabel = "";
  }

  /**
   * @param clusterId
   * @param x
   * @param y
   * @param clusterLabel
   */
  public JsonCluster(final int clusterId, final double x, final double y, final String clusterLabel) {
    this.clusterId = clusterId;
    this.x = x;
    this.y = y;
    this.clusterLabel = clusterLabel;
  }

  /**
   * @return the members
   */
  public List<JsonClusterMember> getMembers() {
    return members;
  }
  
  public int getMemberCount() {
      return members.size();
  }

  public double[] getCentroid() {
    return centroid;
  }

  public void setCentroid(double[] centroid) {
    this.centroid = centroid;
  }

  /**
   * @return the clusterId
   */
  public int getClusterId() {
    return clusterId;
  }

  /**
   * @return the x
   */
  public double getX() {
    return x;
  }
  
  public void setX(double x) {
      this.x = x;
  }
  
/**
   * @return the y
   */
  public double getY() {
    return y;
  }
  
  public void setY(double y) {
      this.y = y;
  }

  /**
   * @return the clusterLabel
   */
  public String getClusterLabel() {
    return clusterLabel;
  }

  /**
   * @param clusterLabel
   *          the clusterLabel to set
   */
  public void setClusterLabel(final String clusterLabel) {
    this.clusterLabel = clusterLabel;
  }

  /**
   * @param member
   */
  public void add(final JsonClusterMember member) {
    this.members.add(member);
    member.setClusterId(clusterId);
  }
  
  public String toString() {
      return clusterId + ": " + members;
  }
}






























