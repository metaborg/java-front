include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Java-1.5.def \
  Java-1.5.tbl \
  Generic-Java.def \
  Generic-Java.tbl \
  Stratego-Generic-Java.tbl

sdfdata_DATA   = $(TBLS_DEFS) $(wildcard *.sdf) Generic-Java-Prefixed.sdf
pkgdata_DATA   = Generic-Java.rtg

EXTRA_DIST     = $(sdfdata_DATA)
CLEANFILES = $(TBLS_DEFS) Generic-Java-Prefixed.sdf Generic-Java.rtg

SDFINCLUDES    = -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
                 -I ${top_srcdir}/syn/v1.4
PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Generic-Java

Generic-Java.def : \
  ${top_srcdir}/syn/v1.4/Java.sdf \
  ${top_srcdir}/syn/v1.4/Java-Classes.sdf \
  ${top_srcdir}/syn/v1.4/Java-Comments.sdf \
  ${top_srcdir}/syn/v1.4/Java-Expressions.sdf \
  ${top_srcdir}/syn/v1.4/Java-Identifiers.sdf \
  ${top_srcdir}/syn/v1.4/Java-Interfaces.sdf \
  ${top_srcdir}/syn/v1.4/Java-Keywords.sdf \
  ${top_srcdir}/syn/v1.4/Java-Lexical.sdf \
  ${top_srcdir}/syn/v1.4/Java-Literals.sdf \
  ${top_srcdir}/syn/v1.4/Java-MethodDecs.sdf \
  ${top_srcdir}/syn/v1.4/Java-Modifiers.sdf \
  ${top_srcdir}/syn/v1.4/Java-Names.sdf \
  ${top_srcdir}/syn/v1.4/Java-Statements.sdf \
  ${top_srcdir}/syn/v1.4/Java-TypeDecs.sdf \
  ${top_srcdir}/syn/v1.4/Java-Types.sdf \
  ${top_srcdir}/syn/v1.4/Java-VarDecs.sdf \
  ${top_srcdir}/syn/v1.4/Java-WhiteSpace.sdf \
  Generic-Java.sdf \
  Java-StaticImport.sdf

Stratego-Generic-Java.def : \
  Generic-Java.def \
  Generic-Java-Prefixed.sdf \
  Stratego-Generic-Java.sdf \
  ${top_srcdir}/syn/v1.4/Stratego-Java.sdf

Generic-Java-Prefixed.sdf: Generic-Java.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Generic-Java --name Generic-Java-Prefixed --prefix Java
