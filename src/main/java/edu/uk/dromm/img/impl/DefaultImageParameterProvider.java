package edu.uk.dromm.img.impl;

import ij.process.AutoThresholder.Method;
import ij.process.ImageStatistics;
import edu.uk.dromm.img.ImageParameterProvider;

public class DefaultImageParameterProvider implements ImageParameterProvider {

  @Override
  public Method thresholdMethod(final ImageStatistics is) {
    if(is.mean > 220)
      return Method.RenyiEntropy;
    return Method.Minimum;
  }

}
