package edu.uk.dromm.img.impl;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;

import edu.uk.dromm.img.ImageProcess;

public class ECGImagePreprocessing implements ImageProcess {

  private final double proportion = 0.022;

  public ECGImagePreprocessing() {
  }

  @Override
  public BufferedImage process(final BufferedImage bi) {
    final ImageProcessor ip = new ColorProcessor(bi);
    final ImageProcessor bp = new BinaryProcessor(
        (ByteProcessor) ip.convertToByte(false));
    new HistogramFilter().run(bp);
    return bp.getBufferedImage();
  }

  class HistogramFilter implements PlugInFilter {

    @Override
    public void run(final ImageProcessor ip) {
      final BinaryProcessor bp = (BinaryProcessor) ip;
      final int[] histogram = bp.getHistogram();
      final double total = bp.getPixelCount();
      int count = 0;
      int current = 0;
      for (current = 0; current < histogram.length; current++) {
        count += histogram[current];
        final double ratio = count / total;
        final double epsilon = 0.0005;
        if (proportion - epsilon <= ratio && ratio <= proportion + epsilon)
          break;
      }
      bp.medianFilter();
      bp.dilate();
      bp.noise(10d);
      bp.dilate();
      bp.threshold(current);
      bp.skeletonize();
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

}
