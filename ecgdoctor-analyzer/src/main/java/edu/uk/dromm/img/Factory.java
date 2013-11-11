package edu.uk.dromm.img;

import edu.uk.dromm.img.impl.DefaultEnhancementStrategy;
import edu.uk.dromm.img.impl.DefaultImageParameterProvider;
import edu.uk.dromm.img.impl.ECGImagePreprocessing;

public class Factory {

  public ImageParameterProvider getImageParameterProvider(){
    return new DefaultImageParameterProvider();
  }

  public ImageProcess getImagePreProcessing(){
    return new ECGImagePreprocessing();
  }

  public EnhancementStrategy getEnhancementStrategy(){
    return new DefaultEnhancementStrategy();
  }
}
