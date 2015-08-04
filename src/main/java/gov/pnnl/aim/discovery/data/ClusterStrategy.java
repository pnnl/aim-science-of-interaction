/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import java.util.Set;

import gov.pnnl.jac.cluster.Cluster;
import gov.pnnl.jac.cluster.ClusterList;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author hamp645
 * 
 */
public abstract class ClusterStrategy extends BaseStrategy {
  /**
   * @param properties
   */
  public ClusterStrategy(final MatrixProperties properties) {
    super(properties);
  }

  /**
   * 
   */
  public ClusterStrategy() {
    super();
  }
  
  /**
   * @return the cluster list
   */
  public abstract ClusterList cluster();

  /**
   * 
   * @param rows
   * @return
   */
  public abstract ClusterList cluster(Set<Integer> rows);

  /**
   * @return the clusters
   */
  public abstract ClusterList getClusters();

  /**
   * @return the matrix projected into 2-space
   */
  public abstract RealMatrix getTwoSpaceMatrix();

  /**
   * @param c
   * @return the representive member id
   */
  public abstract int getRepresentativeMember(final Cluster c);

  /**
   * 
   * @param c
   * @return
   */
  public abstract int mapRowID(final int rowID);
}