include $(top_srcdir)/Makefile.xt

SUBDIRS           = syn sig pp test xtc

BOOTCLEAN_SUBDIRS = $(SUBDIRS)
DIST_SUBDIRS      = $(SUBDIRS)
EXTRA_DIST        = Makefile.xt java-front.spec java-front.spec.in

XTC_IMPORT = $(STRATEGOXT)/share/strategoxt/XTC

fixeddist : dist
	mv $(PACKAGE)-$(VERSION).tar.gz $(PACKAGE).tar.gz