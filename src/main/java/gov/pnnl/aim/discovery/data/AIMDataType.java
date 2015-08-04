/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hamp645
 *
 */
public enum AIMDataType implements VertexType {
  IMAGE, TEXT, VIDEO, NONE, UNKNOWN;

  @Override
  public boolean isRootType() {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends VertexType> Set<T> possibleChildren(final T type) {
    return (Set<T>) new HashSet<AIMDataType>();
  }
}
