include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

sdfdata_DATA        = $(TBLS_DEFS)
nobase_sdfdata_DATA = $(MODS) $(GENMODS)
pkgdata_DATA        = $(RTGS) $(SIGS)

EXTRA_DIST = $(MODS) $(GENMODS) $(TBLS_DEFS) $(DIST_DEFS) $(RTGS) $(SIGS) 
CLEANFILES =         $(GENMODS) $(TBLS_DEFS) $(DIST_DEFS) $(RTGS) $(SIGS) $(wildcard *.dep)

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main $* --ignore-missing-cons
SDFINCLUDES    = -I $(srcdir)

TBLS_DEFS = \
  Java-15.def \
  Java-15.tbl \
  JavaCompilationUnit-15.def \
  JavaCompilationUnit-15.tbl \
  JavaMix.def \
  EmbeddedJavaMix.def \
  Stratego-Java-15.tbl \
  Java-EBlock.def \
  Java-EBlock.tbl \
  Stratego-Java-EBlock.tbl \
  JavaEBlockMix.def

DIST_DEFS = \
  Stratego-Java-15.def \
  Stratego-Java-EBlock.def \
  EmbeddedJava.def

RTGS = Java-15.rtg Java-EBlock.rtg
SIGS = Java-15.str Java-15.rtree Java-EBlock.str Java-EBlock.rtree
MIX = languages/java-15/JavaMix.sdf
EMBMIX  = languages/java/EmbeddedJavaMix.sdf
EBLOCKMIX = languages/java/eblock/JavaEBlockMix.sdf

GENMODS = $(MIX) $(EMBMIX) $(EBLOCKMIX)

MODS = \
  Java-15.sdf \
  JavaCompilationUnit-15.sdf \
  Stratego-Java-15.sdf \
  Java-EBlock.sdf \
  Stratego-Java-EBlock.sdf \
  $(wildcard languages/java/eblock/*.sdf) \
  $(wildcard languages/java/*.sdf) \
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

# Dependencies

Stratego-Java-15.def : $(EMBMIX) $(MIX)
Strateg-Java-EBlock.def : $(EMBMIX) $(MIX) $(EBLOCKMIX)

# Mix modules

$(MIX): Java-15.def
	$(SDF_TOOLS)/bin/gen-sdf-mix -i $< --main languages/java-15/Main --name languages/java-15/JavaMix -o $@

$(EMBMIX) : EmbeddedJava.def
	$(SDF_TOOLS)/bin/gen-sdf-mix -i $< --main languages/java/EmbeddedJava --name languages/java/EmbeddedJavaMix -o $@

$(EBLOCKMIX) : Java-EBlock.def
	$(SDF_TOOLS)/bin/gen-sdf-mix -i $< --main languages/java/eblock/Main --name languages/java/eblock/JavaEBlockMix -o $@

# Definitions

Stratego-Java-15.def : Stratego-Java-15.sdf
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef $(STRATEGO_FRONT)/share/sdf/stratego-front/StrategoMix.def

Stratego-Java-EBlock.def : Stratego-Java-EBlock.sdf
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef $(STRATEGO_FRONT)/share/sdf/stratego-front/StrategoMix.def

EmbeddedJava.def : languages/java/EmbeddedJava.sdf Java-15.def
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef Java-15.def

JavaMix.def : $(MIX) Java-15.def
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef Java-15.def

EmbeddedJavaMix.def : $(EMBMIX) EmbeddedJava.def
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef EmbeddedJava.def

JavaEBlockMix.def : $(EBLOCKMIX) Java-EBlock.def
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ -Idef Java-EBlock.def
