package edu.uk.dromm.infer;

import java.net.URL;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FuzzyEcgInferenceTest {

  private FIS fis;

  @Before
  public void init(){
    final URL ecgFisURL = FuzzyEcgInferenceTest.class.getResource("/fcl/ecg.fcl");
    fis = FIS.load(ecgFisURL.getFile());
    Assert.assertNotNull(fis);
  }

  @Test
  public void onlyOneFunctionBlockCalledDiagnosis(){
    final FunctionBlock fb = fis.getFunctionBlock("diagnosis");
    Assert.assertNotNull(fb);
  }
}
