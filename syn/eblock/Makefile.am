include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

MODULES = \
  Java-EBlock.sdf \
  Stratego-Java-EBlock.sdf

TBLS_DEFS = \
  Java-EBlock.def \
  Java-EBlock.tbl \
  Stratego-Java-EBlock.def \
  Stratego-Java-EBlock.tbl 

sdfdata_DATA = $(TBLS_DEFS) Java-EBlock-Prefixed.sdf
pkgdata_DATA = Java-EBlock.rtg Java-EBlock.str Java-EBlock.rtree

EXTRA_DIST = $(MODULES) $(sdfdata_DATA) $(pkgdata_DATA)
CLEANFILES = $(TBLS_DEFS) Java-EBlock.rtg Java-EBlock-Prefixed.sdf $(wildcard *.dep)

SDFINCLUDES = \
  -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
  -I $(srcdir) \
  -I $(top_builddir)/syn/eblock \
  -Idef $(top_builddir)/syn/v1.5/Java-15.def \
  -Idef $(top_builddir)/syn/stratego-v1.5/Stratego-Java-15.def

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-EBlock

Stratego-Java-EBlock.def : Java-EBlock-Prefixed.sdf 

Java-EBlock-Prefixed.sdf: Java-EBlock.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Java-EBlock --name Java-EBlock-Prefixed --prefix Java
