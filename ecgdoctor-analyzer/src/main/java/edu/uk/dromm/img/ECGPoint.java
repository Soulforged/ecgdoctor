/**
 *
 */
package edu.uk.dromm.img;

import java.awt.Point;

/**
 * @author magian
 *
 */
public class ECGPoint extends Point {
  private static final long serialVersionUID = -5691847930011897169L;

  /**
   * @param x
   * @param y
   */
  public ECGPoint(final int x, final int y) {
    super(x, y);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}
