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

import edu.uk.dromm.img.ECGPoint;
import edu.uk.dromm.img.ImageProcess;

/**
 * @author magian
 * 
 */
public class ECGImageAnalisys implements ImageProcess {

  private int blackValue = 0;

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
    final double gridCellProportionY = 0.037593985;
    final double gridCellProportionX = 0.0208333333;
    final double cellHeightInPx = ip.getHeight() * gridCellProportionY;
    final double cellWidthInPx = ip.getWidth() * gridCellProportionX;
    final double maxY = cellHeightInPx * 3;
    return ip.getBufferedImage();
  }

  public List<Point> zeroes(final ImageProcessor ip){
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> ps = ana.allPoints(ip);
    final Point lastPoint = ps.get(ps.size() - 1);
    final int lastPointOnX = Double.valueOf(lastPoint.x * 0.01).intValue();
    CollectionUtils.filter(ps, new PointsBeetweenX(0, lastPointOnX));
    for (final Point p : ps)
      if (p.x > lastPointOnX)
        Collections.sort(ps, new Comparator<Point>() {
          @Override
          public int compare(final Point o1, final Point o2) {
            if(o1.equals(o2))
              return 0;
            if(o1.x < o2.x)
              return -1;
            else
              return 1;
          }
        });
    final Map<Integer, Collection<Point>> pointsPerY = new TreeMap<Integer, Collection<Point>>(new IntegerComparator());
    final int nearNess = 1;
    for(final Point p : ps){
      final Collection<Point> newPoints = new ArrayList<Point>(ps);
      CollectionUtils.filter(newPoints, new NearPoint(p, nearNess));
      pointsPerY.put(p.y, newPoints);
    }
    final Map<Integer, Point> firstPointPerY = new HashMap<Integer, Point>();
    for(final Integer y : pointsPerY.keySet())
      firstPointPerY.put(y, pointsPerY.get(y).iterator().next());
    final Set<Integer> averages = averageToMinimum(firstPointPerY.keySet());
    final List<Point> zeroes = new ArrayList<Point>();
    for(final Integer y : averages)
      zeroes.add(firstPointPerY.get(y));
    return zeroes;
  }

  public List<Point> allPoints(final ImageProcessor ip){
    final List<Point> points = new ArrayList<Point>();
    for (int w = 0; w < ip.getWidth(); w++)
      for (int h = 0; h < ip.getHeight(); h++) {
        final int p = ip.getPixel(w, h);
        if (p == blackValue)
          points.add(new ECGPoint(w, h));
      }
    return points;
  }

  public void zeroes(final List<Point> zeroes, final List<Point> points, final int y, final int maxY){
    final List<Point> pointsOnHorizontalLine = new ArrayList<Point>();
    pointsOnHorizontalLine.addAll(points);
    final Point zero = (Point)CollectionUtils.find(pointsOnHorizontalLine, new PointsOnY(y, points));
    if(zero != null)
      zeroes.add(zero);
    if(y >= maxY)
      return;
    else
      zeroes(zeroes, points, y+1, maxY);
  }

  private Set<Integer> averageToMinimum(final Collection<Integer> ints){
    final Set<Integer> averages = new TreeSet<Integer>(new IntegerComparator());
    for(final Integer key : ints){
      final List<Integer> filtered = new ArrayList<Integer>(ints);
      CollectionUtils.filter(filtered, new KeysNear(key));
      averages.add(average(filtered));
    }
    if(averages.size() == ints.size())
      return averages;
    return averageToMinimum(averages);
  }

  private Integer average(final List<Integer> numbers){
    int sum = 0;
    for(final Integer n : numbers)
      sum += n;
    return sum / numbers.size();
  }

  class IntegerComparator implements Comparator<Integer>{
    @Override
    public int compare(final Integer o1, final Integer o2) {
      return o2.compareTo(o1);
    }
  }

  class PointsOnY implements Predicate {

    private final int y;
    private final Collection<Point> allPoints;

    public PointsOnY(final int y, final Collection<Point> allPoints) {
      super();
      this.y = y;
      this.allPoints = new ArrayList<Point>(allPoints);
      CollectionUtils.filter(allPoints, new PointInRange(y));
    }

    @Override
    public boolean evaluate(final Object point) {
      final Point p = (Point) point;
      return p.getY() == y && allPoints.size() >= 4;
    }
  }

  class PointInRange implements Predicate{

    private final double y;

    public PointInRange(final int y) {
      super();
      this.y = y;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point pt = (Point)p;
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

  class NearPoint implements Predicate{

    private final Point centralPoint;
    private final int nearNess;

    public NearPoint(final Point central, final int nearNess) {
      super();
      this.nearNess = nearNess;
      centralPoint = central;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point)p;
      if(point.equals(centralPoint))
        return true;
      else if(point.x == centralPoint.x)
        return Math.abs(point.y - centralPoint.y) == nearNess;
      else if(point.y == centralPoint.y)
        return Math.abs(point.x - centralPoint.x) == nearNess;
      return false;
    }
  }

  class KeysNear implements Predicate{

    private final int key;

    public KeysNear(final int key) {
      super();
      this.key = key;
    }

    @Override
    public boolean evaluate(final Object i) {
      final Integer integer = (Integer)i;
      return Math.abs(integer - key) <= 1;
    }
  }

  class PointsInDistance implements Predicate{

    private final double distance;
    private final Point ref;

    public PointsInDistance(final double distance, final Point ref) {
      super();
      this.distance = distance;
      this.ref = ref;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point)p;
      return ref.distance(point) <= distance;
    }
  }
}
