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
pkgdata_DATA = Java-EBlock.rtg

EXTRA_DIST = $(MODULES)
CLEANFILES = $(TBLS_DEFS) Java-EBlock.rtg Java-EBlock-Prefixed.sdf

SDFINCLUDES = \
  -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
  -I $(srcdir) \
  -I $(top_builddir)/syn/eblock \
  -I $(top_builddir)/syn/basic \
  -I $(top_builddir)/syn/generic \
  -I $(top_srcdir)/syn/basic \
  -I $(top_srcdir)/syn/generic

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-EBlock

Java-EBlock.def : \
  $(top_srcdir)/syn/basic/Basic-Java.sdf \
  $(top_srcdir)/syn/basic/Java-Classes.sdf \
  $(top_srcdir)/syn/basic/Java-Comments.sdf \
  $(top_srcdir)/syn/basic/Java-Expressions.sdf \
  $(top_srcdir)/syn/basic/Java-Identifiers.sdf \
  $(top_srcdir)/syn/basic/Java-Interfaces.sdf \
  $(top_srcdir)/syn/basic/Java-Keywords.sdf \
  $(top_srcdir)/syn/basic/Java-Lexical.sdf \
  $(top_srcdir)/syn/basic/Java-Literals.sdf \
  $(top_srcdir)/syn/basic/Java-MethodDecs.sdf \
  $(top_srcdir)/syn/basic/Java-Modifiers.sdf \
  $(top_srcdir)/syn/basic/Java-Names.sdf \
  $(top_srcdir)/syn/basic/Java-Statements.sdf \
  $(top_srcdir)/syn/basic/Java-TypeDecs.sdf \
  $(top_srcdir)/syn/basic/Java-Types.sdf \
  $(top_srcdir)/syn/basic/Java-VarDecs.sdf \
  $(top_srcdir)/syn/basic/Java-WhiteSpace.sdf \
  $(top_srcdir)/syn/generic/Generic-Java.sdf \
  Java-EBlock.sdf

Stratego-Java-EBlock.def : \
  Java-EBlock.def \
  Java-EBlock-Prefixed.sdf \
  Stratego-Java-EBlock.sdf \
  $(top_srcdir)/syn/generic/Stratego-Generic-Java.sdf

Java-EBlock-Prefixed.sdf: Java-EBlock.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Java-EBlock --name Java-EBlock-Prefixed --prefix Java
