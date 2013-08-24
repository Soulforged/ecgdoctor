// File: example3.java       I. Henry    February 18 2005
//
// Java translation of example3.c from the WFDB Programmer's Guide
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)

package wfdb;

import java.io.*;
import wfdb.*;

public class example3 {
	
    public static void main(String argv[]) {
	WFDB_Anninfo a = new WFDB_Anninfo();
	WFDB_Annotation annot = new WFDB_Annotation();

	if (argv.length < 2) {
	    System.out.println( "usage: example3 annotator record");
	    // Unlike C programs, Java programs do not have any foolproof way
	    // to discover their own names, so the name is given as a constant
	    // above.  The usage statement is correct if this file has been
	    // compiled.  The command needed to run this program within a JVM
	    // is platform-dependent and likely to be more complex.
	    System.exit(1);
	}
	a.setName(argv[0]); a.setStat(wfdb.WFDB_READ);
	wfdb.sampfreq(argv[1]);
	if (wfdb.annopen(argv[1], a, 1) < 0) System.exit(2); 
	while (wfdb.getann(0, annot) == 0)
	    System.out.println(wfdb.timstr(-annot.getTime()) + 
			       " (" + annot.getTime() + ") " +
			       wfdb.annstr(annot.getAnntyp()) + " "+
			       annot.getSubtyp() + " " +
			       annot.getChan() + " " + 
			       annot.getNum() + " " +
			       (annot.getAux() == null ? "" :
				annot.getAux().substring(1)));		
	wfdb.wfdbquit();
    }
}
