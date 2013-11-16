package ij.plugin.filter;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.process.Blitter;
import ij.process.FHT;
import ij.process.ImageProcessor;

import java.awt.Rectangle;

public class OwnFFTFilter extends FFTFilter {

  public void filter(final ImageProcessor ip, final double filterLarge, final double filterSmall,
      final int noneHorOrVer, final double widthOfStripeFilter) {
    ImageProcessor ip2 = ip;
    final Rectangle roiRect = ip.getRoi();
    final int maxN = Math.max(roiRect.width, roiRect.height);

    int i=2;
    while(i<1.5 * maxN) i *= 2;

    // Calculate the inverse of the 1/e frequencies for large and small structures.
    final double fl = 2.0*filterLarge / i;
    final double fs = 2.0*filterSmall / i;
    final Rectangle fitRect = new Rectangle();
    fitRect.x = (int) Math.round( (i - roiRect.width) / 2.0 );
    fitRect.y = (int) Math.round( (i - roiRect.height) / 2.0 );
    fitRect.width = roiRect.width;
    fitRect.height = roiRect.height;
    ip2 = tileMirror(ip, i, i, fitRect.x, fitRect.y);
    final FHT fht = new FHT(ip2);
    fht.transform();
    super.filterLargeSmall(fht, fl, fs, noneHorOrVer, widthOfStripeFilter);

    fht.inverseTransform();
    fht.setRoi(fitRect);
    ip2 = fht.crop();

    final ImagePlus imp2 = new ImagePlus("", ip2);
    new ContrastEnhancer().stretchHistogram(imp2, 1.0);
    ip2 = imp2.getProcessor();
    ip2.convertToByte(true);
    ip.snapshot();
    ip.copyBits(ip2, roiRect.x, roiRect.y, Blitter.COPY);
    ip.resetMinAndMax();
  }
}
