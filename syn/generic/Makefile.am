include $(top_srcdir)/Makefile.xt
include $(top_srcdir)/Makefile.regular
include $(wildcard *.dep)

TBLS_DEFS      = Generic-Java.tbl Stratego-Generic-Java.tbl \
                 Generic-Java.def Stratego-Generic-Java.def

sdfdata_DATA   = $(TBLS_DEFS) $(wildcard *.sdf)
pkgdata_DATA   = Generic-Java.rhg-nf

EXTRA_DIST     = $(sdfdata_DATA)
BOOTCLEANFILES = $(TBLS_DEFS)

SDFINCLUDES    = -I $(STRATEGO_FRONT)/share/sdf/stratego-front \
                 -I ${top_srcdir}/syn/basic
PGEN_FLAGS     = -m $*
SDF2RTG_FLAGS  = --main Generic-Java

Generic-Java.def : \
  ${top_srcdir}/syn/basic/Java.sdf \
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
  ${top_srcdir}/syn/basic/Java-WhiteSpace.sdf \
  Generic-Java.sdf
  
Stratego-Generic-Java.def : \
  Generic-Java.def \
  Stratego-Generic-Java.sdf \
  ${top_srcdir}/syn/basic/Stratego-Java.sdf
