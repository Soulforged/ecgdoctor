/**
 *
 */
package edu.uk.dromm.img.impl;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.OwnFFTFilter;
import ij.process.ByteProcessor;
import edu.uk.dromm.img.EnhancementWorkflow;

/**
 * @author dicardo
 *
 */
public class PinkStripedWorkflow implements EnhancementWorkflow {

  /**
   *
   */
  public PinkStripedWorkflow() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.uk.dromm.img.EnhancementWorkflow#execute(ij.process.ImageProcessor)
   */
  @Override
  public void execute(final ByteProcessor ip) {
    final OwnFFTFilter fft = new OwnFFTFilter();
    fft.filter(ip, 3, 3, 2, 5);
    fft.filter(ip, 3, 3, 1, 5);

    final ImagePlus imagePlus = new ImagePlus("", ip);
    final BackgroundSubtracter bs = new BackgroundSubtracter();
    bs.setup("", imagePlus);
    bs.rollingBallBackground(ip, 0.1d, false, true, true, false, false);

    ip.autoThreshold();

    ip.skeletonize();
    for (int i = 0; i < 10; i++)
      ip.convolve3x3(new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0 });
  }

}
