// File: example7.java       I. Henry    February 18 2005
//
// Java translation of example7.c from the WFDB Programmer's Guide
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)
package wfdb;

public class example7 {

	public static void main(final String argv[]) {
		double c[];
		double vv;
		int i, j, nc = argv.length - 4, nsig, nsamp, t;

		if (argv.length < 3) {
			System.out
					.println("usage: example7 record start duration [ coefficients ... ]");
			// Unlike C programs, Java programs do not have any foolproof way
			// to discover their own names, so the name is given as a constant
			// above. The usage statement is correct if this file has been
			// compiled. The command needed to run this program within a JVM
			// is platform-dependent and likely to be more complex.
			System.exit(1);
		}
		if (nc < 1) {
			nc = 1;
			c = new double[1];
			c[0] = 1.0;
		} else {
			c = new double[nc];
		}
		for (i = 0; i < nc; i++) {
			c[i] = Double.parseDouble(argv[i + 3]);
		}
		if ((nsig = wfdb.isigopen(argv[0], null, 0)) < 1) {
			System.exit(3);
		}
		final WFDB_SiginfoArray s = new WFDB_SiginfoArray(nsig);
		final WFDB_SampleArray v = new WFDB_SampleArray(nsig);
		if (wfdb.isigopen(argv[0], s.cast(), nsig) != nsig) {
			System.exit(3);
		}
		if (wfdb.isigsettime(wfdb.strtim(argv[1])) < 0) {
			System.exit(4);
		}
		if ((nsamp = wfdb.strtim(argv[2])) < 1) {
			System.out.println("example7: inappropriate value for duration");
			System.exit(5);
		}
		if (wfdb.osigopen("16l", s.cast(), nsig) != nsig) {
			System.exit(6);
		}

		wfdb.sample(0, 0);
		for (t = 0; t < nsamp && wfdb.sample_valid() == 1; t++) {
			for (j = 0; j < nsig; j++) {
				for (i = 0, vv = 0.; i < nc; i++) {
					if (c[i] != 0.) {
						vv += c[i] * wfdb.sample(j, t + i);
					}
				}
				v.setitem(j, (int) vv);
			}
			if (wfdb.putvec(v.cast()) < 0) {
				break;
			}
		}

		wfdb.newheader("out");
		wfdb.wfdbquit();
	}
}
