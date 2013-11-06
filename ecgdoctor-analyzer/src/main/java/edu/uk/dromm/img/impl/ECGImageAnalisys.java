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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    final List<Point> points = allPoints(ip);
    final Point firstPoint = points.get(0);
    final Point lastPoint = points.get(points.size() - 1);
    final Collection<Point> pointsOnFirstLine = new ArrayList<Point>();
    pointsOnFirstLine.addAll(points);
    CollectionUtils.filter(pointsOnFirstLine, new Predicate() {
      @Override
      public boolean evaluate(final Object arg0) {
        final Point p = (Point) arg0;
        return p.getX() == firstPoint.getX();
      }
    });
    System.out.println("Max X: " + ip.getWidth());
    System.out.println("Max dist X: " + (lastPoint.getX() - firstPoint.getX()));
    System.out.println("Max Y: " + ip.getHeight());
    System.out.println("Max dist Y: " + (lastPoint.getY() - firstPoint.getY()));
    System.out.println("Size on first line: " + pointsOnFirstLine.size());
    System.out.println("Points on first line: "
        + Arrays.toString(pointsOnFirstLine.toArray()));
    // TODO Encontrar picos y guardar sus coordenadas
    drawTableXY(points);

    analizePointsXY(points, ip);

    return ip.getBufferedImage();
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

    private final int y;

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

  /**
   * @param ip
   */
  private void analizePointsXY(final List<Point> points, final ImageProcessor ip) {
    // for (final Point point : points) {
    // for (int x = 0; x < 1; x++) {
    // for (int y = 0; y < po; y++) {
    // final int point = ip.getPixel(x, y);
    // if (point != 0) {
    // System.out.println("primer punto: x = " + x + "; y = " + y
    // + "; valor: " + point);
    // }
    // }
    // }
    // }
    // }
  }

  /**
   * @param points
   */
  private void drawTableXY(final List<Point> points) {
    System.out.println("x,y");
    for (final Point point : points)
      System.out.println(point.getX() + "," + point.getY());
  }
}
