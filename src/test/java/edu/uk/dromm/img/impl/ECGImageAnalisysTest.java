/**
 * 
 */
package edu.uk.dromm.img.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
  }
}
