/**
 * 
 */
package ij.plugin.support;

import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.util.List;

public class Supporto {

  public static ByteProcessor smussaEsottocampiona(final ByteProcessor input,
      final int window, final float sigma) throws IllegalArgumentException {
    final ByteProcessor prepocessing = copyByteProcessor(input);
    final float gauss[] = initGaussianKernel(window, sigma);
    final Convolver convolver = new Convolver();
    convolver.convolve(prepocessing, gauss, (int) Math.sqrt(gauss.length),
        (int) Math.sqrt(gauss.length));

    int prepocessingWidth = prepocessing.getWidth();
    int prepocessingHeight = prepocessing.getHeight();
    final ByteProcessor out = new ByteProcessor(prepocessingWidth / 2,
        prepocessingHeight / 2);
    if (prepocessingWidth % 2 != 0)
      prepocessingWidth--;
    if (prepocessingHeight % 2 != 0)
      prepocessingHeight--;
    for (int i = 0, x = 0; i < prepocessingWidth; i = i + 2) {
      for (int j = 0, y = 0; j < prepocessingHeight; j = j + 2) {
        out.set(x, y, prepocessing.get(i, j));
        y++;
      }
      x++;
    }
    return out;
  }

  public static ColorProcessor cambioColore(final ByteProcessor image) {
    final Image im = image.createImage();
    return new ColorProcessor(im);
  }

  /*********************************************** METODI MIEI *****************************************************/

  /**
   * 
   * Metodo che copia un ImageProcessor in un ByteProcessor.
   * 
   * @param ip
   *          input ImageProcessor.
   * @return ByteProcessor.
   */
  public static ByteProcessor copyByteProcessor(final ImageProcessor ip) {
    final ByteProcessor bp = new ByteProcessor(ip.getWidth(), ip.getHeight());
    for (int y = 0; y < ip.getHeight(); y++)
      for (int x = 0; x < ip.getWidth(); x++)
        bp.set(x, y, ip.getPixel(x, y));
    return bp;
  }

  /**
   * Realizza la gaussiana e ne inserisce i valori in un array
   * 
   * @param window
   *          numero di righi e colonne della matrice gaussiana. Deve essere
   *          dispari
   * @param sigma
   * @return array della gaussiana
   * @throws IllegalArgumentException
   *           se la finestra � negativa, zero o pari. se sigma � zero o
   *           negativa.
   */
  public static float[] initGaussianKernel(final int window, final float sigma)
      throws IllegalArgumentException {
    controlInput(window, sigma);
    final short aperture = (short) (window / 2);
    final float[][] gaussianKernel = new float[2 * aperture + 1][2 * aperture + 1];
    final float out[] = new float[(2 * aperture + 1) * (2 * aperture + 1)];
    int k = 0;
    float sum = 0;
    for (int dy = -aperture; dy <= aperture; dy++)
      for (int dx = -aperture; dx <= aperture; dx++) {
        gaussianKernel[dx + aperture][dy + aperture] = (float) Math.exp(-(dx
            * dx + dy * dy)
            / (2 * sigma * sigma));
        sum += gaussianKernel[dx + aperture][dy + aperture];
      }
    for (int dy = -aperture; dy <= aperture; dy++)
      for (int dx = -aperture; dx <= aperture; dx++)
        out[k++] = gaussianKernel[dx + aperture][dy + aperture] / sum;
    return out;
  }

  /**
   * controllo dei valori della gaussiana
   * 
   * @param window
   *          la finestra della gaussiana.
   * @param sigma
   *          il valore di sigma della gaussiana
   * @throws IllegalArgumentException
   *           se la finestra � negativa, zero o non � dispari. se sigma � zero
   *           o negativa.
   */
  private static void controlInput(final int window, final float sigma)
      throws IllegalArgumentException {
    if (window % 2 == 0)
      throw new IllegalArgumentException("Window isn't an odd.");
    if (window <= 0)
      throw new IllegalArgumentException("Window is negative or zero");
    if (sigma <= 0)
      throw new IllegalArgumentException(
          "Sigma of the gaussian is zero or negative.");
  }

  /**
   * metodo che disegna i corners nell'immagine
   * 
   * @param corn
   *          lista di tutti i corners trvati
   * @param i
   *          immagine su cui disegnare i corners
   * @param colori
   *          array che specifica il numero di corners trovati ad ogni
   *          iterazione
   * @return immagine a colori con i corners identificati da delle piccole croci
   *         colorate
   */
  public static ColorProcessor disegna(final List<int[]> corn,
      final ColorProcessor i, final int[] colori) {
    final int width = i.getWidth();
    final int height = i.getHeight();
    // crea le linee orizzontali
    int R = 0;
    int G = 0;
    int B = 255;
    int colore = R << 16 | G << 8 | B;
    int conta = 1;
    int j = 0;
    boolean esiste = true;
    final int tuttiColori[] = new int[colori.length];
    tuttiColori[0] = colore;

    for (final int[] p : corn) {

      if (conta > colori[j]) {
        conta = 1;
        esiste = true;
        if (j < colori.length - 1) {
          j++;
          while (esiste) {
            R = (int) (Math.random() * 256);
            G = (int) (Math.random() * 256);
            B = (int) (Math.random() * 256);

            colore = R << 16 | G << 8 | B;
            esiste = false;

            for (int k = 0; k < tuttiColori.length; k++)
              if (colore == tuttiColori[k])
                esiste = true;
          }

          tuttiColori[j] = colore;

          colore = R << 16 | G << 8 | B;
        }
      }

      for (int dx = -2; dx <= 2; dx++) {
        if (p[0] + dx < 0 || p[0] + dx >= width)
          continue;
        i.set(p[0] + dx, p[1], colore);

      }

      // crea le linee verticali
      for (int dy = -2; dy <= 2; dy++) {

        if (p[1] + dy < 0 || p[1] + dy >= height)
          continue;

        i.set(p[0], p[1] + dy, colore);
      }
      ++conta;
    }

    return i;
  }

}