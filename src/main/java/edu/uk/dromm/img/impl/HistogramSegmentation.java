package edu.uk.dromm.img.impl;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;

import edu.uk.dromm.img.ImageProcess;

public class HistogramSegmentation implements ImageProcess {

  private final double proportion = 0.01;

  public HistogramSegmentation() {
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
      final int[] histogram = ip.getHistogram();
      final double total = ip.getPixelCount();
      int count = 0;
      int current = 0;
      for (current = 0; current < histogram.length; current++) {
        count += histogram[current];
        final double ratio = count / total;
        double epsilon = 0.0005;
        if (proportion - epsilon <= ratio && ratio <= proportion + epsilon) {
          break;
        }
      }
      ip.threshold(current);
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

}
