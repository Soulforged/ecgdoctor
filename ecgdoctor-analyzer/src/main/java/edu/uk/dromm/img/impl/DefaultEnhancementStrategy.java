/**
 * 
 */
package edu.uk.dromm.img.impl;

import ij.process.ImageStatistics;
import edu.uk.dromm.img.EnhancementStrategy;
import edu.uk.dromm.img.EnhancementWorkflow;

/**
 * @author dicardo
 *
 */
public class DefaultEnhancementStrategy implements EnhancementStrategy {

  /* (non-Javadoc)
   * @see edu.uk.dromm.img.EnhancementStrategy#workflow(ij.process.ImageStatistics)
   */
  @Override
  public EnhancementWorkflow workflow(final ImageStatistics is) {
    if(is.mode == 255 && is.mean > 240)
      return new ClearWorkflow();
    if(is.mean > 220)
      return new PinkStripedWorkflow();
    return new GrayscaleWorkflow();
  }

}
