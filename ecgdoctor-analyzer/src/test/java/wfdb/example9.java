// File: example9.java       I. Henry    February 18 2005
//
// Java translation of example9.c from the WFDB Programmer's Guide
//
// Copyright (C) 2005 Isaac C. Henry (ihenry@physionet.org)
package wfdb;

public class example9 {

  public static void main(final String argv[]) {
    int btype, i, j, nbeats = 0, nsig, hwindow, window, stoptime = 0;
    final WFDB_Anninfo a = new WFDB_Anninfo();
    final WFDB_Annotation annot = new WFDB_Annotation();

    if (argv.length < 2) {
      System.out
          .println("usage: example9 annotator record [beat-type from to]");
      // Unlike C programs, Java programs do not have any foolproof way
      // to discover their own names, so the name is given as a constant
      // above. The usage statement is correct if this file has been
      // compiled. The command needed to run this program within a JVM
      // is platform-dependent and likely to be more complex.
      System.exit(1);
    }
    a.setName(argv[0]);
    a.setStat(wfdbConstants.WFDB_READ);
    if ((nsig = wfdb.isigopen(argv[1], null, 0)) < 1)
      System.exit(2);
    final WFDB_SiginfoArray s = new WFDB_SiginfoArray(nsig);
    final WFDB_SampleArray v = new WFDB_SampleArray(nsig);
    final WFDB_SampleArray vb = new WFDB_SampleArray(nsig);
    if (wfdb.wfdbinit(argv[1], a, 1, s.cast(), nsig) != nsig)
      System.exit(3);
    hwindow = wfdb.strtim(".05");
    window = 2 * hwindow + 1;
    final long sum[][] = new long[nsig][window];
    btype = argv.length > 2 ? wfdb.strann(argv[2]) : wfdbConstants.NORMAL;
    if (argv.length > 3)
      wfdb.iannsettime(wfdb.strtim(argv[3]));
    final WFDB_Siginfo s_0_ = s.getitem(0);
    if (argv.length > 4) {
      if ((stoptime = wfdb.strtim(argv[4])) < 0)
        stoptime = -stoptime;
      if (s_0_.getNsamp() > 0 && stoptime > s_0_.getNsamp())
        stoptime = s_0_.getNsamp();
    } else
      stoptime = s_0_.getNsamp();
    if (stoptime > 0)
      stoptime -= hwindow;
    while (wfdb.getann(0, annot) == 0 && annot.getTime() < hwindow)
      do {
        if (annot.getAnntyp() != btype)
          continue;
        wfdb.isigsettime(annot.getTime() - hwindow - 1);
        wfdb.getvec(vb.cast());
        for (j = 0; j < window && wfdb.getvec(v.cast()) > 0; j++)
          for (i = 0; i < nsig; i++)
            sum[i][j] += v.getitem(i) - vb.getitem(i);
        nbeats++;
      } while (wfdb.getann(0, annot) == 0
          && (stoptime == 0L || annot.getTime() < stoptime));
    if (nbeats < 1) {
      System.out.println("example9: no `" + wfdb.annstr(btype)
          + "' beats found");
      System.exit(4);
    }
    System.out.println("Average of " + nbeats + " `" + wfdb.annstr(btype)
        + "' beats:");
    for (j = 0; j < window; j++)
      for (i = 0; i < nsig; i++)
        System.out.print((double) sum[i][j] / nbeats
            + (i < nsig - 1 ? "\t" : "\n"));
    wfdb.wfdbquit();
  }
}
