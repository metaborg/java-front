include $(top_srcdir)/Makefile.xt
include $(wildcard *.dep)

TBLS_DEFS = \
  Generic-Java.def \
  Generic-Java.tbl \
  Stratego-Generic-Java.def \
  Stratego-Generic-Java.tbl

sdfdata_DATA   = $(TBLS_DEFS) Generic-Java-Prefixed.sdf
pkgdata_DATA   = Generic-Java.rtg

EXTRA_DIST     = $(sdfdata_DATA)
CLEANFILES     = $(TBLS_DEFS) Generic-Java-Prefixed.sdf Generic-Java.rtg

SDFINCLUDES    = -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
                 -I ${top_srcdir}/syn/basic
PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Generic-Java

Generic-Java.def : $(BASICJAVA_MODS) Generic-Java.sdf

Stratego-Generic-Java.def : \
  Generic-Java.def \
  Generic-Java-Prefixed.sdf \
  Stratego-Generic-Java.sdf \
  ${top_srcdir}/syn/basic/Stratego-Basic-Java.sdf

Generic-Java-Prefixed.sdf: Generic-Java.def
	$(SDF_TOOLS)/bin/gen-renamed-sdf-module -i $< -o $@ --main Generic-Java --name Generic-Java-Prefixed --prefix Java

BASICJAVA_MODS = \
  ${top_srcdir}/syn/basic/Basic-Java.sdf \
  ${top_srcdir}/syn/basic/Java-Classes.sdf \
  ${top_srcdir}/syn/basic/Java-Comments.sdf \
  ${top_srcdir}/syn/basic/Java-Expressions.sdf \
  ${top_srcdir}/syn/basic/Java-Identifiers.sdf \
  ${top_srcdir}/syn/basic/Java-Interfaces.sdf \
  ${top_srcdir}/syn/basic/Java-Keywords.sdf \
  ${top_srcdir}/syn/basic/Java-Lexical.sdf \
  ${top_srcdir}/syn/basic/Java-Literals.sdf \
  ${top_srcdir}/syn/basic/Java-MethodDecs.sdf \
  ${top_srcdir}/syn/basic/Java-Modifiers.sdf \
  ${top_srcdir}/syn/basic/Java-Names.sdf \
  ${top_srcdir}/syn/basic/Java-Statements.sdf \
  ${top_srcdir}/syn/basic/Java-TypeDecs.sdf \
  ${top_srcdir}/syn/basic/Java-Types.sdf \
  ${top_srcdir}/syn/basic/Java-VarDecs.sdf \
  ${top_srcdir}/syn/basic/Java-WhiteSpace.sdf 
