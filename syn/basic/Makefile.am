include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Basic-Java.def \
  Basic-Java.tbl \
  Stratego-Basic-Java.def \
  Stratego-Basic-Java.tbl

sdfdata_DATA   = $(TBLS_DEFS) Basic-Java-Prefixed.sdf
pkgdata_DATA   = Basic-Java.rtg

EXTRA_DIST     = $(sdfdata_DATA)
CLEANFILES     = $(TBLS_DEFS) Basic-Java-Prefixed.sdf Basic-Java.rtg

SDFINCLUDES    = -I $(STRATEGO_FRONT)/share/sdf/stratego-front
PGEN_FLAGS     = -m $*

SDF2RTG_FLAGS  = --main Basic-Java

Basic-Java.def : \
  Basic-Java.sdf \
  Java-Classes.sdf \
  Java-Comments.sdf \
  Java-Expressions.sdf \
  Java-Identifiers.sdf \
  Java-Interfaces.sdf \
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
  Java-WhiteSpace.sdf

Stratego-Basic-Java.def : \
  Basic-Java-Prefixed.sdf \
  Stratego-Basic-Java.sdf

Basic-Java-Prefixed.sdf: Basic-Java.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Basic-Java --name Basic-Java-Prefixed --prefix Java
