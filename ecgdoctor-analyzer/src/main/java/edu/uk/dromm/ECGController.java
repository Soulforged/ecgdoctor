package edu.uk.dromm;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import edu.uk.dromm.fcl.Diagnosis;
import edu.uk.dromm.img.ECGParameters;
import edu.uk.dromm.img.Factory;
import edu.uk.dromm.img.ImageAnalysis;
import edu.uk.dromm.img.ImageProcess;

/**
 * @author dicardo
 *
 */
public class ECGController {

  public Diagnosis start(final String url) {
    final Factory factory = new Factory();
    final ImageProcess ip = factory.getImagePreProcessing();
    BufferedImage bi = null;
    try (FileInputStream stream = new FileInputStream(url)) {
      bi = ImageIO.read(stream);
      final BufferedImage preProcessed = ip.process(bi);
      final ImageAnalysis ia = factory.getImageAnalysis();
      final ECGParameters ecgPrms = ia.process(preProcessed);
      return factory.getInferenceSystem().infer(ecgPrms);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
