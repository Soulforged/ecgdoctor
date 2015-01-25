/**
 *
 */
package edu.uk.dromm.img.impl;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageStatistics;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dicardo
 *
 */
public class PinkStripedWorkflowTest {

  @Test
  public void variants() {
    final URL[] resources = new URL[] {
    // this.getClass().getResource(
    // "/image/ecg-pink-M234Mo253Std23Sk-2Ku14.jpg"),
    this.getClass().getResource("/image/ecg-pink-M227Mo255Std41Sk-3Ku13.gif"),
    // this.getClass()
    // .getResource("/image/ecg-pink-M224Mo249Std39Sk-2Ku7.jpg"),
    // this.getClass().getResource(
    // "/image/ecg-pink-M235Mo255Std31Sk-4Ku22.jpg"),
    // this.getClass()
    // .getResource("/image/ecg-pink-M201Mo234Std41Sk-1Ku0.jpg")
    };
    produceVariants(resources);
  }

  public void produceVariants(final URL[] resourceURLs) {
    for (final URL url : resourceURLs) {
      Assert.assertNotNull(url);
      BufferedImage bi;
      try {
        bi = ImageIO.read(url);
        final ByteProcessor ip = (ByteProcessor) new ColorProcessor(bi)
        .convertToByte(false);
        new PinkStripedWorkflow().execute(ip);
        final ImageStatistics is = ip.getStatistics();
        final String[] splitted = url.getPath().split("/");
        try (FileOutputStream fileOutputStream = new FileOutputStream("target/"
            + splitted[splitted.length - 1].replaceAll("\\..*", "-out.png"))) {
          final String formatMessage = "%s of %s is not correct, should be %s, but was %s";
          ImageIO.write(ip.getBufferedImage(), "png", fileOutputStream);
          Assert.assertTrue(
              String.format(formatMessage, "Mean", url, ">= 250", is.mean),
              is.mean >= 250);
          Assert.assertTrue(
              String.format(formatMessage, "Mode", url, ">= 255", is.mode),
              is.mode >= 255);
          Assert.assertTrue(String.format(formatMessage, "Standard deviation",
              url, ">= 22", is.stdDev), is.stdDev >= 22);
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }
}