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

sdfdata_DATA = $(TBLS_DEFS) $(wildcard *.sdf) Java-EBlock-Prefixed.sdf
pkgdata_DATA = Java-EBlock.rtg

EXTRA_DIST = $(MODULES)
CLEANFILES = $(TBLS_DEFS) Java-EBlock.rtg Java-EBlock-Prefixed.sdf

SDFINCLUDES = \
  -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
  -I $(srcdir) \
  -I $(top_builddir)/syn/eblock \
  -I $(top_srcdir)/syn/v1.4 \
  -I $(top_srcdir)/syn/v1.5

PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Java-EBlock

Java-EBlock.def : \
  $(top_srcdir)/syn/v1.4/Java.sdf \
  $(top_srcdir)/syn/v1.4/Java-Classes.sdf \
  $(top_srcdir)/syn/v1.4/Java-Comments.sdf \
  $(top_srcdir)/syn/v1.4/Java-Expressions.sdf \
  $(top_srcdir)/syn/v1.4/Java-Identifiers.sdf \
  $(top_srcdir)/syn/v1.4/Java-Interfaces.sdf \
  $(top_srcdir)/syn/v1.4/Java-Keywords.sdf \
  $(top_srcdir)/syn/v1.4/Java-Lexical.sdf \
  $(top_srcdir)/syn/v1.4/Java-Literals.sdf \
  $(top_srcdir)/syn/v1.4/Java-MethodDecs.sdf \
  $(top_srcdir)/syn/v1.4/Java-Modifiers.sdf \
  $(top_srcdir)/syn/v1.4/Java-Names.sdf \
  $(top_srcdir)/syn/v1.4/Java-Statements.sdf \
  $(top_srcdir)/syn/v1.4/Java-TypeDecs.sdf \
  $(top_srcdir)/syn/v1.4/Java-Types.sdf \
  $(top_srcdir)/syn/v1.4/Java-VarDecs.sdf \
  $(top_srcdir)/syn/v1.4/Java-WhiteSpace.sdf \
  $(top_srcdir)/syn/v1.5/Generic-Java.sdf \
  Java-EBlock.sdf

Stratego-Java-EBlock.def : \
  Java-EBlock.def \
  Java-EBlock-Prefixed.sdf \
  Stratego-Java-EBlock.sdf \
  $(top_srcdir)/syn/v1.5/Stratego-Generic-Java.sdf

Java-EBlock-Prefixed.sdf: Java-EBlock.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Java-EBlock --name Java-EBlock-Prefixed --prefix Java
