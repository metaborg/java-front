include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Stratego-Java-15.def \
  Stratego-Java-15.tbl 

SDFINCLUDES  = -Idef $(top_builddir)/syn/v1.5/Java-15-Prefixed.def
PGEN_FLAGS   = -m $*

sdfdata_DATA = $(TBLS_DEFS)
EXTRA_DIST   = Stratego-Java-15.sdf
CLEANFILES   = $(TBLS_DEFS)
