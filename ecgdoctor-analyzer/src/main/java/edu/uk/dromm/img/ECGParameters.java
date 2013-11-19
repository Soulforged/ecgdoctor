package edu.uk.dromm.img;

/**
 * 
 * @author dicardo
 * 
 */
public class ECGParameters {

	public double qrs = 0.0;
	public double pt = 0.0;
	public double pv = 0.0;
	public double p = 0.0;
	public double pr = 0.0;
	public double t = 0.0;
	public double rr = 0.0;

	public ECGParameters(final double qrs, final double pt, final double pv,
			final double p, final double pr, final double t, final double rr) {
		super();
		this.qrs = qrs;
		this.pt = pt;
		this.pv = pv;
		this.p = p;
		this.pr = pr;
		this.t = t;
		this.rr = rr;
	}
}
