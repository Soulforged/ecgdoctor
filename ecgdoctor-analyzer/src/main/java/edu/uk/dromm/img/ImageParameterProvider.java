package edu.uk.dromm.img;

import ij.process.AutoThresholder.Method;
import ij.process.ImageStatistics;

/**
 * 
 * @author dicardo
 *
 */
public interface ImageParameterProvider {

  Method thresholdMethod(ImageStatistics is);
}
