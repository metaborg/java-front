include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Java-15.def \
  Java-15.tbl \
  JavaCompilationUnit-15.def \
  JavaCompilationUnit-15.tbl \
  Java-15-Prefixed.def

sdfdata_DATA   = $(TBLS_DEFS) Java-15-Prefixed.sdf
pkgdata_DATA   = Java-15.rtg

EXTRA_DIST = $(JAVA15_MODS)
CLEANFILES = $(TBLS_DEFS) Java-15.rtg Java-15-Prefixed.sdf $(wildcard *.dep)

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-15
SDFINCLUDES    = -I $(srcdir)

JavaCompilationUnit-15.def : $(JAVA15_MODS)
Java-15.def : $(JAVA15_MODS)

JAVA15_MODS = \
  Java-15.sdf \
  JavaCompilationUnit-15.sdf \
  $(wildcard languages/java-15/*.sdf) \
  $(wildcard languages/java-15/lexical/*.sdf) \
  $(wildcard languages/java-15/lexical/literals/*.sdf) \
  $(wildcard languages/java-15/names/*.sdf) \
  $(wildcard languages/java-15/types/*.sdf) \
  $(wildcard languages/java-15/packages/*.sdf) \
  $(wildcard languages/java-15/classes/*.sdf) \
  $(wildcard languages/java-15/interfaces/*.sdf) \
  $(wildcard languages/java-15/arrays/*.sdf) \
  $(wildcard languages/java-15/statements/*.sdf) \
  $(wildcard languages/java-15/expressions/*.sdf)

Java-15-Prefixed.sdf: Java-15.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Java-15 --name Java-15-Prefixed --prefix Java
