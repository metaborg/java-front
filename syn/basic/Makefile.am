include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS      = Java.tbl Stratego-Java.tbl Java.def Stratego-Java.def

sdfdata_DATA   = $(TBLS_DEFS) $(wildcard *.sdf) Java-Prefixed.sdf
pkgdata_DATA   = Java.rtg

EXTRA_DIST     = $(sdfdata_DATA)
BOOTCLEANFILES = $(TBLS_DEFS) Java-Prefixed.sdf

SDFINCLUDES    = -I $(STRATEGO_FRONT)/share/sdf/stratego-front
PGEN_FLAGS     = -m $*

SDF2RTG_FLAGS  = --main Java

Java.def : \
  Java.sdf \
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

Stratego-Java.def : \
  Java-Prefixed.sdf \
  Stratego-Java.sdf

Java-Prefixed.sdf: Java.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Java --name Java-Prefixed --prefix Java
