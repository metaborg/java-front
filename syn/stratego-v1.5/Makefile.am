include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = EmbeddedJavaMix.def Stratego-Java-15.def Stratego-Java-15.tbl

SDFINCLUDES = -I .  -Idef $(top_builddir)/syn/v1.5/Java-15.def -Idef $(top_builddir)/syn/v1.5/Java-15-Prefixed.def
PGEN_FLAGS  = -m $*

sdfdata_DATA = $(TBLS_DEFS) 
EXTRA_DIST   = $(sdfdata_DATA) $(MODS) $(MIX) EmbeddedJava.def
CLEANFILES   = $(TBLS_DEFS) $(MIX) EmbeddedJava.def $(wildcard *.dep)

MODS = Stratego-Java-15.sdf Embedded-Java-15.sdf languages/java/EmbeddedJava.sdf
MIX  = languages/java/EmbeddedJavaMix.sdf

$(MIX) : EmbeddedJava.def
	$(SDF_TOOLS)/bin/gen-sdf-mix -i $< --main languages/java/EmbeddedJava --name languages/java/EmbeddedJavaMix -o $@

EmbeddedJava.def : languages/java/EmbeddedJava.sdf
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ $(SDFINCLUDES)

EmbeddedJavaMix.def : $(MIX)
	$(SDF_FRONT)/bin/pack-sdf -i $< -o $@ $(SDFINCLUDES)
