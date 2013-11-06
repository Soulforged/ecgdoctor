// File: example5.java       I. Henry    February 18 2005
//
// Java translation of example5.c from the WFDB Programmer's Guide
// 
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)
package wfdb;

import java.io.*;
import wfdb.*;

public class example5 {
	
    public static void main(String argv[]) {
	int i, nsig;

	if (argv.length < 1) {
	    System.out.println("usage: example5 record");
	    // Unlike C programs, Java programs do not have any foolproof way
	    // to discover their own names, so the name is given as a constant
	    // above.  The usage statement is correct if this file has been
	    // compiled.  The command needed to run this program within a JVM
	    // is platform-dependent and likely to be more complex.
	    System.exit(1);
	}
	nsig = wfdb.isigopen(argv[0], null, 0);
	if (nsig < 1) System.exit(2);
	WFDB_SiginfoArray s = new WFDB_SiginfoArray(nsig);

	if (wfdb.isigopen(argv[0], s.cast(), nsig) != nsig) System.exit(2);
	System.out.println("Record " + argv[0]);
	System.out.println("Starting time: " + wfdb.timstr(0));
	System.out.println("Sampling frequency: " +
			    wfdb.sampfreq(argv[0]) + " Hz");
	System.out.println(nsig + " signals");
	for (i = 0; i < nsig; i++) {
	    WFDB_Siginfo s_i_ = s.getitem(i);
	    System.out.println("Group " + s_i_.getGroup() +
			       ", Signal " + i + ":");
	    System.out.println(" File: " + s_i_.getFname());
	    System.out.println(" Description: " + s_i_.getDesc());
	    System.out.print(" Gain: ");
	    if (s_i_.getGain() == 0.)
		System.out.print("uncalibrated; assume " + wfdb.WFDB_DEFGAIN);
	    else System.out.print(s_i_.getGain());
	    System.out.println(" adu/" +
			   (s_i_.getUnits() != null ? s_i_.getUnits() : "mV"));
	    System.out.println(" Initial value: " + s_i_.getInitval());
	    System.out.println(" Storage format: " + s_i_.getFmt());
	    System.out.print(" I/O: ");
	    if (s_i_.getBsize() == 0) System.out.println("can be unbuffered");
	    else System.out.println(s_i_.getBsize() + "-byte blocks");
	    System.out.println(" ADC resolution: " + s_i_.getAdcres() +
			       " bits");
	    System.out.println(" ADC zero: " + s_i_.getAdczero());
	    if (s_i_.getNsamp() > 0) {
		System.out.println(" Length: " + wfdb.timstr(s_i_.getNsamp()) +
				" (" + s_i_.getNsamp() + " sample intervals)");
		System.out.println(" Checksum: " + s_i_.getCksum());
	    }	
	    else System.out.println(" Length undefined");
	}
	wfdb.wfdbquit();
    }
}
