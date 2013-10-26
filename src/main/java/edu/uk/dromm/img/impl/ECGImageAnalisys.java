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
    final List<Point> points = new ArrayList<Point>();
    for (int w = 0; w < ip.getWidth(); w++) {
      for (int h = 0; h < ip.getHeight(); h++) {
        final int p = ip.getPixel(w, h);
        if (p == blackValue) {
          points.add(new Point(w, h));
        }
      }
    }
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
    System.out.println("Points on first line: "
        + Arrays.toString(pointsOnFirstLine.toArray()));
    // TODO Encontrar picos y guardar sus coordenadas
    return ip.getBufferedImage();
  }

}
