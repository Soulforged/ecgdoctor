/**
 *
 */
package edu.uk.dromm.img.impl;

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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.list.TreeList;

import edu.uk.dromm.img.DefaultECGImageAnalysis;
import edu.uk.dromm.img.ECGPoint;
import edu.uk.dromm.img.ImageProcess;
import edu.uk.dromm.img.ecg.exception.InvalidECGSectionException;

/**
 * @author magian
 *
 */
@Deprecated
public class ECGImageAnalisys implements ImageProcess {

  private int blackValue = 0;
  private final float avgLeadWidth = 0.2400620201f;

  /**
   * @param blackValue
   */
  public ECGImageAnalisys(final int blackValue) {
    super();
    this.blackValue = blackValue;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.uk.dromm.img.ImageProcess#process(java.awt.image.BufferedImage)
   */
  @Override
  public BufferedImage process(final BufferedImage bi) {
    final ImageProcessor ip = new BinaryProcessor(new ByteProcessor(bi));
    final List<Point> zeroes = zeroes(ip);
    final List<Point> firstThreeZeroes = zeroes.subList(0, 3);
    final double gridCellProportionY = 0.037593985;
    final double gridCellProportionX = 0.0208333333;
    final double cellHeightInPx = ip.getHeight() * gridCellProportionY;
    final double cellWidthInPx = ip.getWidth() * gridCellProportionX;
    final double leadDistance = cellWidthInPx * 12.5;
    final int maxY = new Double(cellHeightInPx * 3).intValue();
    final int firstLeadMark = new Double(leadDistance).intValue();
    final int secondLeadMark = firstLeadMark * 2;
    final int thirdLeadMark = firstLeadMark * 3;
    final int fourthLeadMark = Math.max(firstLeadMark * 4, ip.getWidth());
    final int firstStripeUpper = firstThreeZeroes.get(0).y + maxY;
    final int firstStripeLower = firstThreeZeroes.get(0).y - maxY;
    final int secondStripeUpper = firstThreeZeroes.get(1).y + maxY;
    final int secondStripeLower = firstThreeZeroes.get(1).y - maxY;
    final int thirdStripeUpper = firstThreeZeroes.get(2).y + maxY;
    final int thirdStripeLower = firstThreeZeroes.get(2).y - maxY;
    final List<Point> allPoints = allPoints(ip);
    List<Point> leadIPoints = pointsInLead(firstLeadMark, firstStripeUpper,
        firstStripeLower, allPoints);
    CollectionUtils.transform(leadIPoints, new Level(zeroes.get(0)));
    leadIPoints = fillGaps(leadIPoints);
    List<Point> leadAVRPoints = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadAVRPoints, new PointsInDistance(
        firstStripeLower, firstStripeUpper, firstLeadMark, secondLeadMark));
    CollectionUtils.transform(leadAVRPoints, new Level(zeroes.get(0)));
    leadAVRPoints = fillGaps(leadAVRPoints);
    List<Point> leadV1Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV1Points, new PointsInDistance(firstStripeLower,
        firstStripeUpper, secondLeadMark, thirdLeadMark));
    CollectionUtils.transform(leadV1Points, new Level(zeroes.get(0)));
    leadV1Points = fillGaps(leadV1Points);
    List<Point> leadV4Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV4Points, new PointsInDistance(firstStripeLower,
        firstStripeUpper, thirdLeadMark, fourthLeadMark));
    CollectionUtils.transform(leadV4Points, new Level(zeroes.get(0)));
    leadV4Points = fillGaps(leadV4Points);
    List<Point> leadIIPoints = pointsInLead(firstLeadMark, secondStripeUpper,
        secondStripeLower, allPoints);
    CollectionUtils.transform(leadIIPoints, new Level(zeroes.get(1)));
    leadIIPoints = fillGaps(leadIIPoints);
    List<Point> leadAVLPoints = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadAVLPoints, new PointsInDistance(
        secondStripeLower, secondStripeUpper, firstLeadMark, secondLeadMark));
    CollectionUtils.transform(leadAVLPoints, new Level(zeroes.get(1)));
    leadAVLPoints = fillGaps(leadAVLPoints);
    List<Point> leadV2Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV2Points, new PointsInDistance(
        secondStripeLower, secondStripeUpper, secondLeadMark, thirdLeadMark));
    CollectionUtils.transform(leadV2Points, new Level(zeroes.get(1)));
    leadV2Points = fillGaps(leadV2Points);
    List<Point> leadV5Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV5Points, new PointsInDistance(
        secondStripeLower, secondStripeUpper, thirdLeadMark, fourthLeadMark));
    CollectionUtils.transform(leadV5Points, new Level(zeroes.get(1)));
    leadV5Points = fillGaps(leadV5Points);
    List<Point> leadIIIPoints = pointsInLead(firstLeadMark, thirdStripeUpper,
        thirdStripeLower, allPoints);
    CollectionUtils.transform(leadIIIPoints, new Level(zeroes.get(2)));
    leadIIIPoints = fillGaps(leadIIIPoints);
    List<Point> leadAVFPoints = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadAVFPoints, new PointsInDistance(
        thirdStripeLower, thirdStripeUpper, firstLeadMark, secondLeadMark));
    CollectionUtils.transform(leadAVFPoints, new Level(zeroes.get(2)));
    leadAVFPoints = fillGaps(leadAVFPoints);
    List<Point> leadV3Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV3Points, new PointsInDistance(thirdStripeLower,
        thirdStripeUpper, secondLeadMark, thirdLeadMark));
    CollectionUtils.transform(leadV3Points, new Level(zeroes.get(2)));
    leadV3Points = fillGaps(leadV3Points);
    List<Point> leadV6Points = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadV6Points, new PointsInDistance(thirdStripeLower,
        thirdStripeUpper, thirdLeadMark, fourthLeadMark));
    CollectionUtils.transform(leadV6Points, new Level(zeroes.get(2)));
    leadV6Points = fillGaps(leadV6Points);
    print("LEAD II", leadIIPoints);
    return ip.getBufferedImage();
  }

  /**
   * @param leadMark
   * @param stripeUpperBound
   * @param stripeLowerBound
   * @param allPoints
   * @return
   */
  private List<Point> pointsInLead(final int leadMark,
      final int stripeUpperBound, final int stripeLowerBound,
      final List<Point> allPoints) {
    final List<Point> leadIPoints = new ArrayList<Point>(allPoints);
    CollectionUtils.filter(leadIPoints, new PointsInDistance(stripeLowerBound,
        stripeUpperBound, 0, leadMark));
    return leadIPoints;
  }

  private void print(final String title, final List<Point> points) {
    System.out
    .println(String.format(" ============= %s ============= ", title));
    for (final Point p : points)
      System.out.println(p);
  }

  private List<Point> fillGaps(final List<Point> points) {
    final List<Point> withGapsFilled = new ArrayList<Point>(points.size());
    for (int i = 0; i < points.size() - 1; i++) {
      final Point point = points.get(i);
      final int diffX = points.get(i + 1).x - point.x;
      if (diffX != 0) {
        final int diffY = points.get(i + 1).y - point.y;
        fillGap(diffX, diffY, withGapsFilled, point);
      } else
        withGapsFilled.add(point);
    }
    return withGapsFilled;
  }

  private void fillGap(final int gapX, final int gapY,
      final List<Point> points, final Point currentPoint) {
    final float diff = gapY / gapX;
    for (int i = 1; i < gapX + 1; i++)
      points.add(new ECGPoint(currentPoint.x + i, new Float(currentPoint.y
          + diff).intValue()));
  }

  class Level implements Transformer {

    private final Point zero;

    public Level(final Point zero) {
      super();
      this.zero = zero;
    }

    @Override
    public Point transform(final Object p) {
      final Point point = (Point) p;
      return new ECGPoint(point.x, (point.y - zero.y) * -1);
    }
  }

  /**
   * @param allPointsAtHeight
   * @param width
   * @return
   */
  public Set<PointCount> countPointsPerHeight(
      final Map<Integer, List<Point>> allPointsAtHeight, final int offSet,final int width) {
    final Set<PointCount> counts = new TreeSet<>();
    for(final Integer h: allPointsAtHeight.keySet())
      counts.add(countPointsAtHeight(allPointsAtHeight.get(h), offSet, width));
    return counts;
  }

  public Set<Integer> differentHeights(final List<Point> allPoints){
    final Set<Integer> heights = new TreeSet<>();
    for(final Point p : allPoints)
      heights.add(p.y);
    return heights;
  }

  public List<Point> allPointsAt(final int y, final List<Point> allPoints){
    final List<Point> copy = new TreeList(allPoints);
    CollectionUtils.filter(copy, new PointsOnY(y));
    return copy;
  }

  /**
   * @param allPoints
   * @param differentHeights
   * @return
   */
  public Map<Integer, List<Point>> allPointsPerHeight(final List<Point> allPoints,
      final Set<Integer> differentHeights) {
    final Map<Integer, List<Point>> allPointsPerHeight = new HashMap<>(differentHeights.size());
    for(final Integer height : differentHeights)
      allPointsPerHeight.put(height, allPointsAt(height, allPoints));
    return allPointsPerHeight;
  }

  public PointCount countPointsAtHeight(final List<Point> allPointsAtHeight, final int offSet, final int width){
    final List<Point> allPointsCopy = new TreeList(allPointsAtHeight);
    CollectionUtils.filter(allPointsCopy, new PointsBeetweenX(offSet, width));
    return new PointCount(allPointsAtHeight.get(0).y, allPointsCopy.size());
  }

  /**
   * @param ip
   * @param i
   * @return
   */
  public List<Point> allPoints(final List<Point> allPoints, final int i, final int width) {
    if(i < 1 || i > 4)
      throw new InvalidECGSectionException();
    final List<Point> copy = new TreeList(allPoints);
    final int prevI = i -1;
    CollectionUtils.filter(copy, new PointsBeetweenX(prevI * avgLeadWidth * width,
        avgLeadWidth * i * width));
    return copy;
  }

  class PointCount implements Comparable<PointCount>{
    public int y;
    public int count;
    public PointCount(final int y, final int count) {
      super();
      this.y = y;
      this.count = count;
    }
    @Override
    public String toString() {
      return "["+y + "," + count + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + y;
      return result;
    }
    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final PointCount other = (PointCount) obj;
      if (y != other.y)
        return false;
      return true;
    }

    @Override
    public int compareTo(final PointCount o) {
      if(count > o.count)
        return -1;
      if(count < o.count)
        return 1;
      return Integer.valueOf(y).compareTo(o.y);
    }
  }

  /**
   * Obtain the zeroes of each signal graphed in the image.
   * <ol>
   * <li>Filter a vertical lane of only the first points to the left margin. A
   * %1 of the total image horizontal size.</li>
   * <li>Obtain closest points to each of the central points in that lane by x/y inside margin.</li>
   * </ol>
   *
   * @param ip
   * @return
   */
  public List<Point> zeroes(final ImageProcessor ip) {
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> ps = new ArrayList<Point>(allPoints);
    final Point lastPoint = ps.get(ps.size() - 1);
    //    final int lastPointOnX = Double.valueOf(lastPoint.x).intValue();
    final int lastPointOnX = ip.getWidth();

    CollectionUtils.filter(ps, new PointsBeetweenX(0, lastPointOnX));
    for (final Point p : ps)
      if (p.x > lastPointOnX)
        Collections.sort(ps, new Comparator<Point>() {
          @Override
          public int compare(final Point o1, final Point o2) {
            if (o1.equals(o2))
              return 0;
            if (o1.x < o2.x)
              return -1;
            else
              return 1;
          }
        });

    final Map<Integer, Collection<Point>> pointsPerY = new TreeMap<Integer, Collection<Point>>(
        new IntegerComparator());
    final int nearNess = 1;
    for (final Point p : ps) {
      final Collection<Point> newPoints = new ArrayList<Point>(ps);
      CollectionUtils.filter(newPoints, new NearPoint(p, nearNess));
      pointsPerY.put(p.y, newPoints);
    }

    final Map<Integer, Integer> countPointsPerY = new TreeMap<>(new IntegerComparator());
    for(final Point p : ps){
      final Collection<Point> newPoints = new ArrayList<Point>(allPoints);
      CollectionUtils.filter(newPoints, new Predicate() {
        @Override
        public boolean evaluate(final Object arg0) {
          final Point po = (Point)arg0;
          return po.y == p.y;
        }
      });
      countPointsPerY.put(p.y, newPoints.size());
    }

    final Map<Integer, Point> firstPointPerY = new HashMap<Integer, Point>();
    for (final Integer y : pointsPerY.keySet())
      firstPointPerY.put(y, pointsPerY.get(y).iterator().next());
    final Set<Integer> averages = averageToMinimum(firstPointPerY.keySet());
    System.out.println(averages);
    final List<Point> zeroes = new ArrayList<Point>();
    for (final Integer y : averages)
      zeroes.add(firstPointPerY.get(y));
    return zeroes;
  }

  public List<Point> allPoints(final ImageProcessor ip) {
    final List<Point> points = new ArrayList<Point>();
    for (int w = 0; w < ip.getWidth(); w++)
      for (int h = 0; h < ip.getHeight(); h++) {
        final int p = ip.getPixel(w, h);
        if (p == blackValue)
          points.add(new ECGPoint(w, h));
      }
    return points;
  }

  public void zeroes(final List<Point> zeroes, final List<Point> points,
      final int y, final int maxY) {
    final List<Point> pointsOnHorizontalLine = new ArrayList<Point>();
    pointsOnHorizontalLine.addAll(points);
    final Point zero = (Point) CollectionUtils.find(pointsOnHorizontalLine,
        new PointsOnYOld(y, points));
    if (zero != null)
      zeroes.add(zero);
    if (y >= maxY)
      return;
    else
      zeroes(zeroes, points, y + 1, maxY);
  }

  private Set<Integer> averageToMinimum(final Collection<Integer> ints) {
    final Set<Integer> averages = new TreeSet<Integer>(new IntegerComparator());
    for (final Integer key : ints) {
      final List<Integer> filtered = new ArrayList<Integer>(ints);
      CollectionUtils.filter(filtered, new KeysNear(key));
      averages.add(average(filtered));
    }
    if (averages.size() == ints.size())
      return averages;
    return averageToMinimum(averages);
  }

  private Integer average(final List<Integer> numbers) {
    int sum = 0;
    for (final Integer n : numbers)
      sum += n;
    return sum / numbers.size();
  }

  class IntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(final Integer o1, final Integer o2) {
      return o2.compareTo(o1) * -1;
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

  class PointsOnY implements Predicate {

    private final int               y;

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

  class PointsOnYOld implements Predicate {

    private final int               y;
    private final Collection<Point> allPoints;

    public PointsOnYOld(final int y, final Collection<Point> allPoints) {
      super();
      this.y = y;
      this.allPoints = allPoints;
      CollectionUtils.filter(this.allPoints, new PointInRange(y));
    }

    @Override
    public boolean evaluate(final Object point) {
      final Point p = (Point) point;
      return p.getY() == y;
    }
  }

  class PointInRange implements Predicate {

    private final double y;

    public PointInRange(final int y) {
      super();
      this.y = y;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point pt = (Point) p;
      return y <= pt.x && pt.x <= y;
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

  class XOnLine implements Predicate {

    private final int y;

    public XOnLine(final int y) {
      super();
      this.y = y;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      return point.y == y;
    }
  }

  class NearPoint implements Predicate {

    private final Point centralPoint;
    private final int   nearNess;

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
        return Math.abs(point.y - centralPoint.y) <= nearNess;
      else if (point.y == centralPoint.y)
        return Math.abs(point.x - centralPoint.x) <= nearNess;
      return false;
    }
  }

  class KeysNear implements Predicate {

    private final int key;

    public KeysNear(final int key) {
      super();
      this.key = key;
    }

    @Override
    public boolean evaluate(final Object i) {
      final Integer integer = (Integer) i;
      return Math.abs(integer - key) <= 1;
    }
  }

  class PointsInDistance implements Predicate {

    private final int x1, x2;
    private final int y1, y2;

    public PointsInDistance(final int y1, final int y2, final int x1,
        final int x2) {
      super();
      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point) p;
      return y1 <= point.y && point.y <= y2 && x1 <= point.x && point.x <= x2;
    }
  }


}
