include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Stratego-Java-15.def \
  Stratego-Java-15.tbl 

SDFINCLUDES  = -Idef $(top_builddir)/syn/v1.5/Java-15-Prefixed.def
PGEN_FLAGS   = -m $*

sdfdata_DATA = $(TBLS_DEFS) Embedded-Java-15.sdf AmbEmbedded-Java-15.sdf AmbEmbedded-Java-15-quotations.sdf
EXTRA_DIST   = Stratego-Java-15.sdf Embedded-Java-15.sdf AmbEmbedded-Java-15.sdf AmbEmbedded-Java-15-quotations.sdf
CLEANFILES   = $(TBLS_DEFS) $(wildcard *.dep)
