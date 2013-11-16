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

}
