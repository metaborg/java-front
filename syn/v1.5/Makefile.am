include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Java-15.def \
  Java-15.tbl

sdfdata_DATA   = $(TBLS_DEFS) $(JAVA15_MODS)
pkgdata_DATA   = Java-15.rtg

EXTRA_DIST = $(sdfdata_DATA)
CLEANFILES = $(TBLS_DEFS) Java-15.rtg

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-15

Java-15.def : $(JAVA15_MODS)

JAVA15_MODS = \
  Java-15.sdf \
  Java-Classes.sdf \
  Java-Comments.sdf \
  Java-Expressions.sdf \
  Java-Identifiers.sdf \
  Java-Keywords.sdf \
  Java-Lexical.sdf \
  Java-Literals.sdf \
  Java-MethodDecs.sdf \
  Java-Modifiers.sdf \
  Java-Names.sdf \
  Java-Statements.sdf \
  Java-TypeDecs.sdf \
  Java-Types.sdf \
  Java-VarDecs.sdf \
  Java-WhiteSpace.sdf \
  $(wildcard languages/java-15/pacakges/*.sdf) \
  $(wildcard languages/java-15/classes/*.sdf) \
  $(wildcard languages/java-15/interfaces/*.sdf)
