include $(top_srcdir)/Makefile.xt

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

BOOTCLEANFILES = $(pkgdata_DATA) $(SIGS) $(PSIGS) Java.rtg Generic-Java.rtg Java-EBlock.rtg

Java.rtg :
	$(LN_S) $(top_srcdir)/syn/basic/Java.rtg $@

Generic-Java.rtg :
	$(LN_S) $(top_srcdir)/syn/generic/Generic-Java.rtg $@

Java-EBlock.rtg :
	$(LN_S) $(top_srcdir)/syn/eblock/Java-EBlock.rtg $@
