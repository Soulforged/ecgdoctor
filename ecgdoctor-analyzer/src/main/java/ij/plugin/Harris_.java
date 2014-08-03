/**
 * 
 */
package ij.plugin;

//Harry detection per imageJ

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.support.Supporto;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Harris Corner Detector classe che effettua la detection di corners
 * multirisoluzione, in immagini a toni di grigio
 * 
 * @author Messina Mariagrazia
 * 
 */
public class Harris_ implements PlugInFilter {

  // lista che conterr� i conrner ad agni iterazione
  List<int[]>    corners;

  // dimensioni di mezza finestra
  private int    halfwindow    = 0;

  // varianza della gaussiana
  private float  gaussiansigma = 0;

  // parametri dii soglia
  private int    minDistance   = 0;
  private int    minMeasure    = 0;
  private int    piramidi      = 0;
  // oggetto utilizzato per il calcolo del gradiente
  GradientVector gradient      = new GradientVector();
  // matrice dei corners
  int            matriceCorner[][];

  // About...
  private void showAbout() {
    IJ.showMessage("Harris...", " Harris Corner Detector ");
  }

  @Override
  public int setup(final String arg, final ImagePlus imp) {

    // about...
    if (arg.equals("about")) {
      showAbout();
      return DONE;
    }

    // else...
    if (imp == null)
      return DONE;

    // richiesta di parametri in input

    final GenericDialog gd = new GenericDialog("PARAMETRI");
    gd.addNumericField("Varianza gaussiana", 1.4, 1);
    gd.addNumericField("Soglia minima", 10, 0);
    gd.addNumericField("Distanza minima", 8, 0);
    gd.addNumericField("Numero di iterazioni da effetture", 1, 0);

    final int halfwindow = 1;
    float gaussiansigma = 0;
    int minMeasure = 0;
    int minDistance = 0;
    int piramidi = 0;
    boolean controllo = true;
    while (controllo) {
      gd.showDialog();
      if (gd.wasCanceled())
        return DONE;

      gaussiansigma = (float) gd.getNextNumber();
      minMeasure = (int) gd.getNextNumber();
      minDistance = (int) gd.getNextNumber();
      piramidi = (int) gd.getNextNumber();
      if (gaussiansigma > 0 && minMeasure >= 0 && minDistance >= 0)
        controllo = false;
    }
    gd.dispose();

    this.halfwindow = halfwindow;
    this.gaussiansigma = gaussiansigma;
    this.minMeasure = minMeasure;
    this.minDistance = minDistance;
    this.piramidi = piramidi;
    return PlugInFilter.DOES_8G;
  }

  @Override
  public void run(final ImageProcessor ip) {

    ByteProcessor bp = Supporto.copyByteProcessor(ip);
    final ByteProcessor bp2 = Supporto.copyByteProcessor(ip);
    final int width = bp.getWidth();
    final int height = bp.getHeight();
    final int potenza = (int) Math.pow(2, piramidi - 1);
    if (width / potenza < 8 || height / potenza < 8) {
      piramidi = 1;
      JOptionPane
      .showMessageDialog(
          null,
          "n di iteazioni da effettuare troppo alto,\n sar effettuata una sola iterazione");
    }

    final ByteProcessor newbp;
    final List<int[]> tmp = new ArrayList<int[]>();
    final int[] numero = new int[piramidi];

    for (int i = 0; i < piramidi; i++) {
      corners = new ArrayList<int[]>();
      filter(bp, minMeasure, minDistance, i);
      for (final int[] n : corners)
        tmp.add(n);
      numero[i] = corners.size();

      bp = Supporto.smussaEsottocampiona(bp, 3, gaussiansigma);

    }

    ColorProcessor image = Supporto.cambioColore(bp2);
    image = Supporto.disegna(tmp, image, numero);
    final ImagePlus newImgLut = new ImagePlus("Risultato", image);
    newImgLut.show();

  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

  /**
   * Harris Corner Detection
   * 
   * @param c
   *          immagine
   * @param minMeasure
   *          saglio sul valore minimo che assume il corner
   * @param minDistance
   *          soglia sulla distanza minima tra 2 corners
   */
  public void filter(final ByteProcessor c, final int minMeasure,
      final int minDistance, final int factor) {

    final int width = c.getWidth();
    final int height = c.getHeight();

    // scurire l'immagine
    final ByteProcessor c2 = new ByteProcessor(width, height);
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        c2.set(x, y, (int) (c.get(x, y) * 0.80));

    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        // harris response(-1 se il pixel non e un massimo locale)
        final int h = (int) spatialMaximaofHarrisMeasure(c, x, y);

        // aggiunge il corner alla lista se supera un valore di soglia
        if (h >= minMeasure) {
          if (factor != 0) {
            final int XY[] = mappatura(x, y, factor);
            x = XY[0];
            y = XY[1];
          }

          getCorners().add(new int[] { x, y, h });

        }
      }

    // si tengono i valori di risposta pi� alti
    final Iterator<int[]> iter = getCorners().iterator();
    while (iter.hasNext()) {
      final int[] p = iter.next();
      for (final int[] n : getCorners()) {
        if (n == p)
          continue;
        final int dist = (int) Math.sqrt((p[0] - n[0]) * (p[0] - n[0])
            + (p[1] - n[1]) * (p[1] - n[1]));
        if (dist > minDistance)
          continue;
        if (n[2] < p[2])
          continue;
        iter.remove();
        break;
      }
    }

  }

  /**
   * @return the corners
   */
  private List<int[]> getCorners() {
    if (corners == null)
      corners = new ArrayList<>();
      return corners;
  }

  /**
   * reatituisce il valore del pixel (x,y) se � un massimo, altrimenti
   * restituisce -1
   * 
   * @param c
   *          immagine
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @return la harris response se il pixel � un massimo locale, -1 altrimenti
   */
  private double spatialMaximaofHarrisMeasure(final ByteProcessor c,
      final int x, final int y) {
    final int n = 8;
    final int[] dx = new int[] { -1, 0, 1, 1, 1, 0, -1, -1 };
    final int[] dy = new int[] { -1, -1, -1, 0, 1, 1, 1, 0 };
    // si calcola il valore di harris response nel punto x,y
    final double w = harrisMeasure(c, x, y);
    // per ogni punto dell'intorno di x,y si calcola il valore della harris
    // response
    for (int i = 0; i < n; i++) {
      final double wk = harrisMeasure(c, x + dx[i], y + dy[i]);
      // se almeno un valore calcolato in un punto dell'intorno � maggiore di
      // quello del punto in questione, esso non
      // � un massimo locale e si restituisce -1
      if (wk >= w)
        return -1;
    }
    // in caso contrario � un massimo locale
    return w;
  }

  /**
   * computa harris corner response
   * 
   * @param c
   *          Image map
   * @param x
   *          coordinata x
   * @param y
   *          y coordinata y
   * @return harris corner response
   */
  private double harrisMeasure(final ByteProcessor c, final int x, final int y) {
    double m00 = 0, m01 = 0, m10 = 0, m11 = 0;

    // k = det(A) - lambda * trace(A)^2
    // A matrice del secondo momento
    // lambda generalmente � tra 0.04 e 0.06. qui � stato fissato a 0.06

    for (int dy = -halfwindow; dy <= halfwindow; dy++)
      for (int dx = -halfwindow; dx <= halfwindow; dx++) {
        final int xk = x + dx;
        final int yk = y + dy;
        if (xk < 0 || xk >= c.getWidth())
          continue;
        if (yk < 0 || yk >= c.getHeight())
          continue;

        // calcolo del gradiente (derivate prime parziali ) di c nel punto xk,yk
        final double[] g = gradient.getVector(c, xk, yk);
        final double gx = g[0];
        final double gy = g[1];

        // calcolo il peso della finestra gaussiana nel punto dx,dy
        final double gw = gaussian(dx, dy, gaussiansigma);

        // creazione degli elementi della matrice
        m00 += gx * gx * gw;
        m01 += gx * gy * gw;
        m10 = m01;
        m11 += gy * gy * gw;
      }

    // harris = det(A) - 0.06*traccia(A)^2;
    // det(A)=m00*m11 - m01*m10
    final double det = m00 * m11 - m01 * m10;
    // tr(A)=(m00+m11)*(m00+m11);
    final double traccia = m00 + m11;
    // harris response= det-k tr^2;
    final double harris = det - 0.06 * (traccia * traccia);
    return harris / (256 * 256);
  }

  /**
   * Funzione per il computo della Gaussian window
   * 
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @param sigma2
   *          variannza
   * @return valore della funzione
   */

  private double gaussian(final double x, final double y, final float sigma2) {
    final double t = (x * x + y * y) / (2 * sigma2);
    final double u = 1.0 / (2 * Math.PI * sigma2);
    final double e = u * Math.exp(-t);
    return e;
  }

  /**
   * Funzione che realizza la mappatura dei pixel dell'immagine sottocampionata,
   * nell'immagine originale
   * 
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @param fact
   *          parametro di scala
   * @return coordinate x e y nell'immagine originale
   */

  public int[] mappatura(final int x, final int y, final int fact) {
    final int nuoviXY[] = new int[2];
    nuoviXY[0] = x * 2 * fact;
    nuoviXY[1] = y * 2 * fact;
    return nuoviXY;
  }

}

/**
 * Gradient vector classe che effettua il calcolo del gradiente smussato,
 * effetturando le derivate x e y di una gaussiana
 * 
 * @author Messina Mariagrazia
 * 
 */
class GradientVector {

  int        halfwindow = 1;
  double     sigma2     = 1.2;

  double[][] kernelGx   = new double[2 * halfwindow + 1][2 * halfwindow + 1];
  double[][] kernelGy   = new double[2 * halfwindow + 1][2 * halfwindow + 1];

  /**
   * Metodo costruttore
   * 
   */
  public GradientVector() {
    for (int y = -halfwindow; y <= halfwindow; y++)
      for (int x = -halfwindow; x <= halfwindow; x++) {
        kernelGx[halfwindow + y][halfwindow + x] = Gx(x, y);
        kernelGy[halfwindow + y][halfwindow + x] = Gy(x, y);
      }
  }

  /**
   * Funzione che realizza lo smussamento dell'immagine mediante una gaussiana
   * per poi calcolarne la derivata x (operatore Drog)
   * 
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @return volere della gaussiana nel punto x,y
   */
  private double Gx(final int x, final int y) {
    final double t = (x * x + y * y) / (2 * sigma2);
    final double d2t = -x / sigma2;
    final double e = d2t * Math.exp(-t);
    return e;
  }

  /**
   * Funzione che realizza lo smussamento dell'immagine mediante una gaussiana
   * per poi calcolarne la derivata y (operatore Drog)
   * 
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @return volere della gaussiana nel punto x,y
   */
  private double Gy(final int x, final int y) {
    final double t = (x * x + y * y) / (2 * sigma2);
    final double d2t = -y / sigma2;
    final double e = d2t * Math.exp(-t);
    return e;
  }

  // restituisce il vettore del Gradient per il pixel(x,y)
  /**
   * Funzione che inserisce in un vettore il valore del gradiente dei punti
   * appartenenti ad una finestre
   * 
   * @param x
   *          coordinata x
   * @param y
   *          coordinata y
   * @param c
   *          immagine
   * @return volere del gradiente x e y in tutti i punti della finestra
   */
  public double[] getVector(final ByteProcessor c, final int x, final int y) {
    double gx = 0, gy = 0;
    for (int dy = -halfwindow; dy <= halfwindow; dy++)
      for (int dx = -halfwindow; dx <= halfwindow; dx++) {
        final int xk = x + dx;
        final int yk = y + dy;
        final double vk = c.getPixel(xk, yk); // <-- value of the pixel
        gx += kernelGx[halfwindow - dy][halfwindow - dx] * vk;
        gy += kernelGy[halfwindow - dy][halfwindow - dx] * vk;
      }

    final double[] gradientVector = new double[] { gx, gy };

    return gradientVector;
  }
}
