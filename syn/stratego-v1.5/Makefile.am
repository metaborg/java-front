include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Stratego-Java-15.def \
  Stratego-Java-15.tbl \
  Java-15-in-Java-15.def \
  Java-15-in-Java-15.tbl

SDFINCLUDES  = -Idef $(top_builddir)/syn/v1.5/Java-15-Prefixed.def
PGEN_FLAGS   = -m $*

sdfdata_DATA = $(TBLS_DEFS) Embedded-Java-15.sdf AmbEmbedded-Java-15.sdf AmbEmbedded-Java-15-quotations.sdf Java-15-in-Java-15.sdf

pkgdata_DATA = Java-15-in-Java-15.rtg

EXTRA_DIST   = Stratego-Java-15.sdf Embedded-Java-15.sdf AmbEmbedded-Java-15.sdf AmbEmbedded-Java-15-quotations.sdf Java-15-in-Java-15.sdf

CLEANFILES   = $(TBLS_DEFS) $(wildcard *.dep)

SDF2RTG_FLAGS  = --main Java-15-in-Java-15
