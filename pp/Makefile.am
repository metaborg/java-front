include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

bin_PROGRAMS   = jtree2abox
EXTRA_DIST     = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES     = $(wildcard *.dep)
BOOTCLEANFILES = $(wildcard *.c)

SCFLAGS        = --main io-$*
STRINCLUDES    = -I $(top_srcdir)/lib \
                 -I $(top_srcdir)/sig/basic \
                 -I $(top_srcdir)/sig/generic \
                 -I $(GPP)/share/sdf/box-tools

# MUSTDO: replace box-tools with gpp
