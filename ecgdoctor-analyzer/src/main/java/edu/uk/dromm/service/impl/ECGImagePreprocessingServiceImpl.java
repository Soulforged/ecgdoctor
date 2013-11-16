/**
 * 
 */
package edu.uk.dromm.service.impl;

import java.awt.image.BufferedImage;

import edu.uk.dromm.img.impl.ECGImagePreprocessing;
import edu.uk.dromm.service.ECGImagePreprocessingService;

/**
 * @author magian
 * 
 */
public class ECGImagePreprocessingServiceImpl implements
    ECGImagePreprocessingService {

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.uk.dromm.service.ECGImageAnalisysService#processImage(java.awt.image
   * .BufferedImage)
   */
  @Override
  public String processImage(final BufferedImage bufferedImage) {

    final ECGImagePreprocessing ecgImagePreprocessing = new ECGImagePreprocessing();

    // Aqui se comienza con el analisis de la imagen
    // BufferedImage imageProcessed =
    // ecgImagePreprocessing.process(bufferedImage);

    return null;
  }

}
