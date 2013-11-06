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

  @Test
  public void obtainZeroes(){
    final URL ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
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
    obtainingZeroes(ip);
  }

  private void obtainingZeroes(final ImageProcessor ip) {
    final ECGImageAnalisys ana = new ECGImageAnalisys(0);
    final List<Point> zeroes = ana.zeroes(ip);
    Assert.assertFalse(zeroes.isEmpty());
    Assert.assertEquals(3, zeroes.size());
  }

}
