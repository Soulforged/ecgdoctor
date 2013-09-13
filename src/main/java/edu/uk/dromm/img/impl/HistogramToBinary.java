package edu.uk.dromm.img.impl;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

import edu.uk.dromm.img.ImageProcess;

public class HistogramToBinary implements ImageProcess {

  private static final int black = Color.BLACK.getRGB();
  private double tolerance = 50.0;

  public HistogramToBinary(final double tolerance) {
    this.tolerance = tolerance;
  }

  @Override
  public BufferedImage process(final BufferedImage bi) {
    final URL ecgImage = this.getClass().getResource(
        "/image/ecg-output-lrg.jpg");
    final ImageProcessor ip = new ColorProcessor(bi);
    final BufferedImage ob = new BufferedImage(bi.getWidth(), bi.getHeight(),
        BufferedImage.TYPE_BYTE_GRAY);
    new HistogramFilter(ob).run(ip);
    return ob;
  }

  class HistogramFilter implements PlugInFilter {

    final BufferedImage outputBuffer;

    public HistogramFilter(final BufferedImage ob) {
      outputBuffer = ob;
    }

    @Override
    public void run(final ImageProcessor ip) {
      final ImageProcessor proc = new BinaryProcessor(new ByteProcessor(
          outputBuffer));
      final int width = ip.getWidth();
      final int height = ip.getHeight();
      for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++) {
          final long k = ip.getPixel(j, i);
          final long diff = black - k;
          int color = 255;
          if (Math.abs(diff) < tolerance * 65000)
            color = 0;
          proc.putPixel(j, i, color);
        }
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

}
