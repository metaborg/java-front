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
CLEANFILES   = $(SIGS) $(PSIGS) Java.rtg Generic-Java.rtg Java-EBlock.rtg

Java.rtg :
	$(LN_S) $(top_builddir)/syn/basic/Java.rtg $@

Generic-Java.rtg :
	$(LN_S) $(top_builddir)/syn/generic/Generic-Java.rtg $@

Java-EBlock.rtg :
	$(LN_S) $(top_builddir)/syn/eblock/Java-EBlock.rtg $@
