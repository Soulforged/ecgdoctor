/**
 * 
 */
package edu.uk.dromm.img.impl;

import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import edu.uk.dromm.img.ImageProcess;

/**
 * @author dicardo
 * 
 */
public class HistogramToBinaryTest {

  private final ImageProcess histogramToBinary = new HistogramToBinary(50.0);

  @Test
  public void processLeadsToATwoBitImage() {
    try {
      final URL ecgImage = this.getClass().getResource(
          "/image/ecg-output-lrg.jpg");
      Assert.assertNotNull(ecgImage);
      final BufferedImage bi = ImageIO.read(ecgImage);
      final BufferedImage result = histogramToBinary.process(bi);
      final ImageProcessor ip = new BinaryProcessor(new ByteProcessor(result));
      Assert.assertEquals(256, ip.getHistogramSize());
    } catch (final IOException e) {
      Assert.fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

}