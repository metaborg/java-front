include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/Makefile.regular

SIGS = \
  Java.str \
  Generic-Java.str \
  Java-EBlock.str

PSIGS =  \
  Java.rtree \
  Generic-Java.rtree \
  Java-EBlock.rtree

pkgdata_DATA = $(SIGS) $(PSIGS)
EXTRA_DIST   = $(SIGS) $(PSIGS)

BOOTCLEANFILES = $(pkgdata_DATA) $(SIGS) $(PSIGS) Java.rtg-nf Generic-Java.rtg-nf Java-EBlock.rtg-nf

Java.rtg-nf :
	$(LN_S) $(top_srcdir)/syn/basic/Java.rtg-nf $@

Generic-Java.rtg-nf :
	$(LN_S) $(top_srcdir)/syn/generic/Generic-Java.rtg-nf $@

Java-EBlock.rtg-nf :
	$(LN_S) $(top_srcdir)/syn/eblock/Java-EBlock.rtg-nf $@
