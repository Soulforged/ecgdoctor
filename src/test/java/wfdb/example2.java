// File: example2.java       I. Henry    February 18 2005
//			     Last revised:	6 April 2006 (GBM)
// Java translation of example2.c from the WFDB Programmer's Guide
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)
package wfdb;

import java.io.*;
import wfdb.*;

public class example2 {
	
    
    public static void main(String argv[]) {
		
	WFDB_AnninfoArray an = new WFDB_AnninfoArray(2);
	WFDB_Annotation annot = new WFDB_Annotation();

	if (argv.length < 1) {
	    System.out.println("usage: example2 record");
	    // Unlike C programs, Java programs do not have any foolproof way
	    // to discover their own names, so the name is given as a constant
	    // above.  The usage statement is correct if this file has been
	    // compiled.  The command needed to run this program within a JVM
	    // is platform-dependent and likely to be more complex.
	    System.exit(1);
	}
	WFDB_Anninfo a = an.getitem(0);
	a.setName("atr"); a.setStat(wfdb.WFDB_READ);
	an.setitem(0, a);
	a = an.getitem(1);
	a.setName("aha"); a.setStat(wfdb.WFDB_AHA_WRITE);
	an.setitem(1, a);
	if (wfdb.annopen(argv[0], an.cast(), 2) < 0) System.exit(2);
	while (wfdb.getann(0, annot) == 0 && wfdb.putann(0, annot) == 0)
	    ;
	wfdb.wfdbquit();
    }
}
