/**
 * 
 */
package edu.uk.dromm.img.impl;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import edu.uk.dromm.img.ImageProcess;

/**
 * @author magian
 * 
 */
public class ECGImageAnalisysTest {

  private final int blackValue = 0;

  @Test
  public void justRunIt() {
    final URL ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
    Assert.assertNotNull(ecgImage);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(ecgImage);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final ImageProcess pre = new ECGImagePreprocessing();
    final BufferedImage result = pre.process(bi);
    final ImageProcess ana = new ECGImageAnalisys(0);
    ana.process(result);
    // //
    // ImageProcessor ip = new BinaryProcessor(new ByteProcessor(bi));
    // List<Point> points = new ArrayList<Point>();
    // for (int w = 0; w < ip.getWidth(); w++) {
    // for (int h = 0; h < ip.getHeight(); h++) {
    // final int p = ip.getPixel(w, h);
    // if (p == blackValue) {
    // points.add(new Point(w, h));
    // }
    // }
    // }
    // drawByCell(points, ip);
    // drawTableXY(points);
    //
  }

  /**
   * @param points
   */
  private void drawTableXY(final List<Point> points) {
    System.out.println("x,y");
    for (final Point point : points) {
      System.out.println(point.getX() + "," + point.getY());
    }
  }

  private List<Point> getPoints(final ImageProcessor ip) {
    final List<Point> points = new ArrayList<Point>();
    for (int w = 0; w < ip.getWidth(); w++) {
      for (int h = 0; h < ip.getHeight(); h++) {
        final int p = ip.getPixel(w, h);
        if (p == blackValue) {
          points.add(new Point(w, h));
        }
      }
    }
    return points;
  }

  /**
   * @param points
   * @param ip
   */
  private void drawByCell(final List<Point> points, final ImageProcessor ip) {
    // System.out.print(points.size());
    final Point pointAux = new Point();
    for (int y = 0; y < ip.getHeight(); y++) {
      for (int x = 0; x < ip.getWidth(); x++) {
        pointAux.setLocation(x, y);
        System.out.print(points.contains(pointAux) ? blackValue + "," : ",");
      }
      System.out.println();
    }
  }
}
