package edu.uk.dromm.infer;

import java.net.URL;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

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

  @Test
  public void regularSinusRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1100);
    diagnosis.setVariable("pv", 0.8);
    diagnosis.setVariable("qrs", 110);
    diagnosis.setVariable("pr", 180);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertEquals(1, result.getValue(), 0);
  }

  @Test
  public void chart(){
    fis.chart();
    while(true);
  }
}
