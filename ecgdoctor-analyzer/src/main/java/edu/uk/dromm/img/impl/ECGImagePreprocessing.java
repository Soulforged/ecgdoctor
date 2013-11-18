package edu.uk.dromm.img.impl;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.AutoThresholder;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;

import edu.uk.dromm.img.EnhancementStrategy;
import edu.uk.dromm.img.Factory;
import edu.uk.dromm.img.ImageParameterProvider;
import edu.uk.dromm.img.ImageProcess;

public class ECGImagePreprocessing implements ImageProcess {

  private final ImageParameterProvider ipp;
  private final EnhancementStrategy es;

  public ECGImagePreprocessing() {
    final Factory factory = new Factory();
    ipp = factory.getImageParameterProvider();
    es = factory.getEnhancementStrategy();
  }

  @Override
  public BufferedImage process(final BufferedImage bi) {
    final ImageProcessor ip = new ColorProcessor(bi);
    final BinaryProcessor bp = new BinaryProcessor(
        (ByteProcessor) ip.convertToByte(false));
    es.workflow(bp.getStatistics()).execute(bp);;
    return bp.getBufferedImage();
  }

  class HistogramFilter implements PlugInFilter {

    @Override
    public void run(final ImageProcessor ip) {
      final BinaryProcessor bp = (BinaryProcessor) ip;
      final AutoThresholder thresholder = new AutoThresholder();
      int threshold = thresholder.getThreshold(ipp.thresholdMethod(bp.getStatistics()), bp.getHistogram());
      bp.threshold(threshold);
      for(int i = 0; i < 5; i++)
        bp.filter(ImageProcessor.BLUR_MORE);
      threshold = thresholder.getThreshold(ipp.thresholdMethod(bp.getStatistics()), bp.getHistogram());
      bp.threshold(threshold);
      bp.skeletonize();
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

}
