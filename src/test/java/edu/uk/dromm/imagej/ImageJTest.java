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

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dicardo
 * 
 */
public class ImageJTest implements PlugInFilter {

  private final int black = -16777216, white = -1;

  @Test
  public void colorsFromBlackToWhiteToHexaAreSequential() {
    Assert.assertEquals(black, Color.BLACK.getRGB());
    Assert.assertTrue(black < Color.BLUE.getRGB()
        && Color.BLUE.getRGB() < white);
    Assert.assertEquals(white, Color.WHITE.getRGB());
  }

  @Test
  public void process() {
    try {
      URL ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
      Assert.assertNotNull(ecgImage);
      BufferedImage bi = ImageIO.read(ecgImage);
      ImageProcessor ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-byn-out.png");
      ecgImage = this.getClass().getResource("/image/ecg-pink.jpg");
      Assert.assertNotNull(ecgImage);
      bi = ImageIO.read(ecgImage);
      ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-pink-out.png");
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
    // doRun(ip, "Dani.png");
  }

  private void doRun(final ImageProcessor ip, final String stringOut) {
    final File outFile = new File(stringOut);
    try {
      final BinaryProcessor proc = new BinaryProcessor(
          (ByteProcessor) ip.convertToByte(false));
      System.out.println(String.format("Histogram: %s, %s, %s",
          proc.getHistogramSize(), proc.getHistogramMax(),
          proc.getHistogramMin()));
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
      System.out.println("Mean " + mean);
      System.out.println("before : " + Arrays.toString(proc.getHistogram()));
      final int thresh = calculate(proc.getHistogram(), proc.getPixelCount());
      //      proc.sharpen();
      //      proc.dilate();
      proc.threshold(thresh);
      proc.findEdges();
      proc.filter(ImageProcessor.MEDIAN_FILTER);
      proc.invert();
      System.out.println("after : " + Arrays.toString(proc.getHistogram()));
      ImageIO.write(ip.getBufferedImage(), "png", new File("target/pure.png"));
      ImageIO.write(proc.getBufferedImage(), "png", outFile);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private int calculate(final int[] h, final int total) {
    int count = 0;
    final double prop = 0.02;
    for (int i = 0; i < h.length; i++) {
      count += h[i];
      final double ratio = (double) count / (double) total;
      if (prop - 0.0005 <= ratio && ratio <= prop + 0.0005)
        return i;
    }
    return -1;
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
}
