include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS   = jtree2abox
EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep)
BOOTCLEANFILES = $(wildcard *.c)

SCFLAGS        = --main io-$*
STRINCLUDES    = -I $(top_srcdir)/lib \
                 -I $(top_srcdir)/sig \
                 -I $(GPP)/share/sdf/gpp \
                 -I $(GPP)/share/gpp

noinst_jtree2abox_SOURCES = jtree2abox.c
