/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

/**
 * @author hamp645
 *
 */
public interface MatrixInitializer {
  /**
   * Initializes the matrix
   * @return the properties object
   * @throws Exception
   */
  MatrixProperties initialize() throws Exception;
}
