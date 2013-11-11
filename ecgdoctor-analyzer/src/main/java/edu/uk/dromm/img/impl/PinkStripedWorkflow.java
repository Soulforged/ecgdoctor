/**
 * 
 */
package edu.uk.dromm.img.impl;

import ij.plugin.filter.OwnFFTFilter;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import edu.uk.dromm.img.EnhancementWorkflow;
import edu.uk.dromm.img.Factory;
import edu.uk.dromm.img.ImageParameterProvider;

/**
 * @author dicardo
 *
 */
public class PinkStripedWorkflow implements EnhancementWorkflow {

  private final ImageParameterProvider ipp;

  /**
   * 
   */
  public PinkStripedWorkflow() {
    ipp = new Factory().getImageParameterProvider();
  }

  /* (non-Javadoc)
   * @see edu.uk.dromm.img.EnhancementWorkflow#execute(ij.process.ImageProcessor)
   */
  @Override
  public void execute(final ByteProcessor ip) {
    final OwnFFTFilter fft = new OwnFFTFilter();
    fft.filter(ip, 5, 3, 2, 5);
    fft.filter(ip, 5, 3, 1, 5);

    final AutoThresholder thresholder = new AutoThresholder();
    final int threshold = thresholder.getThreshold(
        ipp.thresholdMethod(ip.getStatistics()), ip.getHistogram());
    ip.threshold(threshold);

    ip.skeletonize();
  }

}
