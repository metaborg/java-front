include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/Makefile.regular

pkgdata_DATA = Java.rtree Generic-Java.rtree

EXTRA_DIST = Java.rtree Java.str Generic-Java.rtree Generic-Java.str
BOOTCLEANFILES = $(pkgdata_DATA) Java.rhg-nf Generic-Java.rhg-nf 

Java.rtg-nf :
	$(LN_S) $(top_srcdir)/syn/basic/Java.rtg-nf $@

Generic-Java.rtg-nf :
	$(LN_S) $(top_srcdir)/syn/generic/Generic-Java.rtg-nf $@

