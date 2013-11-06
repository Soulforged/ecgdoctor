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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uk.dromm.img.ImageProcess;

/**
 * @author magian
 * 
 */
public class ECGImageAnalisysTest {

  private final int blackValue = 0;
  private BufferedImage resultantBi;

  @Before
  public void init(){
    final URL ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
    Assert.assertNotNull(ecgImage);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(ecgImage);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final ImageProcess pre = new ECGImagePreprocessing();
    resultantBi = pre.process(bi);
  }

  @Test
  public void justRunIt() {
    final ImageProcess ana = new ECGImageAnalisys(0);
    ana.process(resultantBi);
  }

  @Test
  public void pointsOnZeroY(){
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final ImageProcessor ip = new BinaryProcessor(new ByteProcessor(resultantBi));
    final List<Point> ps = ana.allPoints(ip);
    final List<Point> zeroes = new ArrayList<Point>();
    final Point lastPoint = ps.get(ps.size() - 1);
    final int lastPointOnX = Double.valueOf(lastPoint.x * 0.01).intValue();
    CollectionUtils.filter(ps, new PointsBeetweenX(0, lastPointOnX));
    Assert.assertFalse(ps.isEmpty());
    for(final Point p : ps)
      if(p.x > lastPointOnX)
        Assert.fail("There shouldn't be any points past " + lastPointOnX);
    final Map<Integer, Set<Point>> pointsOnLine = new TreeMap<Integer, Set<Point>>(new Comparator<Integer>() {
      @Override
      public int compare(final Integer o1, final Integer o2) {
        return o1.compareTo(o2);
      }
    });
    //    ana.zeroes(zeroes, ps, 0, lastPoint.y, lastPointOnX);
    System.out.println(Arrays.toString(zeroes.toArray()));
  }

  //  private boolean isZero(final Point p, final ImageProcessor ip){
  //
  //  }

  class PointsBeetweenX implements Predicate{

    private final int x1,x2;

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

  class MinimumX implements Predicate{


    @Override
    public boolean evaluate(final Object arg0) {
      // TODO Auto-generated method stub
      return false;
    }
  }

  class XOnLine implements Predicate{

    private final int y;

    public XOnLine(final int y) {
      super();
      this.y = y;
    }

    @Override
    public boolean evaluate(final Object p) {
      final Point point = (Point)p;
      return point.y == y;
    }
  }
}
