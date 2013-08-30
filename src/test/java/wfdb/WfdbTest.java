/* File: example1.java       I. Henry    February 18 2005
			Last revised:	 20 August 2010 (ICH)
_______________________________________________________________________________
Java translation of example1.c from the WFDB Programmer's Guide 
Copyright (C) 2010 Isaac C. Henry

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU Library General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option) any
later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Library General Public License for more
details.

You should have received a copy of the GNU Library General Public License along
with this library; if not, write to the Free Software Foundation, Inc., 59
Temple Place - Suite 330, Boston, MA 02111-1307, USA.

You may contact the author by e-mail (ihenry@physionet.org) or postal mail
(MIT Room E25-505A, Cambridge, MA 02139 USA).  For updates to this software,
please visit PhysioNet (http://www.physionet.org/).
_______________________________________________________________________________
 */

package wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class WfdbTest {
	
	public void readAndWriteAnnotation() {
		WFDB_AnninfoArray an = new WFDB_AnninfoArray(2);
		String record = "100", iann = "100", oann = "EGG";
		WFDB_Annotation annot = new WFDB_Annotation();
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
			System.in));

		WFDB_Anninfo a = an.getitem(0);
		a.setName(iann);
		a.setStat(wfdb.WFDB_READ);
		an.setitem(0, a);
		a = an.getitem(1);
		a.setName(oann);
		a.setStat(wfdb.WFDB_WRITE);
		an.setitem(1, a);

		if (wfdb.annopen(record, an.cast(), 2) < 0)
			System.exit(1);
		while (wfdb.getann(0, annot) == 0)
			if (wfdb.wfdb_isqrs(annot.getAnntyp()) != 0) {
				annot.setAnntyp(wfdb.NORMAL);
				if (wfdb.putann(0, annot) < 0)
					break;
			}

			wfdb.wfdbquit();
		}

		public void test10(String[] argv) {
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

		public void test2(String[] argv) {

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

		public void test3(String[] argv) {
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
