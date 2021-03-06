/**
 *
 */
package edu.uk.dromm.imagej;

import ij.ImagePlus;
import ij.plugin.Harris_;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.OwnFFTFilter;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.Skeletonize3D;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dicardo
 *
 */
public class ImageJTest implements PlugInFilter {

  private final int black = -16777216, white = -1;
  private final Skeletonize3D sk3d = new Skeletonize3D();

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
      URL ecgImage;
      BufferedImage bi;
      ImageProcessor ip;

      // ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-byn-out.png");

      ecgImage = this.getClass().getResource(
          "/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
      Assert.assertNotNull(ecgImage);
      bi = ImageIO.read(ecgImage);
      ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-pink-M234Mo253Std23Sk-2Ku14-out.png");

      ecgImage = this.getClass().getResource(
          "/image/ecg-pink-M227Mo255Std41Sk-3Ku13.gif");
      Assert.assertNotNull(ecgImage);
      bi = ImageIO.read(ecgImage);
      ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-pink-M227Mo255Std41Sk-3Ku13.png");

      // ecgImage = this.getClass().getResource("/image/ecg-pink-3.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-pink-3-out.png");
      //
      // ecgImage = this.getClass().getResource("/image/ecg-pink-4.png");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-pink-4-out.png");
      //
      // ecgImage = this.getClass().getResource("/image/ecg-pink-5.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-pink-5-out.png");
      //
      // ecgImage = this.getClass().getResource(
      // "/image/ecg-pink-2-year-old-boy.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-pink-2-year-old-boy-out.png");

      // ecgImage = this.getClass().getResource(
      // "/image/ecg-transparent-background-1.gif");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-transparent-background-1-out.png");
      //
      // ecgImage = this.getClass().getResource(
      // "/image/ecg-white-background-1.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-white-background-1-out.png");
      //
      // ecgImage = this.getClass().getResource(
      // "/image/ecg-white-background-2.png");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-white-background-2-out.png");
      //
      // ecgImage =
      // this.getClass().getResource("/image/ecg-blue-background.jpg");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-blue-background-out.png");

    } catch (final IOException e) {
      Assert.fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  @Test
  public void filtering() {
    final String[] imagePathsToProcess = new String[] { "ecg-byn", "ecg-pink" };
    try {
      final Map<Integer, String> iToName = new HashMap<>();
      iToName.put(0, "blur");
      iToName.put(1, "edges");
      iToName.put(2, "median");
      iToName.put(3, "min");
      iToName.put(4, "max");
      iToName.put(5, "convolve");
      for (final String imagePath : imagePathsToProcess) {
        final URL ecgImage = this.getClass().getResource(
            "/image/" + imagePath + ".jpg");
        final BufferedImage bi = ImageIO.read(ecgImage);
        final ImageProcessor ip = new ColorProcessor(bi);
        for (int i = 0; i < 6; i++) {
          final BinaryProcessor bp = new BinaryProcessor(
              (ByteProcessor) ip.convertToByte(false));
          bp.filter(i);
          ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
              + imagePath + "-" + iToName.get(i) + ".png"));
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void thresholding() {
    final String[] imagePathsToProcess = new String[] { "ecg-byn", "ecg-pink" };
    try {
      final List<ThresholdMethodStatistics> methodStats = new ArrayList<>();
      for (final String imagePath : imagePathsToProcess) {
        final URL ecgImage = this.getClass().getResource(
            "/image/" + imagePath + ".jpg");
        final BufferedImage bi = ImageIO.read(ecgImage);
        final ImageProcessor ip = new ColorProcessor(bi);
        for (final Method threshMethod : AutoThresholder.Method.values()) {
          final BinaryProcessor bp = new BinaryProcessor(
              (ByteProcessor) ip.convertToByte(false));
          final int thresh = new AutoThresholder().getThreshold(threshMethod,
              bp.getHistogram());
          methodStats.add(new ThresholdMethodStatistics(thresh, threshMethod
              .name()));
          System.out.println(String.format("%s thresh: %s", threshMethod,
              thresh));
          bp.threshold(thresh);
          System.out.println(String.format("%s histogram: %s", threshMethod,
              Arrays.toString(bp.getHistogram())));
          ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
              + imagePath + "-autothresh-"
              + threshMethod.toString().toLowerCase() + ".png"));
        }
        Collections.sort(methodStats);
        System.out.println("THRESHOLD STATISTICS");
        for (final ThresholdMethodStatistics tms : methodStats)
          System.out.println(tms);
      }
    } catch (final Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
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
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.setAntialiasedText(true);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-aatext.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.setLineWidth(1);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-line1.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.sharpen();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-sharpen.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.skeletonize();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-ske.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.smooth();
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-smooth.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.subtract(255d);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract255.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.subtract(ip.getStatistics().mean);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-mean.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.subtract(ip.getStatistics().median);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-median.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.xor(255);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-xor255.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.xor(0);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-xor0.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.or(0);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-or0.png"));
        bp = new BinaryProcessor((ByteProcessor) ip.convertToByte(false));
        bp.or(255);
        ImageIO.write(bp.getBufferedImage(), "png", new File("target/"
            + imagePath + "-substract-or255.png"));
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

  private static void doRun(final ImageProcessor ip, final String stringOut) {
    final File outFile = new File(stringOut);
    try {
      System.out.println("============= " + stringOut + " =============");
      final BinaryProcessor proc = new BinaryProcessor(
          (ByteProcessor) ip.convertToByte(false));
      printStatistics(proc);
      final ImagePlus imagePlus = new ImagePlus("", proc);

      final OwnFFTFilter fft = new OwnFFTFilter();
      fft.filter(proc, 3, 3, 2, 5);
      fft.filter(proc, 3, 3, 1, 5);

      final BackgroundSubtracter bs = new BackgroundSubtracter();
      bs.setup("", imagePlus);
      bs.rollingBallBackground(proc, 0.05d, false, true, true, false, false);

      proc.autoThreshold();

      proc.skeletonize();

      ImageIO.write(ip.getBufferedImage(), "png", new File(outFile.getPath()
          .replaceAll(".png", "-pure.png")));
      ImageIO.write(proc.getBufferedImage(), "png", outFile);

    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  @Ignore
  public void detectSquarePulse() {
    final URL ecgImage = this.getClass().getResource(
        "/image/ecg-pink-M201Mo234Std41Sk-1Ku0.jpg");
    BufferedImage bi;
    try {
      bi = ImageIO.read(ecgImage);
      final ImageProcessor ip = new ColorProcessor(bi);
      final BinaryProcessor bp = new BinaryProcessor(
          (ByteProcessor) ip.convertToByte(false));
      final Convolver conv = new Convolver();
      final BinaryProcessor proc = (BinaryProcessor) bp.convertToByte(false);
      final ImagePlus imP = new ImagePlus("", proc);
      conv.setup("", imP);
      conv.setNormalize(false);
      // final OwnFFTFilter fft = new OwnFFTFilter();
      // fft.filter(ip, 3, 3, 2, 5);
      // fft.filter(ip, 3, 3, 1, 5);
      // conv.convolve(proc,new float[] {
      // -4, -4, -4, -4, -4,
      // -1, -1, -1, -1, -1,
      // 32, 16, 0, 16, 32,
      // -1, -1, -1, -1, -1,
      // -4, -4, -4, -4, -4}, 5, 5);
      final Harris_ harr = new Harris_();
      // harr.setup("", imP);
      harr.filter(proc, 10, 8, 1);
      // harr.run(proc);
      ImageIO.write(ip.getBufferedImage(), "png", new File(
          "target/ecg-pink-org.png"));
      ImageIO.write(proc.getBufferedImage(), "png", new File(
          "target/ecg-pink-detected.png"));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void printStatistics(final ImageProcessor ip) {
    System.out.println(String.format("Histogram: %s, %s, %s",
        ip.getHistogramSize(), ip.getHistogramMax(), ip.getHistogramMin()));
    final ImageStatistics ips = ip.getStatistics();
    final Field[] stats = ImageStatistics.class.getDeclaredFields();
    for (final Field f : stats)
      try {
        f.setAccessible(true);
        System.out.println(String.format("%s: %s", f.getName(), f.get(ips)));
        f.setAccessible(false);
      } catch (final Exception e) {
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
    sk3d.setup(arg0, arg1);
    return NO_CHANGES;
  }

  class ThresholdMethodStatistics implements
      Comparable<ThresholdMethodStatistics> {
    private final int thresh;
    private final String name;

    public ThresholdMethodStatistics(final int thresh, final String name) {
      super();
      this.thresh = thresh;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public int getThresh() {
      return thresh;
    }

    @Override
    public String toString() {
      return "ThresholdMethodStatistics [thresh=" + thresh + ", name=" + name
          + "]";
    }

    @Override
    public int compareTo(final ThresholdMethodStatistics o) {
      if (o.getThresh() > thresh)
        return 1;
      else if (o.getThresh() < thresh)
        return -1;
      return 0;
    }
  }
}
