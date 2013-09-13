/**
 * 
 */
package edu.uk.dromm.imagej;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dicardo
 * 
 */
public class ImageJTest implements PlugInFilter {

  private Map<Long, Long> histogramData;

  private final int       black     = -16777216, white = -1;
  private final float     tolerance = 50.0f;

  @Test
  public void colorsFromBlackToWhiteToHexaAreSequential() {
    Assert.assertEquals(black, Color.BLACK.getRGB());
    Assert.assertTrue(black < Color.BLUE.getRGB()
        && Color.BLUE.getRGB() < white);
    Assert.assertEquals(white, Color.WHITE.getRGB());
  }

  @Test
  public void findEdges() {
    try {
      final URL ecgImage = this.getClass().getResource(
          "/image/ecg-output-lrg.jpg");
      Assert.assertNotNull(ecgImage);
      BufferedImage bi;
      bi = ImageIO.read(ecgImage);
      final ImageProcessor ip = new ColorProcessor(bi);
      new FindEdgesFilter().run(ip);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void findEdgesBinaryToBinary() {
    try {
      final URL ecgImage = this.getClass().getResource(
          "/image/ecg-output-lrg.jpg");
      Assert.assertNotNull(ecgImage);
      final BufferedImage bi = ImageIO.read(ecgImage);
      final ImageProcessor ip = new ColorProcessor(bi);
      final BinaryProcessor bp = new BinaryProcessor(
          (ByteProcessor) ip.convertToByte(false));
      new FindEdgesBinaryFilter().run(bp);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void process() {
    try {
      final URL ecgImage = this.getClass().getResource(
          "/image/ecg-output-lrg.jpg");
      Assert.assertNotNull(ecgImage);
      final BufferedImage bi = ImageIO.read(ecgImage);
      final ImageProcessor ip = new ColorProcessor(bi);
      run(ip);
      histogramData = new HashMap<Long, Long>(32000);
      final Iterator<Entry<Long, Long>> it = histogramData.entrySet()
          .iterator();
      Long value = 0L;
      while (it.hasNext()) {
        final Entry<Long, Long> entry = it.next();
        value += entry.getValue();
        final Long key = entry.getKey();
        final String hex = Long.toHexString(key);
        final int intValue = key.intValue();
        System.out.println(String.format("%s,%s,%s,%s:%s:%s:%s", ip
            .getColorModel().getAlpha(intValue),
            ip.getColorModel().getRed(intValue),
            ip.getColorModel().getGreen(intValue),
            ip.getColorModel().getBlue(intValue), key, hex, value));
      }
      System.out.println("Total: " + value);
    } catch (final IOException e) {
      Assert.fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
   */
  @Override
  public void run(final ImageProcessor ip) {
    final File outFile = new File("target/hist-out.png");
    try {
      final BinaryProcessor proc = new BinaryProcessor((ByteProcessor)ip.convertToByte(false));
      System.out.println(String.format("Histogram: %s, %s, %s",
          proc.getHistogramSize(), proc.getHistogramMax(),
          proc.getHistogramMin()));
      //      proc.setAutoThreshold(AutoThresholder.Method.Moments, false);
      final ImageStatistics statistics = proc.getStatistics();
      final double median = statistics.median;
      final double mean = statistics.mean;
      final double angle = statistics.angle;
      final double kurtosis = statistics.kurtosis;
      final double stdDev = statistics.stdDev;
      proc.setAntialiasedText(true);
      System.out.println("StdDev " + stdDev);
      System.out.println("Kurtosis " + kurtosis);
      System.out.println("Angle " + angle);
      System.out.println("Median " + median);
      System.out.println("Mean " + median);
      proc.threshold(45);
      //      proc.findEdges();
      //      proc.dilate();
      proc.sharpen();
      System.out.println(Arrays.toString(proc.getCalibrationTable()));
      System.out.println(Arrays.toString(proc.getHistogram()));
      ImageIO.write(ip.getBufferedImage(), "png", new File("target/pure.png"));
      ImageIO.write(proc.getBufferedImage(), "png", outFile);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
   */
  @Override
  public int setup(final String arg0, final ImagePlus arg1) {
    return NO_CHANGES;
  }

  class FindEdgesFilter implements PlugInFilter {
    @Override
    public void run(final ImageProcessor ip) {
      final File outFile = new File("target/edges.png");
      try {
        final BufferedImage sIM = new BufferedImage(ip.getWidth(),
            ip.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        final ColorProcessor proc = new ColorProcessor(sIM);
        histogramData = new HashMap<Long, Long>(32000);
        proc.findEdges();
        ImageIO.write(sIM, "png", outFile);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }

  class FindEdgesBinaryFilter implements PlugInFilter {
    @Override
    public void run(final ImageProcessor ip) {
      final File outFile = new File("target/edges10.png");
      try {
        final BufferedImage sIM = new BufferedImage(ip.getWidth(),
            ip.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        final BinaryProcessor proc = new BinaryProcessor(new ByteProcessor(sIM));
        proc.findEdges();
        ImageIO.write(sIM, "png", outFile);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public int setup(final String arg0, final ImagePlus arg1) {
      return NO_CHANGES;
    }
  }
}
