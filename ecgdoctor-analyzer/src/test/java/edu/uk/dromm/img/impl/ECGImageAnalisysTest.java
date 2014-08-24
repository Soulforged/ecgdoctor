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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import edu.uk.dromm.img.ECGPoint;
import edu.uk.dromm.img.ImageProcess;
import edu.uk.dromm.img.ecg.exception.InvalidECGSectionException;
import edu.uk.dromm.img.impl.ECGImageAnalisys.PointCount;

/**
 * @author magian
 *
 */
public class ECGImageAnalisysTest {

  private static BinaryProcessor bp;

  @Test
  @Ignore
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

  @Test
  public void highestContinuousPointCountPerHeightCanBeUsedAsAFactorForZeroing(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> allPointsI = ana.allPoints(allPoints,1,ip.getWidth());
    final Set<Integer> heightsOrdered = ana.differentHeights(allPointsI);
    final Map<Integer, List<Point>> allPointsPerHeight = ana.allPointsPerHeight(allPointsI, heightsOrdered);
    final Map<Integer, List<Integer>> allLenghtsPerHeight = new HashMap<>();
    for(final Entry<Integer, List<Point>> entry : allPointsPerHeight.entrySet()){
      final List<Integer> lengths = new ArrayList<>();
      final List<Point> list = entry.getValue();
      int count = 0;
      for(int i = 0;i < list.size() - 1;i++){
        final int diff = list.get(i + 1).x - list.get(i).x;
        if(diff <= 1)
          count++;
        else {
          if(count > 0)
            lengths.add(count);
          count = 0;
        }
      }
      if(!lengths.isEmpty())
        allLenghtsPerHeight.put(entry.getKey(), lengths);
    }
    System.out.println(allLenghtsPerHeight);
  }

  @Test
  public void highestPointCountPerSectionMatchesZeroesOnThoseSections(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> allPointsI = ana.allPoints(allPoints,1,ip.getWidth());
    final Set<Integer> heightsOrdered = ana.differentHeights(allPointsI);
    final Set<PointCount> counts = ana.countPointsPerHeight(ana.allPointsPerHeight(allPointsI, heightsOrdered), 0, ip.getWidth());
    System.out.println(counts);
  }

  @Test
  public void scanningAllPointsForHeightsReturnsAllTheDifferentHeightsInDescendingOrder(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final Set<Integer> heightsOrdered = ana.differentHeights(allPoints);
    Assert.assertEquals(487, heightsOrdered.size());
    final Iterator<Integer> heightsIterator = heightsOrdered.iterator();
    final Integer first = heightsIterator.next();
    Assert.assertTrue(first < heightsIterator.next());
    Integer lastHeight = first;
    while(heightsIterator.hasNext())
      lastHeight = heightsIterator.next();
    Assert.assertTrue(first < lastHeight);
    Assert.assertEquals(Integer.valueOf(2), first);
    Assert.assertEquals(Integer.valueOf(655), lastHeight);
    Assert.assertTrue(lastHeight < ip.getHeight());
  }

  @Test
  public void allPointsGivesAGivenCount(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    Assert.assertEquals(8335, allPoints.size());
  }

  @Test
  public void allPointsAtAGivenSectionGiveAGivenCount(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    try{
      ana.allPoints(allPoints, 0, ip.getWidth());
      Assert.fail("ECG segmentation is not 0 based, first index is 1, therefore "
          + "trying to get the points in any section below 1 is wrong");
    }catch(final InvalidECGSectionException e){
    }
    final List<Point> all1Points = ana.allPoints(allPoints, 1, ip.getWidth());
    Assert.assertEquals(1519, all1Points.size());
    final List<Point> all2Points = ana.allPoints(allPoints, 2, ip.getWidth());
    Assert.assertEquals(1643, all2Points.size());
    final List<Point> all3Points = ana.allPoints(allPoints, 3, ip.getWidth());
    Assert.assertEquals(2173, all3Points.size());
    final List<Point> all4Points = ana.allPoints(allPoints, 4, ip.getWidth());
    Assert.assertEquals(2514, all4Points.size());
    Assert.assertTrue(8335 >= all1Points.size() + all2Points.size()
        + all3Points.size() + all4Points.size());
    try{
      ana.allPoints(allPoints, 5, ip.getWidth());
      Assert.fail("Section 5 does not exist in a typical ECG, therefore trying "
          + "to get the points in that section is conceptually wrong.");
    }catch(final InvalidECGSectionException e){
    }
  }

  @Test
  public void pointCountRemainsInsideSection(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> all1Points = ana.allPoints(allPoints, 1, ip.getWidth());
    final Set<Integer> heights = ana.differentHeights(all1Points);
    final Map<Integer, List<Point>> pointsPerHeight = ana.allPointsPerHeight(all1Points, heights);
    final Set<PointCount> counts = ana.countPointsPerHeight(pointsPerHeight, 0, ip.getWidth());
    Assert.assertEquals(heights.size(), counts.size());
    int totalCount = 0;
    for(final PointCount pc : counts)
      totalCount += pc.count;
    Assert.assertEquals(all1Points.size(), totalCount);
  }

  @Test
  public void scanningAHeightForPointsReturnsThePointsAtThatHeight(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final List<Point> pointsFiltered = ana.allPointsAt(219,allPoints);
    Assert.assertEquals(88, pointsFiltered.size());
  }

  @Test
  public void scanningAtAllHeightsReturnsThePointsAtThoseHeights(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final Set<Integer> differentHeights = ana.differentHeights(allPoints);
    final Map<Integer, List<Point>> pointsClassified = ana.allPointsPerHeight(allPoints, differentHeights);
    Assert.assertEquals(differentHeights, pointsClassified.keySet());
    Assert.assertEquals(differentHeights.size(), pointsClassified.size());
    Assert.assertEquals(88, pointsClassified.get(219).size());
    int totalCount = 0;
    for(final List<Point> pc : pointsClassified.values())
      totalCount += pc.size();
    Assert.assertEquals("The sum of all points at all heights must be equal "
        + "to the count of all points.",allPoints.size(), totalCount);
  }

  @Test
  public void countingHorizontallyShouldCountAllPointsAtAGivenHeight(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final PointCount pointsCount = ana.countPointsAtHeight(ana.allPointsAt(219, allPoints), 0, ip.getWidth());
    Assert.assertEquals(219, pointsCount.y);
    Assert.assertEquals(88, pointsCount.count);
  }

  @Test
  public void countingHorizontallyShouldCountAllPointsAtAllHeights(){
    final ImageProcessor ip = getImageProcessor("/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg");
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> allPoints = ana.allPoints(ip);
    final Set<Integer> differentHeights = ana.differentHeights(allPoints);
    final Map<Integer, List<Point>> allPointsPerHeight = ana.allPointsPerHeight(allPoints, differentHeights);
    final Set<PointCount> pointsCount = ana.countPointsPerHeight(allPointsPerHeight, 0,ip.getWidth());
    Assert.assertEquals(differentHeights.size(), pointsCount.size());
  }

  private static ImageProcessor getImageProcessor(final String imageResourcePath){
    if(bp == null){
      final URL ecgImage = ECGImageAnalisysTest.class.getResource(imageResourcePath);
      Assert.assertNotNull(ecgImage);
      BufferedImage bi = null;
      try {
        bi = ImageIO.read(ecgImage);
      } catch (final IOException e) {
        e.printStackTrace();
      }
      final ImageProcess pre = new ECGImagePreprocessing();
      final BufferedImage resultantBi = pre.process(bi);
      bp = new BinaryProcessor(
          new ByteProcessor(resultantBi));
    }
    return bp;
  }

  private void obtainingZeroes(final ECGImageAnalisys ecgim, final ImageProcessor ip) {
    final List<Point> zeroes = ecgim.zeroes(ip);
    Assert.assertFalse(zeroes.isEmpty());
    Assert.assertEquals(4, zeroes.size());
    assertZeroesInRange(zeroes, Arrays.asList(new Point[]{new ECGPoint(0, 70), new ECGPoint(0, 219), new ECGPoint(0, 365), new ECGPoint(0, 516)}), 2.0);
    System.out.println(zeroes);
  }

  private void assertZeroesInRange(final List<Point> points, final List<Point> asserted, final double margin){
    Assert.assertFalse(asserted.isEmpty());
    Assert.assertEquals(points.size(), asserted.size());
    for(int i = 0; i < points.size(); i++){
      final Point org = points.get(i);
      final Point ass = asserted.get(i);
      //      Assert.assertTrue(String.format("Point: %s, at index '%s' should be at a margin of %s from Point: %s", org, i, margin, ass),ass.x - margin <= org.x && org.x <= ass.x + margin);
      Assert.assertTrue(String.format("Point: %s, at index '%s' should be at a margin of %s from Point: %s", org, i, margin, ass), ass.y - margin <= org.y && org.y <= ass.y + margin);
    }
  }

  @Test
  @Ignore
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
