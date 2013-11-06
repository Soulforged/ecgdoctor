package fuzzy;

import java.net.URL;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FuzzyLogicTest {

  private FIS fis = null;

  @Before
  public void init() {
    final URL url = FuzzyLogicTest.class.getResource("/fcl/tipper.fcl");
    fis = FIS.load(url.getFile());
    Assert.assertNotNull(fis);
  }

  @Test
  public void functionBlocksAreRetrievedByCaseSentiveNames() {
    FunctionBlock fb = fis.getFunctionBlock("tipper");
    Assert.assertTrue(fb != null && fb.getName().equals("tipper"));
    fb = fis.getFunctionBlock("Tipper");
    Assert.assertNull(fb);
  }

  @Test
  public void functionBlocksActsAsAContainerOfVariablesAndRules() {
    final FunctionBlock fb = fis.getFunctionBlock("tipper");
    Assert.assertTrue(fb.varibleExists("service"));
    Assert.assertEquals(1, fb.getRuleBlocks().size());
    Assert.assertEquals(3, fb.getVariables().size());
  }

  @Test
  public void variableDefaultValueIsZero() {
    final Variable tip = fis.getVariable("tip");
    Assert.assertTrue(0 == tip.getDefaultValue());
  }

  @Test
  public void variableDefaultValueIsNaNIfNotSetExplicitly(){
    final Variable service = fis.getVariable("service");
    final Variable food = fis.getVariable("food");
    Assert.assertEquals(Double.NaN, service.getDefaultValue(), 0.0);
    Assert.assertEquals(Double.NaN, food.getDefaultValue(), 0.0);
  }

  @Test
  public void whenEvaluatedNoTipIsReturnedForZeroServiceAndZeroFood(){
    fis.evaluate();
    final Variable tip = fis.getVariable("tip");
    Assert.assertEquals(0.0, tip.getValue(), 0);
  }

  @Test
  public void necessityToReevaluateAfterChangingVariable(){
    fis.evaluate();
    final Variable tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() < 10.0);
    fis.setVariable("service", 6);
    fis.setVariable("food", 1);
    fis.evaluate();
    Assert.assertTrue(tip.getValue() >= 10.0);
  }

  @Test
  public void whenServiceIsPoorAndFoodIsRancidTipIsCheap(){
    fis.setVariable("service", 3);
    fis.setVariable("food", 1);
    fis.evaluate();
    final Variable tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() < 10.0);
  }

  @Test
  public void whenServiceGoodFoodDoesntMatterAndTipIsAverage(){
    fis.setVariable("service", 6);
    fis.setVariable("food", 1);
    fis.evaluate();
    final Variable tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() >= 10.0 && tip.getValue() < 20);
    fis.setVariable("food", 8);
    fis.evaluate();
    Assert.assertTrue(tip.getValue() >= 10.0 && tip.getValue() < 20);
  }

  @Test
  public void whenServiceIsExcellentAndFoodIsDeliciousTipIsGenerous(){
    fis.setVariable("service", 8);
    fis.setVariable("food", 8);
    fis.evaluate();
    final Variable tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() >= 20.0 && tip.getValue() < 30.0);
  }

  @Test
  public void whenVariablesAreBelowMinAndAboveMaxAResultIsReturned(){
    fis.setVariable("service", -1);
    fis.setVariable("food", -1);
    fis.evaluate();
    Variable tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() >= 0.0 && tip.getValue() < 10.0);
    fis.setVariable("service", 10);
    fis.setVariable("food", 10);
    fis.evaluate();
    tip = fis.getVariable("tip");
    Assert.assertTrue(tip.getValue() >= 20.0 && tip.getValue() < 30.0);
  }

  @Test
  public void outputVariablesCanBeSet(){
    fis.setVariable("tip", 20);
    final Variable tip =fis.getVariable("tip");
    Assert.assertEquals(20.0, tip.getValue(), 0);
  }
}
