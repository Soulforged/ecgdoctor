/**
 *
 */
package edu.uk.dromm.img.impl;

import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Test;

import edu.uk.dromm.img.DefaultECGImageAnalisys;
import edu.uk.dromm.img.DefaultECGImageAnalisys.HeightLengths;
import edu.uk.dromm.img.DefaultECGImageAnalisys.Range;
import edu.uk.dromm.img.EcgMetrics;
import edu.uk.dromm.img.ImageProcess;

/**
 * @author magian
 *
 */
public class NormalPinkDefaultECGImageAnalisysTest {

  private static final String IMAGE = "/image/ecg-pink-typical-normal2.jpg";

  private static BinaryProcessor bp;

  @Test
  public void canFillSpacesBetweenPoints() {
    final ImageProcessor ip = getImageProcessor(IMAGE);
    final DefaultECGImageAnalisys ana = new DefaultECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> allPointsI = ana.allPoints(allPoints, 1, ip.getWidth());
    final Set<Integer> heightsOrdered = ana.differentHeights(allPointsI);
    final List<Range> ranges = ana.heightRanges(heightsOrdered, 30);
    final List<Range> relevantRanges = ana.relevantRanges(ranges, 25);
    final Map<Integer, List<Point>> allPointsPerHeight = ana
        .allPointsPerHeight(allPointsI, heightsOrdered);
    final List<HeightLengths> allLenghtsPerHeight = ana
        .mapLengths(allPointsPerHeight);
    final int height = ana.zeroByHeightMargins(relevantRanges.get(1).begin,
        relevantRanges.get(1).end, allLenghtsPerHeight);
    final List<Point> pointsInLeadII = ana.pointsInLead(relevantRanges, 1,
        allPointsI);
    System.out.println(pointsInLeadII);
    final List<Point> calibratedPoints = ana.calibrate(pointsInLeadII, height);
    System.out.println(calibratedPoints);
    Assert.assertEquals(pointsInLeadII.size(), calibratedPoints.size());
    final List<Point> filteredPoints = ana.coalesce(calibratedPoints);
    System.out.println(filteredPoints);
    Assert.assertTrue(filteredPoints.size() <= calibratedPoints.size());
    final Map<Integer, Point> sifted = new HashMap<>();
    for (final Point p : filteredPoints)
      sifted.put(p.x, p);
    Assert.assertEquals(sifted.keySet().size(), filteredPoints.size());
    final Point start = ana.detectStart(filteredPoints);
    System.out.println(start);
    final List<Point> chopped = ana.chop(filteredPoints, start);
    System.out.println(chopped);
    final EcgMetrics met = ana.detectWaves(chopped);
    System.out.println(String.format(
        "P starts at %s and ends at %s, with a peak of %s at %s", met.pStart,
        met.pEnd, met.pPeak, met.pPeakT));
    System.out.println(String.format(
        "Q starts at %s and reaches its peak %s at %s", met.qStart, met.qPeak,
        met.qPeakT));
    System.out.println(String.format(
        "R starts at %s and reaches its peak %s at %s", met.rStart, met.rPeak,
        met.rPeakT));
    System.out.println(String.format(
        "S starts at %s and ends at %s, with a peak of %s at %s", met.sStart,
        met.sEnd, met.sPeak, met.sPeakT));
    System.out.println(String.format(
        "T starts at %s and ends at %s, with a peak of %s at %s", met.tStart,
        met.tEnd, met.tPeak, met.tPeakT));
    System.out.println(String.format("Next R occurs at %s with a peak of %s",
        met.nextRt, met.nextR));
  }

  private static ImageProcessor getImageProcessor(final String imageResourcePath) {
    if (bp == null) {
      final URL ecgImage = NormalPinkDefaultECGImageAnalisysTest.class
          .getResource(imageResourcePath);
      Assert.assertNotNull(ecgImage);
      BufferedImage bi = null;
      try {
        bi = ImageIO.read(ecgImage);
      } catch (final IOException e) {
        e.printStackTrace();
      }
      final ImageProcess pre = new ECGImagePreprocessing();
      final BufferedImage resultantBi = pre.process(bi);
      bp = new BinaryProcessor(new ByteProcessor(resultantBi));
    }
    return bp;
  }

  class ByYSeparation implements Predicate {
    final private int y;
    final private int separation;

    public ByYSeparation(final int y, final int separation) {
      super();
      this.y = y;
      this.separation = separation;
    }

    @Override
    public boolean evaluate(final Object i) {
      final Integer integer = (Integer) i;
      return integer - y < separation;
    }
  }

  class NearPoint implements Predicate {

    private final Point centralPoint;
    private final int nearNess;

    public NearPoint(final Point central, final int nearNess) {
      super();
      this.nearNess = nearNess;
      centralPoint = central;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      if (point.equals(centralPoint))
        return true;
      else if (point.x == centralPoint.x)
        return Math.abs(point.y - centralPoint.y) == nearNess;
      else if (point.y == centralPoint.y)
        return Math.abs(point.x - centralPoint.x) == nearNess;
      return false;
    }
  }

  class IntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(final Integer o1, final Integer o2) {
      return o2.compareTo(o1);
    }
  }

  class XPoints implements Predicate {

    private final int x;

    public XPoints(final int x) {
      super();
      this.x = x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      if (point.x == x)
        return true;
      return false;
    }
  }

  class YPoints implements Predicate {

    private final int y;

    public YPoints(final int y) {
      super();
      this.y = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      if (point.y == y)
        return true;
      return false;
    }
  }
}
