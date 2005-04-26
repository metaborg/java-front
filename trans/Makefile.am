include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

libexec_PROGRAMS   = core-lift-eblocks

EXTRA_DIST = $(wildcard *.str) $(wildcard *.meta)
CLEANFILES = $(wildcard *.dep) $(wildcard *.c)

SCFLAGS      = --main io-$*
STRINCLUDES  = -I $(srcdir) \
               -I $(XTC)/share/xtc \
               -I $(top_builddir)/sig 

nodist_core_lift_eblocks_SOURCES = core-lift-eblocks.c
