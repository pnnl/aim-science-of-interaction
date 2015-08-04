/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import java.util.Set;

/**
 * @author hamp645
 *
 */
public interface VertexType{
  /**
   * @param type
   * @return the possible child types
   */
  public <T extends VertexType> Set<T> possibleChildren(final T type);

  /**
   * @return true if this type is a root type
   */
  public boolean isRootType();
}
