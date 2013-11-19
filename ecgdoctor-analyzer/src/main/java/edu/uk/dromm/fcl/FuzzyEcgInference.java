package edu.uk.dromm.fcl;

import java.net.URL;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import edu.uk.dromm.img.ECGParameters;

public class FuzzyEcgInference {

	private FIS fis;

	public Diagnosis infer(final ECGParameters ecgp) {
		final URL ecgFisURL = FuzzyEcgInference.class
				.getResource("/fcl/ecg.fcl");
		fis = FIS.load(ecgFisURL.getFile());
		final FunctionBlock diagnosis = fis.getFunctionBlock("diagnosis");
		diagnosis.setVariable("qrs", ecgp.qrs);
		diagnosis.setVariable("rr", ecgp.rr);
		diagnosis.setVariable("pr", ecgp.pr);
		diagnosis.setVariable("pv", ecgp.pv);
		diagnosis.evaluate();
		final Variable result = diagnosis.getVariable("result");
		if (result.getValue() > 0.5) {
			return new Diagnosis("Su corazón está en perfecto estado",
					"El ritmo es normal al igual que la intensidad.",
					"Synus rythm");
		}
		return new Diagnosis("No se ha podido determinar con seguridad",
				"El ritmo no ha podido ser reconocido por el sistema", "");
	}

}
