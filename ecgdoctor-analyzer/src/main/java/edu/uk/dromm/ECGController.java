package edu.uk.dromm;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import edu.uk.dromm.fcl.Diagnosis;
import edu.uk.dromm.img.ECGParameters;
import edu.uk.dromm.img.Factory;
import edu.uk.dromm.img.ImageAnalysis;
import edu.uk.dromm.img.ImageProcess;

/**
 * @author dicardo
 * 
 */
public class ECGController {

	public Diagnosis start(final String url) {
		final Factory f = new Factory();
		final ImageProcess ip = f.getImagePreProcessing();
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new FileInputStream(url));
			final BufferedImage preProcessed = ip.process(bi);
			final ImageAnalysis ia = f.getImageAnalysis();
			final ECGParameters ecgPrms = ia.process(preProcessed);
			return f.getInferenceSystem().infer(ecgPrms);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
