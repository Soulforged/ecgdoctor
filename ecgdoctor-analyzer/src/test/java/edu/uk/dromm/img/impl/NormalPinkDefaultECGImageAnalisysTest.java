/**
 *
 */
package edu.uk.dromm.img.impl;

import edu.uk.dromm.img.DefaultECGImageAnalisys;
import edu.uk.dromm.img.ECGParameters;
import edu.uk.dromm.img.ImageProcess;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author magian
 *
 */
public class NormalPinkDefaultECGImageAnalisysTest {

  private static final String IMAGE = "/image/ecg-pink-typical-normal.gif";

  @Test
  public void canFillSpacesBetweenPoints() throws IOException {
    final URL ecgImage = NormalPinkDefaultECGImageAnalisysTest.class
        .getResource(IMAGE);
    Assert.assertNotNull(ecgImage);
    BufferedImage bi = null;
    try {
      bi = ImageIO.read(ecgImage);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final ImageProcess pre = new ECGImagePreprocessing();
    final BufferedImage resultantBi = pre.process(bi);
    final DefaultECGImageAnalisys ana = new DefaultECGImageAnalisys(0);
    final ECGParameters ecgParams = ana.process(resultantBi);

    Assert.assertEquals(1050, Math.round(ecgParams.getRrDiff()));
    Assert.assertEquals(1, Math.round(ecgParams.getrPeak()));
  }

}
