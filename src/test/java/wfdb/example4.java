// File: example4.java       I. Henry    February 18 2005
//			Last revised:	11 March 2006 (GBM)
// Java translation of example4.c from the WFDB Programmer's Guide
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)

package wfdb;

import java.io.*;
import wfdb.*;

public class example4 {
	
    
    public static void main(String argv[]) {
	int rr, rrhist[], rrmax, t;
	WFDB_Anninfo a = new WFDB_Anninfo();
	WFDB_Annotation annot = new WFDB_Annotation();
		
	if (argv.length < 2) {
	    System.out.println("usage: example4 annotator record\n");
	    // Unlike C programs, Java programs do not have any foolproof way
	    // to discover their own names, so the name is given as a constant
	    // above.  The usage statement is correct if this file has been
	    // compiled.  The command needed to run this program within a JVM
	    // is platform-dependent and likely to be more complex.
	    System.exit(1);
	}
	a.setName(argv[0]); a.setStat(wfdb.WFDB_READ);
	if (wfdb.annopen(argv[1], a, 1) < 0) System.exit(2);
	if ((rrmax = (int)(3*wfdb.sampfreq(argv[1]))) <= 0)
	    System.exit(3);
	rrhist = new int[rrmax+1];
	while (wfdb.getann(0, annot) == 0 && 
	       wfdb.wfdb_isqrs(annot.getAnntyp()) == 0)
	    ;
	t = annot.getTime();
	while (wfdb.getann(0, annot) == 0)
	    if (wfdb.wfdb_isqrs(annot.getAnntyp()) != 0) {
		if ((rr = annot.getTime() - t) > rrmax) rr = rrmax;
		rrhist[rr]++;
		t = annot.getTime();
	    }
	for (rr = 1; rr < rrmax; rr++)
	    System.out.println(rrhist[rr] + wfdb.mstimstr(rr));
	System.out.println( rrhist[rr] + wfdb.mstimstr(rr) + " (or longer)");
	wfdb.wfdbquit();
    }
}
