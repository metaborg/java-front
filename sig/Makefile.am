include $(top_srcdir)/Makefile.xt

SIGS = \
  Java-15.str \
  Basic-Java.str \
  Generic-Java.str \
  Java-EBlock.str

PSIGS =  \
  Java-15.rtree \
  Basic-Java.rtree \
  Generic-Java.rtree \
  Java-EBlock.rtree

pkgdata_DATA = $(SIGS) $(PSIGS)
CLEANFILES   = $(SIGS) $(PSIGS) Basic-Java.rtg Generic-Java.rtg Java-EBlock.rtg

Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/v1.5/Java-15.rtg $@

Basic-Java.rtg :
	$(LN_S) $(top_builddir)/syn/basic/Basic-Java.rtg $@

Generic-Java.rtg :
	$(LN_S) $(top_builddir)/syn/generic/Generic-Java.rtg $@

Java-EBlock.rtg :
	$(LN_S) $(top_builddir)/syn/eblock/Java-EBlock.rtg $@

