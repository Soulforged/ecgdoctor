// File: example10.java		I. Henry	February 18 2005
//				Last revised:	6 April 2006 (GBM)
// Java translation of example1.c from the WFDB Programmer's Guide
// 
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)
package wfdb;

import java.io.*;
import wfdb.*;

public class example10 {
	
    public static void main(String argv[]) {
	int filter, time=0, slopecrit, sign=1, maxslope=0, nsig, nslope=0,
	    qtime=0, maxtime=0, t0, t1, t2, t3, t4, t5, t6, t7, t8, t9,
	    ms160, ms200, s2, scmax, scmin = 0;
	WFDB_Anninfo a = new WFDB_Anninfo();
	WFDB_Annotation annot = new WFDB_Annotation();

	if (argv.length < 1) {
	    System.out.println("usage: example10 record [threshold]");
	    // Unlike C programs, Java programs do not have any foolproof way
	    // to discover their own names, so the name is given as a constant
	    // above.  The usage statement is correct if this file has been
	    // compiled.  The command needed to run this program within a JVM
	    // is platform-dependent and likely to be more complex.
	    System.exit(1);
	}
	a.setName("qrs"); a.setStat(wfdb.WFDB_WRITE);
		
	if ((nsig = wfdb.isigopen(argv[0], null, 0)) < 1) System.exit(2);
	WFDB_SiginfoArray s = new WFDB_SiginfoArray(nsig);
	WFDB_SampleArray v = new WFDB_SampleArray(nsig);
	if (wfdb.wfdbinit(argv[0], a, 1, s.cast(), nsig) != nsig)
	    System.exit(2);
	if (wfdb.sampfreq(null) < 240. || wfdb.sampfreq(null) > 260.)
	    wfdb.setifreq(250.);
	if (argv.length > 1) scmin = wfdb.muvadu(0, Integer.parseInt(argv[1]));
	if (scmin < 1) scmin = wfdb.muvadu(0, 1000);
	slopecrit = scmax = 10 * scmin;
	ms160 = wfdb.strtim("0.16"); ms200 = wfdb.strtim("0.2");
	s2 = wfdb.strtim("2");
	annot.setSubtyp(0); annot.setChan(0); annot.setNum(0);
	annot.setAux(null);
	wfdb.getvec(v.cast());
	t9 = t8 = t7 = t6 = t5 = t4 = t3 = t2 = t1 = v.getitem(0);
		
	do {
	    filter = (t0 = v.getitem(0)) + 4*t1 + 6*t2 + 4*t3 + t4
		            - t5         - 4*t6 - 6*t7 - 4*t8 - t9;
	    if (time % s2 == 0) {
		if (nslope == 0) {
		    slopecrit -= slopecrit >> 4;
		    if (slopecrit < scmin) slopecrit = scmin;
		}
		else if (nslope >= 5) {
		    slopecrit += slopecrit >> 4;
		    if (slopecrit > scmax) slopecrit = scmax;
		}
	    }
	    if (nslope == 0 && Math.abs(filter) > slopecrit) {
		nslope = 1; maxtime = ms160;
		sign = (filter > 0) ? 1 : -1;
		qtime = time;
	    }
	    if (nslope != 0) {
		if (filter * sign < -slopecrit) {
		    sign = -sign;
		    maxtime = (++nslope > 4) ? ms200 : ms160;
		}
		else if (filter * sign > slopecrit &&
			 Math.abs(filter) > maxslope)
		    maxslope = Math.abs(filter);
		if (maxtime-- < 0) {
		    if (2 <= nslope && nslope <= 4) {
			slopecrit += ((maxslope>>2) - slopecrit) >> 3;
			if (slopecrit < scmin) slopecrit = scmin;
			else if (slopecrit > scmax) slopecrit = scmax;
			annot.setTime(wfdb.strtim("i") - (time - qtime) - 4);
			annot.setAnntyp(wfdb.NORMAL); wfdb.putann(0, annot);
			time = 0;
		    }
		    else if (nslope >= 5) {
			annot.setTime(wfdb.strtim("i") - (time - qtime) - 4);
			annot.setAnntyp(wfdb.ARFCT); wfdb.putann(0, annot);
		    }
		    nslope = 0;
		}
	    }
	    t9 = t8; t8 = t7; t7 = t6; t6 = t5; t5 = t4;
	    t4 = t3; t3 = t2; t2 = t1; t1 = t0; time++;
	} while (wfdb.getvec(v.cast()) > 0);

	wfdb.wfdbquit();		
    }
}
