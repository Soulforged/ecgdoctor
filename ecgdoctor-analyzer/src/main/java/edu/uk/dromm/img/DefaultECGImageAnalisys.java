/**
 *
 */
package edu.uk.dromm.img;

import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TreeList;

import edu.uk.dromm.img.ecg.exception.InvalidECGSectionException;

/**
 * @author magian
 *
 */
public class DefaultECGImageAnalisys implements ImageAnalysis {

  private int blackValue = 0;
  private final float avgLeadWidth = 0.2400620201f;
  private final float avgLeadHeight = 0.08f;

  @Override
  public ECGParameters process(final BufferedImage bi) {
    final ImageProcessor ip = new BinaryProcessor(new ByteProcessor(bi));
    final double gridCellMs = 2600 / (ip.getWidth() * avgLeadWidth);
    final double gridCellMV = 1 / (ip.getHeight() * avgLeadHeight);

    final List<Point> allPoints = allPoints(ip);
    final List<Point> allPointsI = allPoints(allPoints, 1, ip.getWidth());
    final Set<Integer> heightsOrdered = differentHeights(allPointsI);
    final List<Range> relevantRanges = heightRanges(heightsOrdered, 25);
    final Map<Integer, List<Point>> allPointsPerHeight = allPointsPerHeight(
        allPointsI, heightsOrdered);
    final List<HeightLengths> allLenghtsPerHeight = mapLengths(allPointsPerHeight);
    final int height = zeroByHeightMargins(relevantRanges.get(1).begin,
        relevantRanges.get(1).end, allLenghtsPerHeight);
    final List<Point> pointsInLeadII = pointsInLead(relevantRanges, 1,
        allPointsI);
    final List<Point> calibratedPoints = calibrate(pointsInLeadII, height);
    final List<Point> filteredPoints = coalesce(calibratedPoints);
    final Point start = detectStart(filteredPoints);
    final List<Point> chopped = chop(filteredPoints, start);
    final EcgMetrics met = detectWaves(chopped);
    return new ECGParameters(met.pStart * gridCellMs, met.pPeak * gridCellMV,
        met.pEnd * gridCellMs, met.pPeakT * gridCellMs,
        met.qStart * gridCellMs, met.qPeak * gridCellMV, met.qPeakT
        * gridCellMs, met.rStart * gridCellMs, met.rPeak * gridCellMV,
        met.rPeakT * gridCellMs, met.sStart * gridCellMs, met.sPeak
        * gridCellMV, met.sEnd * gridCellMV, met.sPeakT * gridCellMs,
        met.tStart * gridCellMs, met.tPeak * gridCellMV, met.tEnd * gridCellMs,
        met.tPeakT * gridCellMs, met.nextR * gridCellMV, met.nextRt
        * gridCellMs);
  }

  /**
   * @param blackValue
   */
  public DefaultECGImageAnalisys(final int blackValue) {
    super();
    this.blackValue = blackValue;
  }

  /**
   * Finds the location and peaks of all ECG waves (P,QRS,T,RR)
   *
   * @param points
   * @return
   */
  private static EcgMetrics detectWaves(final List<Point> points) {
    final EcgMetrics met = new EcgMetrics();
    for (final Point p : points) {
      if (p.y > 0 && met.pStart == -1)
        met.pStart = p.x;
      if (met.pStart > -1 && met.pEnd == 0)
        if (p.y > met.pPeak) {
          met.pPeak = p.y;
          met.pPeakT = p.x;
        }
      if (met.pStart > -1 && p.y == 0 && met.pEnd == 0)
        met.pEnd = p.x;
      if (met.pEnd > 0 && p.y < 0 && met.qStart == 0)
        met.qStart = p.x;
      if (met.qStart > 0 && p.y <= 0 && met.rStart == 0)
        if (met.qPeak > p.y) {
          met.qPeak = p.y;
          met.qPeakT = p.x;
        }
      if (met.qStart > 0 && p.y >= 0 && met.rStart == 0)
        met.rStart = p.x;
      if (met.rStart > 0 && met.sStart == 0)
        if (p.y > met.rPeak) {
          met.rPeak = p.y;
          met.rPeakT = p.x;
        }
      if (met.rStart > 0 && met.rPeak > 0 && p.y <= 0 && met.sStart == 0)
        met.sStart = p.x;
      if (met.sStart > 0 && met.sEnd == 0)
        if (met.sPeak > p.y) {
          met.sPeak = p.y;
          met.sPeakT = p.x;
        }
      if (met.sStart > 0 && met.sPeak < 0 && p.y == 0 && met.sEnd == 0)
        met.sEnd = p.x;
      if (met.sEnd > 0 && met.tStart == 0)
        met.tStart = p.x;
      if (met.tStart > 0 && met.tEnd == 0)
        if (p.y > met.tPeak) {
          met.tPeak = p.y;
          met.tPeakT = p.x;
        }
      if (met.tStart > 0 && met.tPeak > 0 && p.y == 0 && met.tEnd == 0)
        met.tEnd = p.x;
      if (met.tEnd > 0 && p.y > met.nextR) {
        met.nextR = p.y;
        met.nextRt = p.x;
      }
    }
    return met;
  }

  /**
   * Gets a list free of all points before the starting point calculated by
   * calling {@link #detectStart(List)}
   *
   * @param coalescedPoints
   * @param startAt
   * @return
   */
  private static List<Point> chop(final List<Point> coalescedPoints,
      final Point startAt) {
    CollectionUtils.filter(coalescedPoints, new Predicate() {
      @Override
      public boolean evaluate(final Object p) {
        final Point point = (Point) p;
        return point.x >= startAt.x;
      }
    });
    return coalescedPoints;
  }

  /**
   * Detects the point at which a baseline on a lead starts.
   * <p>
   * The baseline is usually a long enough succession of points at the same
   * height, the height of the zero at the lead.
   * </p>
   *
   * @param coalescedPoints
   * @return
   */
  private static Point detectStart(final List<Point> coalescedPoints) {
    int zCount = 0;
    Point sp = null;
    for (final Point p : coalescedPoints) {
      if (p.y == 0) {
        if (zCount == 0)
          sp = p;
        zCount++;
      } else
        zCount = 0;
      if (zCount > 5)
        break;
    }
    return sp;
  }

  /**
   * Transforms misaligned or aliased/superfluous points in the average of its
   * alias.
   * <p>
   * This creates a single one point clean line.
   * </p>
   *
   * @param calibratedPoints
   * @return
   */
  private static List<Point> coalesce(final List<Point> calibratedPoints) {
    final List<Point> filteredPoints = new ArrayList<>();
    int lastHeight = 0, lastLength = 0, toAvg = 0, count = 0;
    for (final Point p : calibratedPoints) {
      Point toAdd = p;
      if (p.x == lastLength) {
        if (Math.abs(p.y - lastHeight) > 2)
          continue;
        toAvg += p.y;
        count++;
        continue;
      }
      if (toAvg != 0) {
        toAdd = new ECGPoint(p.x, toAvg / count);
        toAvg = 0;
        count = 0;
      }
      lastHeight = p.y;
      lastLength = p.x;
      filteredPoints.add(toAdd);
    }
    return filteredPoints;
  }

  /**
   * Maps all the lengths, or differences between different Xs at a same Y
   * level.
   * <p>
   * The purpose of this is detecting candidates for a base ECG line
   * </p>
   *
   * @param allPointsPerHeight
   * @return
   */
  private static List<HeightLengths> mapLengths(
      final Map<Integer, List<Point>> allPointsPerHeight) {
    final List<HeightLengths> allLenghtsPerHeight = new ArrayList<>();
    for (final Entry<Integer, List<Point>> entry : allPointsPerHeight
        .entrySet()) {
      final List<Integer> lengths = new ArrayList<>();
      final List<Point> list = entry.getValue();
      int count = 0;
      for (int i = 0; i < list.size() - 1; i++) {
        final int diff = list.get(i + 1).x - list.get(i).x;
        if (diff <= 1)
          count++;
        else {
          if (count > 0)
            lengths.add(count);
          count = 0;
        }
      }
      if (!lengths.isEmpty())
        allLenghtsPerHeight.add(new HeightLengths(entry.getKey(), lengths));
    }
    Collections.sort(allLenghtsPerHeight);
    return allLenghtsPerHeight;
  }

  /**
   * Calculates the different ranges of horizontal sections by using an
   * aproximated margin.
   *
   * @param differentHeightsOrdered
   *          - the heights of a vertical section, calculated by calling
   *          {@link #differentHeights(List)} and passing
   *          {@link #allPoints(List, int, int)} into it.
   * @param margin
   *          - an arbitrary range. If
   *          {@code height and height + 1 is >= margin} then the range is
   *          considered as a new horizontal section
   * @return
   */
  private static List<Range> heightRanges(
      final Collection<Integer> differentHeightsOrdered, final int margin) {
    final List<Range> ranges = new ArrayList<>();
    final ArrayList<Integer> hList = new ArrayList<>(differentHeightsOrdered);
    int firstBound = hList.get(0);
    for (int i = 0; i < hList.size() - 1; i++) {
      final Integer height = hList.get(i);
      final Integer height1 = hList.get(i + 1);
      if (height1 - height >= margin) {
        ranges.add(new Range(firstBound, height));
        firstBound = height1;
      }
    }
    ranges.add(new Range(firstBound, hList.get(hList.size() - 1)));
    return ranges;
  }

  public static class Range {
    public int begin, end;

    public Range(final int begin, final int end) {
      super();
      this.begin = begin;
      this.end = end;
    }

    @Override
    public String toString() {
      return String.format("[%s-%s]", begin, end);
    }
  }

  /**
   * Finds the height of the zero X by looking for the max length in that
   * height.
   * <p>
   * The purpose of this is to detect a zero in a range and collection of
   * heights.
   * </p>
   *
   * @param lowerMargin
   *          - the lower margin of a point section, usually the lesser value of
   *          Y belonging to a lead.
   * @param upperMargin
   *          - the higher margin of a point section, usually the higher value
   *          of Y belonging to a lead.
   * @param lengths
   *          - all the lengths collected for each height
   * @return
   */
  private static int zeroByHeightMargins(final int lowerMargin,
      final int upperMargin, final List<HeightLengths> lengths) {
    HeightLengths maxL = lengths.get(0);
    for (final HeightLengths hl : lengths)
      if (lowerMargin <= hl.getHeight() && hl.getHeight() <= upperMargin
      && maxL.sum < hl.sum)
        maxL = hl;
    return maxL.height;
  }

  /**
   * Holds a size and a sum for all lenghts (differences between Xs at same
   * height level) at a same height level.
   *
   * @author dicardo
   *
   */
  public static class HeightLengths implements Comparable<HeightLengths> {

    protected final Integer height;
    private final Integer size;
    protected Integer sum = new Integer(0);

    public HeightLengths(final Integer height, final List<Integer> lengths) {
      super();
      this.height = height;
      size = lengths.size();
      CollectionUtils.forAllDo(lengths, new Closure() {
        @Override
        public void execute(final Object l) {
          sum += (Integer) l;
        }
      });
    }

    public Integer getHeight() {
      return height;
    }

    public Integer getSize() {
      return size;
    }

    public Integer getSum() {
      return sum;
    }

    @Override
    public String toString() {
      return String.format("%s:%s:%s", height, size, sum);
    }

    @Override
    public int compareTo(final HeightLengths o) {
      final int lenDiff = size - o.getSize();
      if (lenDiff != 0)
        return lenDiff * -1;
      final int sumDiff = sum - o.getSum();
      if (sumDiff != 0)
        return sumDiff;
      return height.compareTo(o.getHeight());
    }

    public static Comparator<HeightLengths> sumDesc() {
      return new Comparator<DefaultECGImageAnalisys.HeightLengths>() {
        @Override
        public int compare(final HeightLengths o1, final HeightLengths o2) {
          final int sumDiff = o1.getSum() - o2.getSum();
          if (sumDiff != 0)
            return sumDiff * -1;
          return o1.compareTo(o2);
        }
      };
    }
  }

  /**
   * Gives only the heights of a point collection.
   * <p>
   * The heights are ordered in natural {@link Integer} order
   * </p>
   *
   * @param allPoints
   * @return
   */
  private static Set<Integer> differentHeights(final List<Point> allPoints) {
    final Set<Integer> heights = new TreeSet<>();
    for (final Point p : allPoints)
      heights.add(p.y);
    return heights;
  }

  private List<Point> allPointsAt(final int y, final List<Point> allPoints) {
    @SuppressWarnings("unchecked")
    final List<Point> copy = new TreeList(allPoints);
    CollectionUtils.filter(copy, new PointsOnY(y));
    return copy;
  }

  /**
   * Gives all the points in a collection mapped by height.
   *
   * @param allPoints
   * @param differentHeights
   * @return
   */
  private Map<Integer, List<Point>> allPointsPerHeight(
      final List<Point> allPoints, final Set<Integer> differentHeights) {
    final Map<Integer, List<Point>> allPointsPerHeight = new HashMap<>(
        differentHeights.size());
    for (final Integer height : differentHeights)
      allPointsPerHeight.put(height, allPointsAt(height, allPoints));
    return allPointsPerHeight;
  }

  /**
   * Obtains all points in a range of the X axis.
   *
   * @param allPoints
   *          - all the ECG mapped points usually obtained calling
   *          {@link #allPoints(ImageProcessor)}
   * @param section
   *          - the number of the vertical section to collect, valid [1-4]
   * @param width
   *          - the width of the image that's actually filled by the ECG
   *          (discounting white margins for example), obtained by calling
   *          {@link ImageProcessor#getWidth()}
   * @return
   */
  private List<Point> allPoints(final List<Point> allPoints, final int section,
      final int width) {
    if (section < 1 || section > 4)
      throw new InvalidECGSectionException();
    @SuppressWarnings("unchecked")
    final List<Point> copy = new TreeList(allPoints);
    final int prevI = section - 1;
    CollectionUtils.filter(copy, new PointsBeetweenX(prevI * avgLeadWidth
        * width, avgLeadWidth * section * width));
    return copy;
  }

  /**
   * Maps all points in an ECG to list of {@link ECGPoint}
   *
   * @param ip
   * @return a list of {@link ECGPoint} containing every point valued pixel on
   *         an ECG image.
   */
  private List<Point> allPoints(final ImageProcessor ip) {
    final List<Point> points = new ArrayList<>();
    for (int w = 0; w < ip.getWidth(); w++)
      for (int h = 0; h < ip.getHeight(); h++) {
        final int p = ip.getPixel(w, h);
        if (p == blackValue)
          points.add(new ECGPoint(w, h));
      }
    return points;
  }

  class PointsOnY implements Predicate {

    private final int y;

    public PointsOnY(final int y) {
      super();
      this.y = y;
    }

    @Override
    public boolean evaluate(final Object point) {
      final Point p = (Point) point;
      return p.getY() == y;
    }
  }

  class PointsBeetweenX implements Predicate {

    private final int x1, x2;

    public PointsBeetweenX(final int x1, final int x2) {
      super();
      this.x1 = x1;
      this.x2 = x2;
    }

    /**
     * @param d
     * @param e
     */
    public PointsBeetweenX(final float d, final float e) {
      this(Math.round(d), Math.round(e));
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      return x1 <= point.x && point.x <= x2;
    }
  }

  /**
   * Lists all the points in a specific section of the ECG.
   *
   * @param relevantRanges
   *          - all the ranges considered relevant, that's the ranges for each
   *          significant height
   * @param lead
   *          - the lead number, from 0 to 3, this actually identifies the
   *          vertical position of a lead
   * @param allPointsInVerticalSection
   *          - all the points in a vertical section of the ECG
   * @return
   */
  private static List<Point> pointsInLead(final List<Range> relevantRanges,
      final int lead, final List<Point> allPointsInVerticalSection) {
    final List<Point> copy = new ArrayList<>(allPointsInVerticalSection);
    final Range range = relevantRanges.get(lead);
    CollectionUtils.filter(copy, byLatitude(range));
    return copy;
  }

  private static Predicate byLatitude(final Range range) {
    return new Predicate() {
      @Override
      public boolean evaluate(final Object opoint) {
        final Point point = (Point) opoint;
        return range.begin <= point.y && point.y <= range.end;
      }
    };
  }

  /**
   * Returns the points at a lead by taking the zero value out of them,
   * therefore taking them out of the lead context and making their values
   * relative to their zero.
   *
   * @param pointsInLead
   *          - points in lead to calibrate
   * @param zero
   *          - the zero's height of the lead
   * @return
   */
  private static List<Point> calibrate(final List<Point> pointsInLead,
      final int zero) {
    final List<Point> copy = new ArrayList<>(pointsInLead);
    CollectionUtils.transform(copy, new Transformer() {
      @Override
      public Object transform(final Object opoint) {
        final Point point = (Point) opoint;
        return new ECGPoint(point.x, zero - point.y);
      }
    });
    return copy;
  }
}
