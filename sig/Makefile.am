include $(top_srcdir)/Makefile.xt

SIGS = \
  Java.str \
  Java-15.str \
  Generic-Java.str \
  Java-EBlock.str

PSIGS =  \
  Java.rtree \
  Java-15.rtree \
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

Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/v1.5/Java-15.rtg $@