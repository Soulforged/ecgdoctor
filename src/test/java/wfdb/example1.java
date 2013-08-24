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

public class example1 {
	@Test
	public void testAlgo() {
		WFDB_AnninfoArray an = new WFDB_AnninfoArray(2);
		String record = null, iann = null, oann = null;
		WFDB_Annotation annot = new WFDB_Annotation();
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));

		try {
			System.out.print("Type record name: ");
			record = stdin.readLine();
			System.out.print("Type input annotator name: ");
			iann = stdin.readLine();
			System.out.print("Type output annotator name: ");
			oann = stdin.readLine();
		} catch (IOException e) {
			System.out.println("IO Exception");
		}

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
}
