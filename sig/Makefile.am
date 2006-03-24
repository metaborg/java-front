include $(top_srcdir)/Makefile.xt

SIGS = \
  Java-15.str \
  Java-EBlock.str \
  Java-15-in-Java-15.str

PSIGS =  \
  Java-15.rtree \
  Java-EBlock.rtree \
  Java-15-in-Java-15.rtree

pkgdata_DATA = $(SIGS) $(PSIGS)
CLEANFILES   = \
  $(SIGS) $(PSIGS) \
  Java-15.rtg \
  Java-EBlock.rtg \
  Java-15-in-Java-15.rtg

Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/v1.5/Java-15.rtg $@

Java-EBlock.rtg :
	$(LN_S) $(top_builddir)/syn/eblock/Java-EBlock.rtg $@

Java-15-in-Java-15.rtg :
	$(LN_S) $(top_builddir)/syn/stratego-v1.5/Java-15-in-Java-15.rtg $@
