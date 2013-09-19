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
      final int localMax = 0;
      for (int i = 0; i < histogram.length; i++) {
      }
      ip.threshold(45);
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

}
