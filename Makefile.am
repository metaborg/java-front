include $(top_srcdir)/Makefile.xt

SUBDIRS = news-archive syn sig pp trans xtc test
pkgdata_DATA = java-front.m4

BOOTCLEAN_SUBDIRS = $(SUBDIRS)
DIST_SUBDIRS      = $(SUBDIRS)
EXTRA_DIST        = $(pkgdata_DATA) Makefile.xt java-front.spec java-front.spec.in
CLEANFILES        = XTC

XTC_IMPORT = $(STRATEGOXT)/share/strategoxt/XTC

fixeddist : dist
	mv $(PACKAGE)-$(VERSION).tar.gz $(PACKAGE).tar.gz

DISTCHECK_CONFIGURE_FLAGS = \
  --with-strategoxt=$(STRATEGOXT) \
  --with-sglr=$(SGLR) \
  --with-pgen=$(PGEN) \
  --with-pt-support=$(PT_SUPPORT) \
  --with-asf-library=$(ASF_LIBRARY) \
  --with-aterm=$(ATERM)
