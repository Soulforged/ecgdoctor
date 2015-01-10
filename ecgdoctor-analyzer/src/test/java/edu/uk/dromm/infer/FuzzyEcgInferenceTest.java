package edu.uk.dromm.infer;

import java.net.URL;
import java.util.HashMap;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
  public void sureRegularSinusRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    assertInputRanges(diagnosis, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  @Test
  public void borderlineRegularSinusRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1000);
    assertInputRanges(diagnosis, 0.6);
    diagnosis.evaluate();
    Variable result = diagnosis.getVariable("result");
    Assert.assertNotNull(result.getLinguisticTerm("normalsr"));
    diagnosis.setVariable("rr", 1670);
    diagnosis.evaluate();
    result = diagnosis.getVariable("result");
    Assert.assertNotNull(result.getLinguisticTerm("normalsr"));
  }

  @Test
  public void cardiopathy(){
	  final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
	  diagnosis.setVariable("rr", 750);
	  diagnosis.evaluate();
	  Variable result = diagnosis.getVariable("result");
	  Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
	  diagnosis.setVariable("rr", 2000);
	  diagnosis.evaluate();
	  Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
  }

  @Test
  public void cardiopathyLimits(){
	  final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
	  diagnosis.setVariable("rr", 950);
	  diagnosis.evaluate();
	  Variable result = diagnosis.getVariable("result");
	  Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
	  diagnosis.setVariable("rr", 1750);
	  diagnosis.evaluate();
	  Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
  }

  public void sinusBradycardiaRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    assertInputRanges(diagnosis, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  private void assertInputRanges(final FunctionBlock fb, final double rrexp){
    final double rrmemb = fb.getVariable("rr").getMembership("normal");
    Assert.assertEquals("RR membership should be near " + rrexp, rrexp, rrmemb, 0.01);
  }

  @Test
  @Ignore("Only for visualization purposes")
  public void chart(){
    fis.chart();
    while(true);
  }
}
