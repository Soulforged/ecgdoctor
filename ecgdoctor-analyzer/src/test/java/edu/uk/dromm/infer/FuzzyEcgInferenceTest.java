package edu.uk.dromm.infer;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyEcgInferenceTest {

  private FIS fis;

  @Before
  public void init() {
    final URL ecgFisURL = FuzzyEcgInferenceTest.class
        .getResource("/fcl/ecg.fcl");
    fis = FIS.load(ecgFisURL.getFile());
    Assert.assertNotNull(fis);
  }

  // ECGParameters [pStart=653.0976047515869, pPeak=0.01923076994717121,
  // pEnd=754.1722340583801, pPeakT=653.0976047515869, pDur=101.07462930679321,
  // qStart=863.0218348503113, qPeak=-0.03846153989434242,
  // qPeakT=886.3467493057251, qDur=46.64982891082764, rStart=909.6716637611389,
  // rPeak=0.730769257992506, rPeakT=948.5465211868286, rDur=101.07462930679321,
  // sStart=1010.7462930679321, sPeak=-0.01923076994717121,
  // sEnd=1041.846179008484, sPeakT=1010.7462930679321, sDur=31.099885940551758,
  // tStart=1041.846179008484, tPeak=0.26923077926039696,
  // tEnd=1438.3697247505188, tPeakT=1251.7704091072083, tDur=396.5235457420349,
  // qrsDur=178.8243441581726, nextR=0.7692307978868484,
  // nextRt=1998.1676716804504, rrDiff=1049.6211504936218]
  @Test
  public void almostSureRegularSinusRythm() {
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1049.6211504936218);
    assertInputRanges(diagnosis, 0.97);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.5);
  }

  @Test
  public void onlyOneFunctionBlockCalledDiagnosis() {
    final FunctionBlock fb = fis.getFunctionBlock("diagnosis");
    Assert.assertNotNull(fb);
  }

  @Test
  public void sureRegularSinusRythm() {
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    assertInputRanges(diagnosis, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  @Test
  public void borderlineRegularSinusRythm() {
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
  public void cardiopathy() {
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 750);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
    diagnosis.setVariable("rr", 2000);
    diagnosis.evaluate();
    Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
  }

  @Test
  public void cardiopathyLimits() {
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 950);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
    diagnosis.setVariable("rr", 1750);
    diagnosis.evaluate();
    Assert.assertNotNull(result.getLinguisticTerm("cardiopathy"));
  }

  public void sinusBradycardiaRythm() {
    final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
    diagnosis.setVariable("rr", 1335);
    assertInputRanges(diagnosis, 1.0);
    diagnosis.evaluate();
    final Variable result = diagnosis.getVariable("result");
    Assert.assertTrue(result.getValue() > 0.9);
  }

  private static void assertInputRanges(final FunctionBlock fb,
      final double rrexp) {
    final double rrmemb = fb.getVariable("rr").getMembership("normal");
    Assert.assertEquals("RR membership should be near " + rrexp, rrexp, rrmemb,
        0.01);
  }

  @Test
  @Ignore("Only for visualization purposes")
  public void chart() {
    fis.chart();
    while (true) {
      // Just keep the chart open until someone closes it manually;
    }
  }
}
