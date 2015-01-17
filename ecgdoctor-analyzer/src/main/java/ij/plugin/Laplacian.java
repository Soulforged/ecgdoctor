package ij.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FHT;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Laplacian implements PlugInFilter {
  private int M, N, size, w, h;
  private ImagePlus imp;
  private FHT fht;
  private ImageProcessor mask, ipFilter;

  // method from PlugInFilter Interface
  @Override
  public int setup(final String arg, final ImagePlus imp) {
    this.imp = imp;
    return DOES_ALL;
  }

  // method from PlugInFilter Interface
  @Override
  public void run(final ImageProcessor ip) {
    final ImageProcessor ipL = imp.getProcessor();
    M = ip.getWidth();
    N = ip.getHeight();
    filtering(ipL, imp);
    IJ.showProgress(1.0);
  }

  // shows the power spectrum and filters the image
  public void filtering(final ImageProcessor ip, final ImagePlus impL) {
    final int maxN = Math.max(M, N);
    size = 2;
    while (size < maxN)
      size *= 2;
    IJ.runPlugIn("ij.plugin.FFT", "forward");
    h = Math.round((size - N) / 2);
    w = Math.round((size - M) / 2);
    final ImageProcessor ip2 = ip.createProcessor(size, size); // processor of
    // the padded
    // image
    ip2.fill();
    ip2.insert(ip, w, h);
    if (ip instanceof ColorProcessor) {
      final ImageProcessor bright = ((ColorProcessor) ip2).getBrightness();
      fht = new FHT(bright);
      fht.rgb = (ColorProcessor) ip.duplicate(); // get a duplication of
      // brightness in order to add
      // it after filtering
    } else
      fht = new FHT(ip2);

    fht.transform(); // calculates the Fourier transformation
    fht.originalColorModel = ip.getColorModel();
    fht.originalBitDepth = impL.getBitDepth();
    ipFilter = Lapl();
    fht.swapQuadrants(ipFilter);

    final byte[] pixels_id = (byte[]) ipFilter.getPixels();
    final float[] pixels_fht = (float[]) fht.getPixels();

    for (int i = 0; i < size * size; i++)
      pixels_fht[i] = (float) (pixels_fht[i] * (pixels_id[i] & 255) / 255.0);

    mask = fht.getPowerSpectrum();
    final ImagePlus imp2 = new ImagePlus("inverse FFT of " + impL.getTitle(),
        mask);
    imp2.setProperty("FHT", fht);
    imp2.setCalibration(impL.getCalibration());
    doInverseTransform(fht);
  }

  // creates a Laplacian filter
  public ByteProcessor Lapl() {
    final ByteProcessor proc = new ByteProcessor(M, N);
    double value = 0;
    final int xcenter = M / 2 + 1;
    final int ycenter = N / 2 + 1;

    for (int y = 0; y < N; y++)
      for (int x = 0; x < M; x++) {
        value = (1 & 255) / 255 + Math.abs(x - xcenter) * Math.abs(x - xcenter)
            + Math.abs(y - ycenter) * Math.abs(y - ycenter);
        proc.putPixelValue(x, y, value);
      }

    final ByteProcessor ip2 = new ByteProcessor(size, size);
    final byte[] p = (byte[]) ip2.getPixels();
    for (int i = 0; i < size * size; i++)
      p[i] = (byte) 255;
    ip2.insert(proc, w, h);
    return ip2;
  }

  // applies the inverse Fourier transform to the filtered image
  void doInverseTransform(final FHT fhtp) {
    final FHT fhtL = fhtp.getCopy();
    fhtL.inverseTransform();
    fhtL.resetMinAndMax();
    ImageProcessor ip2 = fhtL;
    fhtL.setRoi(w, h, M, N);
    ip2 = fhtL.crop();

    final int bitDepth = fhtL.originalBitDepth > 0 ? fhtL.originalBitDepth
        : imp.getBitDepth();
    switch (bitDepth) {
    case 8:
      ip2 = ip2.convertToByte(true);
      break;
    case 16:
      ip2 = ip2.convertToShort(true);
      break;
    case 24:
      if (fhtL.rgb == null || ip2 == null) {
        IJ.error("FFT", "Unable to set brightness");
        return;
      }
      final ColorProcessor rgb = (ColorProcessor) fhtL.rgb.duplicate();
      rgb.setBrightness((FloatProcessor) ip2);
      ip2 = rgb;
      fhtL.rgb = null;
      break;
    case 32:
    default:
      break;
    }
    if (bitDepth != 24 && fhtL.originalColorModel != null)
      ip2.setColorModel(fhtL.originalColorModel);
    String title = imp.getTitle();
    if (title.startsWith("FFT of "))
      title = title.substring(7, title.length());
    final ImagePlus imp2 = new ImagePlus("Inverse FFT of " + title, ip2);
    if (imp2.getWidth() == imp.getWidth())
      imp2.setCalibration(imp.getCalibration());
    imp2.show();
  }
}