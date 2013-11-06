/**
 * 
 */
package edu.uk.dromm.imagej;

import ij.ImagePlus;
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
import org.junit.Test;

import skeleton_analysis.AnalyzeSkeleton_;
import skeleton_analysis.SkeletonResult;
import edu.uk.dromm.img.Factory;
import edu.uk.dromm.img.ImageParameterProvider;

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
      URL ecgImage;
      BufferedImage bi;
      ImageProcessor ip;

      ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
      Assert.assertNotNull(ecgImage);
      bi = ImageIO.read(ecgImage);
      ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-byn-out.png");

      ecgImage = this.getClass().getResource("/image/ecg-pink-1.jpg");
      Assert.assertNotNull(ecgImage);
      bi = ImageIO.read(ecgImage);
      ip = new ColorProcessor(bi);
      doRun(ip, "target/ecg-pink-1-out.png");

      // ecgImage = this.getClass().getResource("/image/ecg-pink-2.gif");
      // Assert.assertNotNull(ecgImage);
      // bi = ImageIO.read(ecgImage);
      // ip = new ColorProcessor(bi);
      // doRun(ip, "target/ecg-pink-2-out.png");

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
      //
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
      final Map<Integer, String> iToName = new HashMap<Integer, String>();
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
      final List<ThresholdMethodStatistics> methodStats = new ArrayList<ThresholdMethodStatistics>();
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
        for (final ThresholdMethodStatistics tms : methodStats) {
          System.out.println(tms);
        }
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

  private void doRun(final ImageProcessor ip, final String stringOut) {
    final File outFile = new File(stringOut);
    final Factory factory = new Factory();
    final ImageParameterProvider ipp = factory.getImageParameterProvider();
    try {
      System.out.println("BEFORE");
      printStatistics(ip);
      final BinaryProcessor proc = new BinaryProcessor(
          (ByteProcessor) ip.convertToByte(false));
      System.out.println("AFTER");
      printStatistics(proc);
      System.out.println("Before : " + Arrays.toString(proc.getHistogram()));
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-0.png")));
      final AutoThresholder thresholder = new AutoThresholder();
      int threshold = thresholder.getThreshold(
          ipp.thresholdMethod(proc.getStatistics()), proc.getHistogram());
      proc.threshold(threshold);
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-1.png")));
      for (int i = 0; i < 5; i++) {
        proc.filter(ImageProcessor.BLUR_MORE);
      }
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-" + 2 + ".png")));
      threshold = thresholder.getThreshold(
          ipp.thresholdMethod(proc.getStatistics()), proc.getHistogram());
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-3.png")));
      proc.threshold(threshold);
      // ImageIO.write(proc.getBufferedImage(), "png", new
      // File(outFile.getPath()
      // .replaceAll(".png", "-4.png")));
      proc.skeletonize();
      //

      final AnalyzeSkeleton_ analyzeSkeleton = new AnalyzeSkeleton_();
      proc.invert();
      final ImagePlus imagePlus = new ImagePlus("hoooo", proc);
      System.out.println("list end of points: 0.0");
      // final ImageStack imageStack = analyzeSkeleton.getResultImage(true);
      analyzeSkeleton.calculateShortestPath = true;
      analyzeSkeleton.setup("", imagePlus);
      // analyzeSkeleton.verbose = true;
      // analyzeSkeleton.pruneEnds = true;
      final SkeletonResult skelResult = analyzeSkeleton.run(
          AnalyzeSkeleton_.LOWEST_INTENSITY_VOXEL, false, true, imagePlus,
          true, false);

      System.out.println("list end of points: 0");
      // final SkeletonResult skeletonResult = analyzeSkeleton.run(
      // AnalyzeSkeleton_.LOWEST_INTENSITY_VOXEL, true, false, imagePlus,
      // false, true);
      System.out.println("list end of points: 0");
      // System.out.println("list: "
      // + skeletonResult.getListOfEndPoints().toString());
      System.out.println("list end of points: 0");

      //
      System.out.println("After : " + Arrays.toString(proc.getHistogram()));
      // ImageIO.write(ip.getBufferedImage(), "png", new File(outFile.getPath()
      // .replaceAll(".png", "-pure.png")));
      final BinaryProcessor proc2 = new BinaryProcessor(
          (ByteProcessor) proc.convertToByte(false));
      ImageIO.write(proc2.getBufferedImage(), "png", outFile);
      // ImageIO.write(proc.getBufferedImage(), "png", outFile);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private void printStatistics(final ImageProcessor ip) {
    System.out.println(String.format("Histogram: %s, %s, %s",
        ip.getHistogramSize(), ip.getHistogramMax(), ip.getHistogramMin()));
    final ImageStatistics ips = ip.getStatistics();
    final Field[] stats = ImageStatistics.class.getDeclaredFields();
    for (final Field f : stats) {
      try {
        f.setAccessible(true);
        System.out.println(String.format("%s: %s", f.getName(), f.get(ips)));
        f.setAccessible(false);
      } catch (final Exception e) {
        e.printStackTrace();
      }
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
      if (o.getThresh() > thresh) {
        return 1;
      } else if (o.getThresh() < thresh) {
        return -1;
      }
      return 0;
    }
  }
}
