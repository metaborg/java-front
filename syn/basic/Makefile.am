include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Basic-Java.def \
  Basic-Java.tbl \
  Stratego-Basic-Java.def \
  Stratego-Basic-Java.tbl

sdfdata_DATA   = $(TBLS_DEFS) Basic-Java-Prefixed.sdf
pkgdata_DATA   = Basic-Java.rtg 

EXTRA_DIST     = $(BASIC_MODULES) $(STRATEGO_BASIC_MODULES)
CLEANFILES     = $(TBLS_DEFS) Basic-Java-Prefixed.sdf Basic-Java.rtg

SDFINCLUDES = \
  -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
  -I $(top_builddir)/syn/basic

PGEN_FLAGS    = -m $*
SDF2RTG_FLAGS = --main Basic-Java

BASIC_MODULES = \
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

STRATEGO_BASIC_MODULES = \
  Stratego-Basic-Java.sdf

Basic-Java.def : $(BASIC_MODULES)

Stratego-Basic-Java.def : \
  Basic-Java-Prefixed.sdf \
  $(STRATEGO_BASIC_MODULES)

Basic-Java-Prefixed.sdf: Basic-Java.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Basic-Java --name Basic-Java-Prefixed --prefix Java
