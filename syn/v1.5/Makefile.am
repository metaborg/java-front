include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Java-15.def \
  Java-15.tbl

sdfdata_DATA   = $(TBLS_DEFS) $(JAVA15_MODS)
# pkgdata_DATA   = Java-15.rtg

EXTRA_DIST = $(sdfdata_DATA)
CLEANFILES = $(TBLS_DEFS)

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-15

Java-15.def : $(JAVA15_MODS)

JAVA15_MODS = \
  Java-15.sdf \
  Java-Classes.sdf \
  Java-Expressions.sdf \
  Java-Statements.sdf \
  Java-VarDecs.sdf \
  $(wildcard languages/java-15/lexical/*.sdf) \
  $(wildcard languages/java-15/types/*.sdf) \
  $(wildcard languages/java-15/packages/*.sdf) \
  $(wildcard languages/java-15/classes/*.sdf) \
  $(wildcard languages/java-15/interfaces/*.sdf)
