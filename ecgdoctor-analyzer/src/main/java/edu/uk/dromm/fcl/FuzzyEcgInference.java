package edu.uk.dromm.fcl;

import java.io.IOException;
import java.io.InputStream;

import edu.uk.dromm.img.ECGParameters;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyEcgInference {

  private FIS fis;

  public Diagnosis infer(final ECGParameters ecgp) throws IOException {
    try (InputStream ecgFisStream = FuzzyEcgInference.class
        .getResourceAsStream("/fcl/ecg.fcl")) {
      fis = FIS.load(ecgFisStream, true);
      final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
      diagnosis.setVariable("rr", ecgp.getRrDiff());
      diagnosis.evaluate();
      final Variable result = diagnosis.getVariable("result");
      if (result.getValue() > 0.5)
        return new Diagnosis("Su corazón está en perfecto estado",
            "El ritmo es normal al igual que la intensidad.", "Synus rythm");
      return new Diagnosis("No se ha podido determinar con seguridad",
          "El ritmo no ha podido ser reconocido por el sistema", "");
    }
  }

}
