package edu.uk.dromm.img;

/**
 *
 * @author dicardo
 *
 */
public class ECGParameters {

  private double pStart = -1, pPeak = 0, pEnd = 0, pPeakT = 0;
  private final double pDur;
  private double qStart = 0, qPeak = 0, qPeakT = 0;
  private final double qDur;
  private double rStart = 0, rPeak = 0, rPeakT = 0;
  private final double rDur;
  private double sStart = 0, sPeak = 0, sEnd = 0, sPeakT = 0;
  private final double sDur;
  private double tStart = 0, tPeak = 0, tEnd = 0, tPeakT = 0;
  private final double tDur;
  private final double qrsDur;
  private double nextR = 0, nextRt = 0;
  private final double rrDiff;

  public ECGParameters(final double pStart, final double pPeak,
      final double pEnd, final double pPeakT, final double qStart,
      final double qPeak, final double qPeakT, final double rStart,
      final double rPeak, final double rPeakT, final double sStart,
      final double sPeak, final double sEnd, final double sPeakT,
      final double tStart, final double tPeak, final double tEnd,
      final double tPeakT, final double nextR, final double nextRt) {
    super();
    this.pStart = pStart;
    this.pPeak = pPeak;
    this.pEnd = pEnd;
    this.pPeakT = pPeakT;
    pDur = pEnd - pStart;
    this.qStart = qStart;
    this.qPeak = qPeak;
    this.qPeakT = qPeakT;
    qDur = rStart - qStart;
    this.rStart = rStart;
    this.rPeak = rPeak;
    this.rPeakT = rPeakT;
    rDur = sStart - rStart;
    this.sStart = sStart;
    this.sPeak = sPeak;
    this.sEnd = sEnd;
    this.sPeakT = sPeakT;
    sDur = sEnd - sStart;
    this.tStart = tStart;
    this.tPeak = tPeak;
    this.tEnd = tEnd;
    this.tPeakT = tPeakT;
    tDur = tEnd - tStart;
    qrsDur = qDur + rDur + sDur;
    this.nextR = nextR;
    this.nextRt = nextRt;
    rrDiff = nextRt - rPeakT;
  }

  public double getpStart() {
    return pStart;
  }

  public double getpPeak() {
    return pPeak;
  }

  public double getpEnd() {
    return pEnd;
  }

  public double getpPeakT() {
    return pPeakT;
  }

  public double getpDur() {
    return pDur;
  }

  public double getqStart() {
    return qStart;
  }

  public double getqPeak() {
    return qPeak;
  }

  public double getqPeakT() {
    return qPeakT;
  }

  public double getqDur() {
    return qDur;
  }

  public double getrStart() {
    return rStart;
  }

  public double getrPeak() {
    return rPeak;
  }

  public double getrPeakT() {
    return rPeakT;
  }

  public double getrDur() {
    return rDur;
  }

  public double getsStart() {
    return sStart;
  }

  public double getsPeak() {
    return sPeak;
  }

  public double getsEnd() {
    return sEnd;
  }

  public double getsPeakT() {
    return sPeakT;
  }

  public double getsDur() {
    return sDur;
  }

  public double gettStart() {
    return tStart;
  }

  public double gettPeak() {
    return tPeak;
  }

  public double gettEnd() {
    return tEnd;
  }

  public double gettPeakT() {
    return tPeakT;
  }

  public double gettDur() {
    return tDur;
  }

  public double getQrsDur() {
    return qrsDur;
  }

  public double getNextR() {
    return nextR;
  }

  public double getNextRt() {
    return nextRt;
  }

  public double getRrDiff() {
    return rrDiff;
  }

}
