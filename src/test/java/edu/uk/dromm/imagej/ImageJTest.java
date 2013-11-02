/**
 * 
 */
package edu.uk.dromm.imagej;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.Skeletonize3D;
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
  private final Skeletonize3D sk3d = new Skeletonize3D();;

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

  @Test
  public void processingTools() {
    final String[] imagePathsToProcess = new String[] { "ecg-byn", "ecg-pink" };
    try {
      for (final String imagePath : imagePathsToProcess) {
        final URL ecgImage = this.getClass().getResource(
            "/image/" + imagePath + ".jpg");
        final BufferedImage bi = ImageIO.read(ecgImage);
        final ImageProcessor ip = new ColorProcessor(bi);
        BinaryProcessor bp = new BinaryProcessor(
            (ByteProcessor) ip.convertToByte(false));
        final int thresh = calculate(bp.getHistogram(), bp.getPixelCount());
        bp.autoThreshold();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-autothresh.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.erode();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-erode.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.and(255);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-and255.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.and(0);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-and0.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.erode(2, 255);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-erode.2.255.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.BILINEAR);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-bilinear.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.BICUBIC);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-bicubic.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.BLUR_MORE);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-blurmore.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.CENTER_JUSTIFY);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-centerjustify.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.ISODATA);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-isodata.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.ISODATA2);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-isodata2.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.LEFT_JUSTIFY);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-leftjustify.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.RIGHT_JUSTIFY);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-rightjustify.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.MAX);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-max.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.MIN);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-min.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.filter(ImageProcessor.MEDIAN_FILTER);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-median.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.findEdges();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-edges.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.noise(10d);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-noise10d.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.outline();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-outline.png"));
      }
    } catch (final Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
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
      System.out.println("Before : " + Arrays.toString(proc.getHistogram()));
      final int thresh = calculate(proc.getHistogram(), proc.getPixelCount());
      final ImagePlus imp = new ImagePlus("", proc);
      // final BackgroundSubtracter bsubs = new BackgroundSubtracter();
      // bsubs.setup("", imp);
      // bsubs.rollingBallBackground(proc, 0.2, false, true, true, false, true);
      ImageIO.write(proc.getBufferedImage(), "png", new File(outFile.getPath()
          .replaceAll(".png", "-bkg.png")));
      // proc.filter(ImageProcessor.MEDIAN_FILTER);
      // proc.medianFilter();
      // proc.medianFilter();
      // proc.medianFilter();
      // proc.medianFilter();
      // proc.medianFilter();
      // proc.autoThreshold();
      proc.smooth();
      proc.threshold(thresh);
      proc.skeletonize();
      // proc.dilate();
      // for (int i = 0; i < 1; i++) {
      // proc.medianFilter();
      // }
      proc.medianFilter();
      proc.dilate();
      proc.noise(10d);
      proc.dilate();
      proc.threshold(thresh);
      proc.skeletonize();
      proc.threshold(thresh);
      proc.threshold(thresh);
      proc.skeletonize();
      // proc.threshold(thresh);
      // proc.threshold(thresh);
      ImageIO.write(proc.getBufferedImage(), "png", new File(outFile.getPath()
          .replaceAll(".png", "-thresh.png")));
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-edges.png")));
      // for (int i = 0; i < 10; i++) {
      // proc.filter(ImageProcessor.MEDIAN_FILTER);
      // }
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-filtered.png")));
      // final Skeletonize3D sk3d = new Skeletonize3D();
      // sk3d.setup("", imp);
      // sk3d.run(proc);
      // proc.invert();
      // proc.skeletonize();
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-ske.png")));
      // System.out.println("After : " + Arrays.toString(proc.getHistogram()));
      // ImageIO.write(ip.getBufferedImage(), "png", new File(outFile.getPath()
      // .replaceAll(".png", "-pure.png")));
      // ImageIO.write(proc.getBufferedImage(), "png", outFile);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private int calculate(final int[] h, final int total) {
    int count = 0;
    final double prop = 0.022;
    for (int i = 0; i < h.length; i++) {
      count += h[i];
      final double ratio = (double) count / (double) total;
      final double epsilon = 0.0005;
      if (prop - epsilon <= ratio && ratio <= prop + epsilon) {
        return i;
      }
    }
    return -1;
  }

  public void convertToCoordinate(final ImageProcessor ip) {

    ip.getHeight();
    ip.getWidth();
  }

  /*
   * (non-Javadoc)
   * 
   * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
   */
  @Override
  public int setup(final String arg0, final ImagePlus arg1) {
    sk3d.setup(arg0, arg1);
    return NO_CHANGES;
  }
}
