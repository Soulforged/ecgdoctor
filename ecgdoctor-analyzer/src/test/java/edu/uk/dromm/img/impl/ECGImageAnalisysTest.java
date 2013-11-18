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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Test;

import edu.uk.dromm.img.ImageProcess;

/**
 * @author magian
 * 
 */
public class ECGImageAnalisysTest {

  @Test
  public void process(){
    final URL ecgImage = this.getClass().getResource("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    Assert.assertNotNull(ecgImage);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(ecgImage);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final ImageProcess pre = new ECGImagePreprocessing();
    final BufferedImage resultantBi = pre.process(bi);
    final ImageProcessor ip = new BinaryProcessor(
        new ByteProcessor(resultantBi));
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    obtainingZeroes(ana, ip);
    ana.process(resultantBi);
  }

  private void obtainingZeroes(final ECGImageAnalisys ecgim, final ImageProcessor ip) {
    final List<Point> zeroes = ecgim.zeroes(ip);
    Assert.assertFalse(zeroes.isEmpty());
    Assert.assertEquals(4, zeroes.size());
    System.out.println(zeroes);
  }

  @Test
  public void obtainingZeroesLineMethod(){
    final URL ecgImage = this.getClass().getResource("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    Assert.assertNotNull(ecgImage);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(ecgImage);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final ImageProcess pre = new ECGImagePreprocessing();
    final BufferedImage resultantBi = pre.process(bi);
    final ImageProcessor ip = new BinaryProcessor(
        new ByteProcessor(resultantBi));
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final Map<Integer, Integer> highestPointCount = new TreeMap<Integer,Integer>(new IntegerComparator());
    for(int i = 0; i < ip.getHeight(); i++){
      final List<Point> allPointsCopy = new ArrayList<Point>(allPoints);
      CollectionUtils.filter(allPointsCopy, new YPoints(i));
      highestPointCount.put(allPointsCopy.size(), i);
    }
    Assert.assertFalse(highestPointCount.isEmpty());
    System.out.println(highestPointCount);
    final Map<Integer, Collection<Point>> pointsPerY = new TreeMap<Integer, Collection<Point>>();
    final int nearNess = 1;
    for (final Point p : allPoints) {
      final Collection<Point> newPoints = new ArrayList<Point>(allPoints);
      CollectionUtils.filter(newPoints, new NearPoint(p, nearNess));
      pointsPerY.put(p.y, newPoints);
    }
    final Map<Integer, Point> firstPointPerY = new HashMap<Integer, Point>();
    for (final Integer y : pointsPerY.keySet())
      firstPointPerY.put(y, pointsPerY.get(y).iterator().next());
    System.out.println(firstPointPerY);
    final Collection<Integer> intersection = CollectionUtils.intersection(firstPointPerY.keySet(), highestPointCount.values());
    final List<Integer> sortableIntersection = new ArrayList<Integer>(intersection);
    Collections.sort(sortableIntersection);
    System.out.println(sortableIntersection);
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

  class XPoints implements Predicate{

    private final int x;

    public XPoints(final int x) {
      super();
      this.x = x;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point)p;
      if(point.x == x)
        return true;
      return false;
    }
  }

  class YPoints implements Predicate{

    private final int y;

    public YPoints(final int y) {
      super();
      this.y = y;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point)p;
      if(point.y == y)
        return true;
      return false;
    }
  }
}