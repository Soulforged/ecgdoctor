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
  public void sureRegularSinusRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    diagnosis.setVariable("pv", 10);
    diagnosis.setVariable("qrs", 120);
    diagnosis.setVariable("pr", 160);
    assertInputRanges(diagnosis, 1.0, 1.0, 1.0, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  @Test
  public void borderlineRegularSinusRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1000);
    diagnosis.setVariable("pv", 0.84);
    diagnosis.setVariable("qrs", 110);
    diagnosis.setVariable("pr", 120);
    assertInputRanges(diagnosis, 0.6, 0.6, 0.6, 0.6);
    diagnosis.evaluate();
    Variable result = diagnosis.getVariable("result");
    System.out.println(result.getValue());
    Assert.assertTrue(result.getValue() > 0.5);
    diagnosis.setVariable("rr", 1670);
    diagnosis.setVariable("pv", 0.84);
    diagnosis.setVariable("qrs", 130);
    diagnosis.setVariable("pr", 200);
    assertInputRanges(diagnosis, 0.6, 0.6, 0.6, 0.6);
    diagnosis.evaluate();
    result = diagnosis.getVariable("result");
    System.out.println(result.getValue());
    Assert.assertTrue(result.getValue() > 0.5);
  }

  public void sinusBradycardiaRythm(){
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    diagnosis.setVariable("pv", 10);
    diagnosis.setVariable("qrs", 120);
    diagnosis.setVariable("pr", 160);
    assertInputRanges(diagnosis, 1.0, 1.0, 1.0, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  private void assertInputRanges(final FunctionBlock fb, final double rrexp, final double qrsexp, final double pvexp, final double prexp){
    final double rrmemb = fb.getVariable("rr").getMembership("normal");
    Assert.assertEquals("RR membership should be near " + rrexp, rrexp, rrmemb, 0.01);
    final double pvmemb = fb.getVariable("pv").getMembership("present");
    Assert.assertEquals("PV membership should be near " + pvexp, pvexp, pvmemb, 0.01);
    final double qrsmemb = fb.getVariable("qrs").getMembership("normal");
    Assert.assertEquals("QRS membership should be near " + qrsexp, qrsexp, qrsmemb, 0.01);
    final double prmemb = fb.getVariable("pr").getMembership("normal");
    Assert.assertEquals("PR membership should be near " + prexp, prexp, prmemb, 0.01);
  }

  @Test
  public void chart(){
    fis.chart();
    while(true);
  }
}
