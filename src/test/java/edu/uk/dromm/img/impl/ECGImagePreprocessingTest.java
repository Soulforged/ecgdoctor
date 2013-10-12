/**
 * 
 */
package edu.uk.dromm.img.impl;

import ij.process.ColorProcessor;
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
public class ECGImagePreprocessingTest {

  private final ImageProcess histogramSegmentation = new ECGImagePreprocessing();

  @Test
  public void processLeadsToATwoBitImage() {
    try {
      final URL ecgImage = this.getClass().getResource("/image/ecg-byn.jpg");
      Assert.assertNotNull(ecgImage);
      final BufferedImage bi = ImageIO.read(ecgImage);
      final BufferedImage result = histogramSegmentation.process(bi);
      final ImageProcessor ip = new ColorProcessor(result);
      final int[] histogram = ip.getHistogram();
      Assert.assertTrue(histogram[0] > 0);
      Assert.assertTrue(histogram[255] > 0);
      for (int i = 1; i < histogram.length - 1; i++) {
        Assert.assertTrue(histogram[i] == 0);
      }
    } catch (final IOException e) {
      Assert.fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

}